package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Course;
import com.hbwl.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping
    public String addCourse(@RequestBody Course course){
        int row = courseService.addCourse(course);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加课程失败";
        return "增加课程成功";
    }

    @DeleteMapping("{id}")
    public String deleteCourse(@PathVariable("id") Integer id){
        int row = courseService.deleteCourseById(id);
        if (row == 0) return "删除课程失败";
        return "删除课程成功";
    }

    @PutMapping
    public String updateCourse(@RequestBody Course course){
        int row = courseService.updateCourseById(course);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新课程失败";
        return "更新课程成功";
    }

    @GetMapping
    public List<Course> getCourses(@RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer sectionId){
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setSectionId(sectionId);
        return courseService.getCourses(course);
    }

    @GetMapping("/{page}/{size}")
    public Page<Course> getCoursesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                       @RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer sectionId){
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setSectionId(sectionId);
        return courseService.getCoursesPage(pageNo, pageSize, course);
    }
}
