package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.CourseSection;

import java.util.List;

public interface CourseSectionService {

    int addCourseSection(CourseSection courseSection);

    int deleteCourseSection(CourseSection courseSection);

    int deleteCourseSectionById(Integer id);

    int updateCourseSectionById(CourseSection courseSection);

    List<CourseSection> getCourseSections(CourseSection courseSection);

    Page<CourseSection> getCourseSectionsPage(int pageNo, int pageSize, CourseSection courseSection);
}
