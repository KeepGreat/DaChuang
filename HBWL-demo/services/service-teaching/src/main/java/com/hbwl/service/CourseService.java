package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Course;

import java.util.List;

public interface CourseService {

    int addCourse(Course course);

    int deleteCourse(Course course);

    int deleteCourseById(Integer id);

    int updateCourseById(Course course);

    List<Course> getCourses(Course course);

    Page<Course> getCoursesPage(int pageNo, int pageSize, Course course);
}
