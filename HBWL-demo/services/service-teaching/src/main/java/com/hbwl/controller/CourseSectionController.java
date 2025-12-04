package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.CourseSection;
import com.hbwl.service.CourseSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching/coursesection")
public class CourseSectionController {

    @Autowired
    private CourseSectionService courseSectionService;

    @PostMapping
    public String addCourseSection(@RequestBody CourseSection courseSection,
                                   @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = courseSectionService.addCourseSection(courseSection);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加课程系列失败";
        return "增加课程系列成功";
    }

    @DeleteMapping("/{id}")
    public String deleteCourseSectionById(@PathVariable("id") Integer id,
                                          @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = courseSectionService.deleteCourseSectionById(id);
        if (row == 0) return "删除课程系列失败";
        return "删除课程系列成功";
    }

    @PutMapping
    public String updateCourseSection(@RequestBody CourseSection courseSection,
                                      @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = courseSectionService.updateCourseSectionById(courseSection);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新课程系列失败";
        return "更新课程系列成功";
    }

    @GetMapping
    public List<CourseSection> getCourseSections(@RequestParam(required = false) Integer id,
                                                 @RequestParam(required = false) String name){
        CourseSection courseSection = new CourseSection();
        courseSection.setId(id);
        courseSection.setName(name);
        return courseSectionService.getCourseSections(courseSection);
    }

    @GetMapping("/{page}/{size}")
    public Page<CourseSection> getCourseSectionsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                                     @RequestParam(required = false) Integer id,
                                                     @RequestParam(required = false) String name){
        CourseSection courseSection = new CourseSection();
        courseSection.setId(id);
        courseSection.setName(name);
        return courseSectionService.getCourseSectionsPage(pageNo, pageSize, courseSection);
    }
}
