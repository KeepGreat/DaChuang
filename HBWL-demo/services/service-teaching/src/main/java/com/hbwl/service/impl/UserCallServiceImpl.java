package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hbwl.feign.EvaluationFeignClient;
import com.hbwl.mapper.UserCallRecordMapper;
import com.hbwl.pojo.UserCallRecord;
import com.hbwl.service.UserCallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserCallServiceImpl implements UserCallService {

    @Autowired
    private UserCallRecordMapper userCallRecordMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EvaluationFeignClient evaluationFeignClient;

    // 统计时间窗口
    private static final long TIME_WINDOW = 3600L; // 秒
    //用于实时记录用户统计时间窗口内调用次数，过期时间即为统计时间窗口，用于限制用户调用次数
    private static final String USER_CALL_COUNT_KEY_PREFIX = "user:call:count:";
    //用于保存用户调用次数，不设置过期时间，由定时任务清理，用于保存和反馈评估模块
    private static final String USER_CALL_DATA_KEY_PREFIX = "user:call:data:";

    //将用户调用次数记录到redis中
    @Override
    public int recordUserCall(String userId) {
        if (userId == null || userId.trim().isEmpty()) return 0;
        try {
            // 获取当前小时的时间戳（整点）
            String currentHour = String.valueOf(getCurrentHourTimestamp());

            // 构建Redis key
            String countKey = buildCountKey(userId, currentHour);
            String dataKey = buildDataKey(userId, currentHour);

            // 使用INCR命令递增计数器（原子操作）
            Long count = stringRedisTemplate.opsForValue().increment(countKey);

            // 如果是第一次设置这个key（count=1），设置过期时间
            if (count != null && count == 1) {
                // 过期时间设为TIME_WINDOW
                stringRedisTemplate.expire(countKey, TIME_WINDOW, TimeUnit.SECONDS);
            }

            // 同时更新数据备份（不过期，由定时任务清理）
            stringRedisTemplate.opsForValue().set(dataKey, String.valueOf(count));

            return 1;
        } catch (Exception e) {
            log.error("记录用户调用次数失败，userId: {}", userId, e);
            return -1;
        }
    }

    //获取指定小时用户调用次数
    //若不传hourTimestamp则获取当前小时用户调用次数
    @Override
    public long getUserCallCount(String userId, Long hourTimestamp) {
        if (userId == null || userId.trim().isEmpty()) return 0;
        try {
            // 构建Redis key
            String countKey;
            if (hourTimestamp == null ) countKey = buildCountKey(userId, String.valueOf(getCurrentHourTimestamp()));
            else countKey = buildCountKey(userId, String.valueOf(hourTimestamp));

            // 获取计数
            String countStr = stringRedisTemplate.opsForValue().get(countKey);

            if (countStr == null || countStr.trim().isEmpty()) {
                return 0;
            }

            return Long.parseLong(countStr);
        } catch (NumberFormatException e) {
            log.error("解析调用次数失败，userId: {}", userId, e);
            return 0L;
        } catch (Exception e) {
            log.error("获取用户调用次数失败，userId: {}", userId, e);
            return 0L;
        }
    }

    //获取用户最近N个小时内的调用统计
    @Override
    public Map<String, Long> getUserCallCountHistory(String userId, int hours) {
        Map<String, Long> result = new LinkedHashMap<>();

        // 获取当前小时的时间戳
        long currentHourTimestamp = getCurrentHourTimestamp();

        for (int i = 0; i < hours; i++) {
            long hourTimestamp = currentHourTimestamp - (i * 3600);
            String countKey = buildCountKey(userId, String.valueOf(hourTimestamp));

            try {
                String countStr = stringRedisTemplate.opsForValue().get(countKey);
                if (countStr != null && !countStr.trim().isEmpty()) {
                    String hourStr = timestampToHourString(hourTimestamp);
                    result.put(hourStr, Long.parseLong(countStr));
                }
            } catch (Exception e) {
                log.error("获取用户历史调用次数失败，userId: {}, hour: {}", userId, hourTimestamp, e);
            }
        }

        return result;
    }

    //根据userId和小时时间戳删除redis中的键,userId若为空则删除全部，timestamp为空则删除当前小时
    @Override
    public int resetUserCallCount(String userId, Long hourTimestamp) {
        if (hourTimestamp == null ) hourTimestamp = getCurrentHourTimestamp();
        String pattern = USER_CALL_COUNT_KEY_PREFIX;
        if (userId != null) pattern += userId + ":" + hourTimestamp;
        pattern += "*:" + hourTimestamp;

        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            return keys.size();
        }
        return 0;
    }

    @Override
    public int processUserCallRecordByHourTimestamp(Long hourTimestamp) {
        if (hourTimestamp == null) hourTimestamp = getCurrentHourTimestamp();
        String pattern = USER_CALL_DATA_KEY_PREFIX + "*:" + hourTimestamp;

        Set<String> keys = stringRedisTemplate.keys(pattern);

        int row = 0;
        if (keys != null && !keys.isEmpty()) {
            for (String key : keys) {
                try {
                    String[] parts = key.split(":");
                    String userId = parts[3];
                    String countStr = stringRedisTemplate.opsForValue().get(key);

                    if (countStr != null && !countStr.trim().isEmpty()) {
                        int callCount = Integer.parseInt(countStr);
                        row += saveUserCallRecord(userId, hourTimestamp, callCount);
                        stringRedisTemplate.delete(key);
                        log.info("手动处理数据: userId={}, hour={}, count={}",
                                userId, hourTimestamp, callCount);
                    }
                } catch (Exception e) {
                    log.error("手动处理key失败: {}", key, e);
                    return 0;
                }
            }
        }
        return row;
    }

    @Override
    @Scheduled(cron = "0 */5 * * * ?") //每5分钟执行一次
    public void processUserCallRecordBatchAuto() {
        log.info("开始批量处理过期数据...");

        // 获取一小时前的时间戳
        long oneHourAgo = getCurrentHourTimestamp() - 3600;

        // 构建key模式
        String pattern = USER_CALL_DATA_KEY_PREFIX + "*";

        try {
            Set<String> allKeys = stringRedisTemplate.keys(pattern);

            if (allKeys != null && !allKeys.isEmpty()) {
                int processedCount = 0;

                for (String key : allKeys) {
                    try {
                        // 解析key获取时间戳
                        String[] parts = key.split(":");
                        if (parts.length >= 5) {
                            String userId = parts[3];
                            long hourTimestamp = Long.parseLong(parts[4]);

                            // 如果是一小时前的数据，进行处理
                            if (hourTimestamp <= oneHourAgo) {
                                String countStr = stringRedisTemplate.opsForValue().get(key);

                                if (countStr != null && !countStr.trim().isEmpty()) {
                                    int callCount = Integer.parseInt(countStr);

                                    // 保存到数据库
                                    saveUserCallRecord(userId, hourTimestamp, callCount);

                                    //反馈给评分模块
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("userId", userId);
                                    map.put("hourTimestamp", hourTimestamp);
                                    map.put("callCount", callCount);
                                    evaluationFeignClient.evaluateBaseOnAITeaching(map);

                                    // 从Redis删除key
                                    stringRedisTemplate.delete(key);

                                    processedCount++;
                                    log.debug("处理过期数据: key={}, count={}", key, callCount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("处理key失败: {}", key, e);
                    }
                }

                log.info("批量处理完成，共处理 {} 条记录", processedCount);
            } else {
                log.info("没有找到需要处理的key");
            }
        } catch (Exception e) {
            log.error("批量处理过期数据失败", e);
        }
    }

    //保存用户调用记录
    @Override
    @Transactional
    public int saveUserCallRecord(String userId, Long hourTimestamp, Integer callCount) {
        // 检查是否已存在记录
        QueryWrapper<UserCallRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("hour_timestamp", hourTimestamp);
        List<UserCallRecord> existingRecords = userCallRecordMapper.selectList(queryWrapper);

        int row;
        if (existingRecords.isEmpty()) {
            // 创建新记录
            UserCallRecord record = new UserCallRecord();
            record.setUserId(userId);
            record.setHourTimestamp(hourTimestamp);
            record.setCallCount(callCount);
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now());

            row = userCallRecordMapper.insert(record);
            log.info("保存用户调用记录: userId={}, hour={}, count={}",
                    userId, hourTimestamp, callCount);
        } else {
            // 更新现有记录（理论上不应该出现重复）
            UserCallRecord record = existingRecords.get(0);
            record.setCallCount(callCount);
            record.setUpdatedAt(LocalDateTime.now());
            row = userCallRecordMapper.updateById(record);
            log.info("更新用户调用记录: userId={}, hour={}, count={}",
                    userId, hourTimestamp, callCount);
        }
        return row;
    }

    @Override
    @Transactional
    public int deleteUserCallRecord(String userId, Long hourTimestamp, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        QueryWrapper<UserCallRecord> queryWrapper = new QueryWrapper<>();
        if (userId != null) queryWrapper.eq("user_id", userId);
        if (hourTimestamp != null) queryWrapper.eq("hour_timestamp", hourTimestamp);
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        return userCallRecordMapper.delete(queryWrapper);
    }

    @Override
    public List<UserCallRecord> getUserCallRecords(String userId, Long hourTimestamp, LocalDateTime createdAtStart, LocalDateTime createdAtEnd) {
        if (userId == null && hourTimestamp == null && createdAtStart == null && createdAtEnd == null) return userCallRecordMapper.selectList(null);
        QueryWrapper<UserCallRecord> queryWrapper = new QueryWrapper<>();
        if (userId != null) queryWrapper.eq("user_id", userId);
        if (hourTimestamp != null) queryWrapper.ge("hour_timestamp", hourTimestamp);
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        return userCallRecordMapper.selectList(queryWrapper);
    }

    /**
     * 构建Redis key
     */
    private String buildCountKey(String userId, String hourTimestamp) {
        return USER_CALL_COUNT_KEY_PREFIX + userId + ":" + hourTimestamp;
    }

    /**
     * 构建数据存储key（不过期）
     */
    private String buildDataKey(String userId, String hourTimestamp) {
        return USER_CALL_DATA_KEY_PREFIX  + userId + ":" + hourTimestamp;
    }

    /**
     * 获取当前整点小时的时间戳（秒级）
     */
    private long getCurrentHourTimestamp() {
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime - (currentTime % 3600);
    }

    /**
     * 时间戳转换为可读的小时字符串
     */
    private String timestampToHourString(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:00");
        return sdf.format(new Date(timestamp * 1000));
    }
}
