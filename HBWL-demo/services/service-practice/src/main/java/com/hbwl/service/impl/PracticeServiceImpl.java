package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.PracticeIndexMapper;
import com.hbwl.mapper.PracticeMapper;
import com.hbwl.pojo.Practice;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PracticeServiceImpl implements PracticeService {

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private PracticeIndexMapper practiceIndexMapper;

    @Override
    public int addPractice(Practice practice, PracticeIndex practiceIndex) {
        if (practice == null || practiceIndex == null) return -1;
        //addPractice
        if (practice.getName() == null || practice.getQuestionNum() == null) return -1;
        int row = 0;
        row = practiceMapper.insert(practice);
        if (row == 0) return 0;
        //addPracticeIndex
        if (practiceIndex.getCourseSectionId() == null) return -1;
        practiceIndex.setPracticeId(practice.getId());
        if (practiceIndex.getCourseId() == null) practiceIndex.setCourseId(0);
        row = practiceIndexMapper.insert(practiceIndex);
        if (row == 0) {
            throw new RuntimeException("插入PracticeIndex失败，进行回滚");
        }
        return row;
    }

    @Override
    public int deletePractice(Integer id) {
        if (id == null) return -1;
        //deletePractice
        int row = 0;
        row = practiceMapper.deleteById(id);
        if (row == 0) return 0;
        //deletePracticeIndex
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("practice_id", id);
        row = practiceIndexMapper.delete(queryWrapper);
        if (row == 0) {
            throw new RuntimeException("插入PracticeIndex失败，进行回滚");
        }
        return row;
    }

    @Override
    public int deletePractices(PracticeIndex practiceIndex) {
        if (practiceIndex == null || practiceIndex.getCourseSectionId() == null) return -1;
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_section_id", practiceIndex.getCourseSectionId());
        if (practiceIndex.getCourseId() != null) queryWrapper.eq("course_id", practiceIndex.getCourseId());
        else queryWrapper.eq("course_id", 0);
        List<PracticeIndex> practiceIndexList = practiceIndexMapper.selectList(queryWrapper);
        int row = 0;
        for (PracticeIndex index : practiceIndexList) {
            //deletePractices
            row = practiceMapper.deleteById(index.getPracticeId());
            if (row == 0) throw new RuntimeException("批量删除Practice失败，进行回滚");
        }
        //deletePracticeIndex
        row = practiceIndexMapper.delete(queryWrapper);
        if (row == 0) throw new RuntimeException("删除PracticeIndex失败，进行回滚");
        return row;
    }

    @Override
    public int updatePracticeById(Practice practice) {
        if (practice == null || practice.getId() == null) return -1;
        UpdateWrapper<Practice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", practice.getId());
        if (practice.getName() != null) updateWrapper.set("name", practice.getName());
        if (practice.getQuestionNum() != null) updateWrapper.set("question_num", practice.getQuestionNum());
        if (practice.getCreatedAt() != null) updateWrapper.set("created_at", practice.getCreatedAt());
        if (practice.getExpiredAt() != null) updateWrapper.set("expired_at", practice.getExpiredAt());
        return practiceMapper.update(null, updateWrapper);

    }

    @Override
    public List<Practice> getPractices(Integer id, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd) {
        if (id == null && createdAtStart == null && createdAtEnd == null && expiredAtStart == null && expiredAtEnd == null) return practiceMapper.selectList(null);
        QueryWrapper<Practice> queryWrapper = new QueryWrapper<>();
        if (id != null) queryWrapper.eq("id", id);
        if (createdAtStart != null && createdAtEnd != null) queryWrapper.between("created_at", createdAtStart, createdAtEnd);
        else {
            if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
            if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        }
        if (expiredAtStart != null && expiredAtEnd != null) queryWrapper.between("expired_at", expiredAtStart, expiredAtEnd);
        else {
            if (expiredAtStart != null) queryWrapper.ge("expired_at", expiredAtStart);
            if (expiredAtEnd != null) queryWrapper.le("expired_at", expiredAtEnd);
        }
        return practiceMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Practice> getPracticesPage(int pageNo, int pageSize,
                                           Integer id, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd) {
        Page<Practice> page = new Page<>(pageNo, pageSize);
        if (id == null && createdAtStart == null && createdAtEnd == null && expiredAtStart == null && expiredAtEnd == null) return practiceMapper.selectPage(page, null);
        QueryWrapper<Practice> queryWrapper = new QueryWrapper<>();
        if (id != null) queryWrapper.eq("id", id);
        if (createdAtStart != null && createdAtEnd != null) queryWrapper.between("created_at", createdAtStart, createdAtEnd);
        else {
            if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
            if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        }
        if (expiredAtStart != null && expiredAtEnd != null) queryWrapper.between("expired_at", expiredAtStart, expiredAtEnd);
        else {
            if (expiredAtStart != null) queryWrapper.ge("expired_at", expiredAtStart);
            if (expiredAtEnd != null) queryWrapper.le("expired_at", expiredAtEnd);
        }
        return practiceMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<Practice> getPracticesByIndex(PracticeIndex practiceIndex) {
        if (practiceIndex == null) return practiceMapper.selectList(null);
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        if (practiceIndex.getCourseId() != null) queryWrapper.eq("course_id", practiceIndex.getCourseId());
        else queryWrapper.eq("course_id", 0);
        if (practiceIndex.getCourseSectionId() != null) queryWrapper.eq("course_section_id", practiceIndex.getCourseSectionId());
        List<PracticeIndex> practiceIndexList = practiceIndexMapper.selectList(queryWrapper);
        List<Practice> practices = new ArrayList<>();
        for (PracticeIndex index : practiceIndexList) {
            practices.add(practiceMapper.selectById(index.getPracticeId()));
        }
        return practices;
    }

    @Override
    public Page<Practice> getPracticesPageByIndex(int pageNo, int pageSize, PracticeIndex practiceIndex) {
        Page<Practice> page = new Page<>(pageNo, pageSize);
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        if (practiceIndex.getCourseId() != null) queryWrapper.eq("course_id", practiceIndex.getCourseId());
        else queryWrapper.eq("course_id", 0);
        if (practiceIndex.getCourseSectionId() != null) queryWrapper.eq("course_section_id", practiceIndex.getCourseSectionId());
        List<PracticeIndex> practiceIndexList = practiceIndexMapper.selectList(queryWrapper);
        List<Practice> practices = new ArrayList<>();
        for (PracticeIndex index : practiceIndexList) {
            practices.add(practiceMapper.selectById(index.getPracticeId()));
        }
        return page.setRecords(practices);
    }
}
