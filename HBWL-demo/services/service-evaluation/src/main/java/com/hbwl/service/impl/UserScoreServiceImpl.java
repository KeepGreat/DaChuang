package com.hbwl.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.mapper.UserScoreMapper;
import com.hbwl.pojo.UserEvaluation;
import com.hbwl.pojo.UserScore;
import com.hbwl.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserScoreServiceImpl implements UserScoreService {

    @Autowired
    private UserScoreMapper userScoreMapper;

    @Override
    public int addUserScore(String userId) {
        if(userId == null)   return -1;
        UserScore userScore = new UserScore();
        UserEvaluation userEvaluation = new UserEvaluation();
        userEvaluation.setAccuracyRateScore(60f);
        userEvaluation.setAiDependenceScore(60f);
        userEvaluation.setConversionEfficiencyScore(60f);
        userEvaluation.setExpressionScore(60f);
        userEvaluation.setMasteryDepthScore(60f);
        userEvaluation.setQualityScore(60f);
        String eachScore = JSONUtil.toJsonStr(userEvaluation);

        userScore.setUserId(userId);
        userScore.setTotalScore(60.0f);

        userScore.setEachScore(eachScore);

        int insert = userScoreMapper.insert(userScore);
        return insert;
    }

    @Override
    public UserScore getUserScore(String userId) {
        if(userId == null || userId.trim().isEmpty())   return null;

        QueryWrapper<UserScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        return userScoreMapper.selectOne(queryWrapper);
    }

    @Override
    public int deleteUserScoreByUserId(String userId) {
        if(userId == null)  return -1;

        QueryWrapper<UserScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);

        return userScoreMapper.delete(queryWrapper);
    }

    @Override
    public int updateUserScoreByUserId(UserScore userScore) {
        if(userScore == null)   return -1;
        UpdateWrapper<UserScore> wrapper = new UpdateWrapper<>();
        wrapper.eq("userId", userScore.getUserId());
        if(userScore.getTotalScore() != null)  wrapper.set("total_score",userScore.getTotalScore());
        if(userScore.getEachScore() != null)  wrapper.set("each_score",userScore.getEachScore());
        return userScoreMapper.update(null, wrapper);
    }
}
