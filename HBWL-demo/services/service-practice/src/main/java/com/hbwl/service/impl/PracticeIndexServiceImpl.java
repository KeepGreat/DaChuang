package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.PracticeIndexMapper;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.service.PracticeIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PracticeIndexServiceImpl implements PracticeIndexService {

    @Autowired
    private PracticeIndexMapper practiceIndexMapper;

    @Override
    public int addPracticeIndexes(List<PracticeIndex> indexList) {
        if (indexList == null || indexList.isEmpty()) return -1;
        int row = 0;
        for (PracticeIndex practiceIndex : indexList) {
            row = practiceIndexMapper.insert(practiceIndex);
            if (row == 0) throw new RuntimeException("插入练习索引失败，进行回滚");
        }
        return row;
    }

    @Override
    public int deletePracticeIndexesByIds(List<Integer> indexIds) {
        if (indexIds == null || indexIds.isEmpty()) return -1;
        int row = 0;
        for (Integer indexId : indexIds) {
            row = practiceIndexMapper.deleteById(indexId);
            if (row == 0) throw new RuntimeException("删除练习索引失败，进行回滚");
        }
        return row;
    }

    @Override
    public int updatePracticeIndexById(PracticeIndex practiceIndex) {
        if (practiceIndex == null) return -1;
        return practiceIndexMapper.updateById(practiceIndex);
    }

    @Override
    public List<PracticeIndex> getPracticeIndexes(PracticeIndex practiceIndex) {
        if (practiceIndex == null) return practiceIndexMapper.selectList(null);
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        if (practiceIndex.getPracticeId() != null) queryWrapper.eq("practice_id", practiceIndex.getPracticeId());
        if (practiceIndex.getCourseSectionId() != null) queryWrapper.eq("course_section_id", practiceIndex.getCourseSectionId());
        if (practiceIndex.getCourseId() != null) queryWrapper.eq("course_id", practiceIndex.getCourseId());
        return practiceIndexMapper.selectList(queryWrapper);
    }

    @Override
    public Page<PracticeIndex> getPracticeIndexesPage(int pageNo, int pageSize, PracticeIndex practiceIndex) {
        Page<PracticeIndex> page = new Page<>(pageNo, pageSize);
        QueryWrapper<PracticeIndex> queryWrapper = new QueryWrapper<>();
        if (practiceIndex.getPracticeId() != null) queryWrapper.eq("practice_id", practiceIndex.getPracticeId());
        if (practiceIndex.getCourseSectionId() != null) queryWrapper.eq("course_section_id", practiceIndex.getCourseSectionId());
        if (practiceIndex.getCourseId() != null) queryWrapper.eq("course_id", practiceIndex.getCourseId());
        return practiceIndexMapper.selectPage(page, queryWrapper);
    }
}
