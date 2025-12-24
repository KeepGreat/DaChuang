package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.CourseSectionMapper;
import com.hbwl.pojo.CourseSection;
import com.hbwl.service.CourseSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseSectionServiceImpl implements CourseSectionService {

    @Autowired
    private CourseSectionMapper courseSectionMapper;

    @Override
    public int addCourseSection(CourseSection courseSection) {
        if (courseSection == null || courseSection.getName() == null) return -1;
        return courseSectionMapper.insert(courseSection);
    }

    @Override
    public int deleteCourseSection(CourseSection courseSection) {
        if (courseSection == null) return -1;
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        if (courseSection.getId() != null) queryWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) queryWrapper.like("name", courseSection.getName());
        return courseSectionMapper.delete(queryWrapper);
    }

    @Override
    public int deleteCourseSectionById(Integer id) {
        return courseSectionMapper.deleteById(id);
    }

    @Override
    public int updateCourseSectionById(CourseSection courseSection) {
        if (courseSection == null) return -1;
        UpdateWrapper<CourseSection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) updateWrapper.set("name", courseSection.getName());
        if (courseSection.getDescription() != null) updateWrapper.set("description", courseSection.getDescription());
        return courseSectionMapper.update(null, updateWrapper);
    }

    @Override
    public List<CourseSection> getCourseSections(CourseSection courseSection) {
        if (courseSection == null) return courseSectionMapper.selectList(null);
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        if (courseSection.getId() != null) queryWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) queryWrapper.like("name", courseSection.getName());
        return courseSectionMapper.selectList(queryWrapper);
    }

    @Override
    public Page<CourseSection> getCourseSectionsPage(int pageNo, int pageSize, CourseSection courseSection) {
        Page<CourseSection> page = new Page<>(pageNo, pageSize);
        if (courseSection == null) return courseSectionMapper.selectPage(page, null);
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        if (courseSection.getId() != null) queryWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) queryWrapper.like("name", courseSection.getName());
        return courseSectionMapper.selectPage(page, queryWrapper);
    }


}
