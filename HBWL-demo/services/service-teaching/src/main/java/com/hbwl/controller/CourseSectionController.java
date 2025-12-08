package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
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
    public Result addCourseSection(@RequestBody CourseSection courseSection,
                                   @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseSectionService.addCourseSection(courseSection);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加课程系列失败");
        return Result.success("增加课程系列成功");
    }

    @DeleteMapping("/{id}")
    public Result deleteCourseSectionById(@PathVariable("id") Integer id,
                                          @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseSectionService.deleteCourseSectionById(id);
        if (row == 0) return Result.error("删除课程系列失败");
        return Result.success("删除课程系列成功");
    }

    @PutMapping
    public Result updateCourseSection(@RequestBody CourseSection courseSection,
                                      @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = courseSectionService.updateCourseSectionById(courseSection);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新课程系列失败");
        return Result.success("更新课程系列成功");
    }

    @GetMapping
    public Result getCourseSections(@RequestParam(required = false) Integer id,
                                                 @RequestParam(required = false) String name){
        CourseSection courseSection = new CourseSection();
        courseSection.setId(id);
        courseSection.setName(name);
        List<CourseSection> list = courseSectionService.getCourseSections(courseSection);
        if (list == null || list.isEmpty()) return Result.error("查询课程系列失败");
        return Result.success(list, "查询课程系列成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getCourseSectionsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                                     @RequestParam(required = false) Integer id,
                                                     @RequestParam(required = false) String name){
        CourseSection courseSection = new CourseSection();
        courseSection.setId(id);
        courseSection.setName(name);
        Page<CourseSection> page = courseSectionService.getCourseSectionsPage(pageNo, pageSize, courseSection);
        if (page == null || page.getSize() == 0) return Result.error("查询课程列表失败");
        return Result.success(page, "查询课程列表成功");
    }
}
