package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.CourseSectionMapper;
import com.hbwl.mapper.CourseSectionTypeMapper;
import com.hbwl.pojo.CourseSection;
import com.hbwl.pojo.CourseSectionType;
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

    @Autowired
    private CourseSectionTypeMapper courseSectionTypeMapper;

    @Override
    public int addCourseSection(CourseSection courseSection) {
        if (courseSection == null || courseSection.getName() == null || courseSection.getTeacherId() == null) return -1;
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
        if (courseSection.getCourseSectionTypeId() != null) updateWrapper.set("course_section_type_id", courseSection.getCourseSectionTypeId());
        if (courseSection.getTeacherId() != null) updateWrapper.set("teacher_id", courseSection.getTeacherId());
        return courseSectionMapper.update(null, updateWrapper);
    }

    @Override
    public List<CourseSection> getCourseSections(CourseSection courseSection) {
        if (courseSection == null) return courseSectionMapper.selectList(null);
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        if (courseSection.getId() != null) queryWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) queryWrapper.like("name", courseSection.getName());
        if (courseSection.getCourseSectionTypeId() != null) queryWrapper.eq("course_section_type_id", courseSection.getCourseSectionTypeId());
        if (courseSection.getTeacherId() != null) queryWrapper.eq("teacher_id", courseSection.getTeacherId());
        return courseSectionMapper.selectList(queryWrapper);
    }

    @Override
    public Page<CourseSection> getCourseSectionsPage(int pageNo, int pageSize, CourseSection courseSection) {
        Page<CourseSection> page = new Page<>(pageNo, pageSize);
        if (courseSection == null) return courseSectionMapper.selectPage(page, null);
        QueryWrapper<CourseSection> queryWrapper = new QueryWrapper<>();
        if (courseSection.getId() != null) queryWrapper.eq("id", courseSection.getId());
        if (courseSection.getName() != null) queryWrapper.like("name", courseSection.getName());
        if (courseSection.getCourseSectionTypeId() != null) queryWrapper.eq("course_section_type_id", courseSection.getCourseSectionTypeId());
        if (courseSection.getTeacherId() != null) queryWrapper.eq("teacher_id", courseSection.getTeacherId());
        return courseSectionMapper.selectPage(page, queryWrapper);
    }

    //管理CourseSectionType
    @Override
    public int addCourseSectionType(CourseSectionType courseSectionType) {
        if (courseSectionType == null || courseSectionType.getName() == null) return -1;
        return courseSectionTypeMapper.insert(courseSectionType);
    }

    @Override
    public int deleteCourseSectionTypeById(Integer id) {
        if (id == null) return -1;
        UpdateWrapper<CourseSection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("course_section_type_id", id);
        updateWrapper.set("course_section_type_name", 0);
        courseSectionMapper.update(null, updateWrapper);
        return courseSectionTypeMapper.deleteById(id);
    }

    @Override
    public int updateCourseSectionTypeById(CourseSectionType courseSectionType) {
        if (courseSectionType == null || courseSectionType.getId() == null) return -1;
        UpdateWrapper<CourseSectionType> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", courseSectionType.getId());
        if (courseSectionType.getName() != null) updateWrapper.set("name", courseSectionType.getName());
        return courseSectionTypeMapper.update(null, updateWrapper);
    }

    @Override
    public List<CourseSectionType> getCourseSectionTypes(CourseSectionType courseSectionType) {
        if (courseSectionType == null) return courseSectionTypeMapper.selectList(null);
        QueryWrapper<CourseSectionType> queryWrapper = new QueryWrapper<>();
        if (courseSectionType.getId() != null) queryWrapper.eq("id", courseSectionType.getId());
        if (courseSectionType.getName() != null) queryWrapper.like("name", courseSectionType.getName());
        return courseSectionTypeMapper.selectList(queryWrapper);
    }


}
