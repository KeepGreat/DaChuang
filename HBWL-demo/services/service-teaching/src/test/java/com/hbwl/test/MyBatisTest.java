package com.hbwl.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.CourseSection;
import com.hbwl.service.CourseSectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MyBatisTest {

    @Autowired
    private CourseSectionService courseSectionService;

    @Test
    public void test01(){
        CourseSection courseSection = new CourseSection();
        courseSection.setDescription("测试系列");
        courseSection.setName("测试系列");
        int row = courseSectionService.addCourseSection(courseSection);
        System.out.println(row);
    }

    @Test
    public void test02(){
        List<CourseSection> list = courseSectionService.getCourseSections(null);
        System.out.println(list);
        int row = 0;
        for (CourseSection courseSection : list) {
            courseSection.setDescription("测试更改");
             row += courseSectionService.updateCourseSectionById(courseSection);
        }
        System.out.println(row);
    }

    @Test
    public void test03(){
        Page<CourseSection> page = courseSectionService.getCourseSectionsPage(1, 2, null);
        System.out.println(page.getRecords());
        CourseSection courseSection = new CourseSection();
        courseSection.setName("测试");
        Page<CourseSection> page1 = courseSectionService.getCourseSectionsPage(1, 2, courseSection);
        System.out.println(page1.getRecords());
    }

    @Test
    public void test04(){
        CourseSection courseSection = new CourseSection();
        courseSection.setName("测试");
        int row = courseSectionService.deleteCourseSection(courseSection);
        System.out.println(row);
    }
}
