package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
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
    public Result addCourse(@RequestBody Course course,
                            @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseService.addCourse(course);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加课程失败");
        return Result.success("增加课程成功");
    }

    @DeleteMapping("{id}")
    public Result deleteCourse(@PathVariable("id") Integer id,
                               @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseService.deleteCourseById(id);
        if (row == 0) return Result.error("删除课程失败");
        return Result.success("删除课程成功");
    }

    @PutMapping
    public Result updateCourse(@RequestBody Course course,
                               @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseService.updateCourseById(course);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新课程失败");
        return Result.success("更新课程成功");
    }

    @GetMapping
    public Result getCourses(@RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String name,
                                   @RequestParam(required = false) Integer sectionId){
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setSectionId(sectionId);
        List<Course> list = courseService.getCourses(course);
        if (list == null || list.isEmpty()) return Result.error("查询课程失败");
        return Result.success(list, "查询课程成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getCoursesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                       @RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) Integer sectionId){
        Course course = new Course();
        course.setId(id);
        course.setName(name);
        course.setSectionId(sectionId);
        Page<Course> page = courseService.getCoursesPage(pageNo, pageSize, course);
        if (page == null || page.getSize() == 0) return Result.error("查询课程失败");
        return Result.success(page, "查询课程成功");
    }
}
