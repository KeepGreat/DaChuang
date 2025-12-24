package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.CourseMapper;
import com.hbwl.pojo.Course;
import com.hbwl.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public int addCourse(Course course) {
        if (course == null || course.getName() == null || course.getSectionId() == null) return -1;
        return courseMapper.insert(course);
    }

    @Override
    public int deleteCourse(Course course) {
        if (course == null) return -1;
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getId() != null) queryWrapper.eq("id", course.getId());
        if (course.getName() != null) queryWrapper.like("name", course.getName());
        if (course.getSectionId() != null) queryWrapper.eq("section_id", course.getSectionId());
        return courseMapper.delete(queryWrapper);
    }

    @Override
    public int deleteCourseById(Integer id) {
        return courseMapper.deleteById(id);
    }

    @Override
    public int updateCourseById(Course course) {
        if (course == null) return -1;
        UpdateWrapper<Course> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", course.getId());
        if (course.getName() != null) updateWrapper.set("name", course.getName());
        if (course.getSectionId() != null) updateWrapper.set("section_id", course.getSectionId());
        if (course.getDescription() != null) updateWrapper.set("description", course.getDescription());
        return courseMapper.update(null, updateWrapper);
    }

    @Override
    public List<Course> getCourses(Course course) {
        if (course == null) return courseMapper.selectList(null);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getId() != null) queryWrapper.eq("id", course.getId());
        if (course.getName() != null) queryWrapper.like("name", course.getName());
        if (course.getSectionId() != null) queryWrapper.eq("section_id", course.getSectionId());
        return courseMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Course> getCoursesPage(int pageNo, int pageSize, Course course) {
        Page<Course> page = new Page<>(pageNo, pageSize);
        if (course == null) return courseMapper.selectPage(page, null);
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        if (course.getId() != null) queryWrapper.eq("id", course.getId());
        if (course.getName() != null) queryWrapper.like("name", course.getName());
        if (course.getSectionId() != null) queryWrapper.eq("section_id", course.getSectionId());
        return courseMapper.selectPage(page, queryWrapper);
    }
}
