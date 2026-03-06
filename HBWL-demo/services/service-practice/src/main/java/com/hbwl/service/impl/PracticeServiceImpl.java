package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.PracticeIndexMapper;
import com.hbwl.mapper.PracticeMapper;
import com.hbwl.mapper.PracticeTypeMapper;
import com.hbwl.pojo.Practice;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.pojo.PracticeType;
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

    @Autowired
    private PracticeTypeMapper practiceTypeMapper;

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
        if (practice.getPracticeTypeId() != null) updateWrapper.set("practice_type_id", practice.getPracticeTypeId());
        if (practice.getCreatedAt() != null) updateWrapper.set("created_at", practice.getCreatedAt());
        if (practice.getExpiredAt() != null) updateWrapper.set("expired_at", practice.getExpiredAt());
        return practiceMapper.update(null, updateWrapper);

    }

    @Override
    public List<Practice> getPractices(Practice practice, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd) {
        if (practice == null
            && createdAtStart == null && createdAtEnd == null && expiredAtStart == null && expiredAtEnd == null) return practiceMapper.selectList(null);
        QueryWrapper<Practice> queryWrapper = new QueryWrapper<>();
        if (practice.getId() != null) queryWrapper.eq("id", practice.getId());
        if (practice.getName() != null) queryWrapper.like("name", practice.getName());
        if (practice.getPracticeTypeId() != null) queryWrapper.eq("practice_type_id", practice.getPracticeTypeId());
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        if (expiredAtStart != null) queryWrapper.ge("expired_at", expiredAtStart);
        if (expiredAtEnd != null) queryWrapper.le("expired_at", expiredAtEnd);
        return practiceMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Practice> getPracticesPage(int pageNo, int pageSize,
                                           Practice practice, LocalDateTime createdAtStart, LocalDateTime createdAtEnd, LocalDateTime expiredAtStart, LocalDateTime expiredAtEnd) {
        Page<Practice> page = new Page<>(pageNo, pageSize);
        if (practice == null && createdAtStart == null && createdAtEnd == null && expiredAtStart == null && expiredAtEnd == null) return practiceMapper.selectPage(page, null);
        QueryWrapper<Practice> queryWrapper = new QueryWrapper<>();
        if (practice.getId() != null) queryWrapper.eq("id", practice.getId());
        if (practice.getName() != null) queryWrapper.like("name", practice.getName());
        if (practice.getPracticeTypeId() != null) queryWrapper.eq("practice_type_id", practice.getPracticeTypeId());
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        if (expiredAtStart != null) queryWrapper.ge("expired_at", expiredAtStart);
        if (expiredAtEnd != null) queryWrapper.le("expired_at", expiredAtEnd);
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

    //管理PracticeType
    @Override
    public int addPracticeType(PracticeType practiceType) {
        if (practiceType == null || practiceType.getName() == null) return -1;
        return practiceTypeMapper.insert(practiceType);
    }

    @Override
    public int deletePracticeTypeById(Integer id) {
        if (id == null) return -1;
        UpdateWrapper<Practice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("practice_type_id", id);
        updateWrapper.set("practice_type_id", 0);
        return practiceTypeMapper.deleteById(id);
    }

    @Override
    public int updatePracticeTypeById(PracticeType practiceType) {
        if (practiceType == null || practiceType.getId() == null) return -1;
        UpdateWrapper<PracticeType> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", practiceType.getId());
        if (practiceType.getName() != null) updateWrapper.set("name", practiceType.getName());
        return practiceTypeMapper.update(null, updateWrapper);
    }

    @Override
    public List<PracticeType> getPracticeTypes(PracticeType practiceType) {
        if (practiceType == null) return practiceTypeMapper.selectList(null);
        QueryWrapper<PracticeType> queryWrapper = new QueryWrapper<>();
        if (practiceType.getId() != null) queryWrapper.eq("id", practiceType.getId());
        if (practiceType.getName() != null) queryWrapper.like("name", practiceType.getName());
        return practiceTypeMapper.selectList(queryWrapper);
    }
}
