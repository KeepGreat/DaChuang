package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.CourseSection;
import com.hbwl.pojo.CourseSectionType;

import java.util.List;

public interface CourseSectionService {

    int addCourseSection(CourseSection courseSection);

    int deleteCourseSection(CourseSection courseSection);

    int deleteCourseSectionById(Integer id);

    int updateCourseSectionById(CourseSection courseSection);

    List<CourseSection> getCourseSections(CourseSection courseSection);

    Page<CourseSection> getCourseSectionsPage(int pageNo, int pageSize, CourseSection courseSection);

    //CourseSectionType管理
    int addCourseSectionType(CourseSectionType courseSectionType);

    int deleteCourseSectionTypeById(Integer id);

    int updateCourseSectionTypeById(CourseSectionType courseSectionType);

    List<CourseSectionType> getCourseSectionTypes(CourseSectionType courseSectionType);
}
