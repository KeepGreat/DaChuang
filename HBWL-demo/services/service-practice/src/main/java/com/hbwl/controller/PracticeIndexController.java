package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.service.PracticeIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice/practiceindex")
public class PracticeIndexController {

    @Autowired
    private PracticeIndexService practiceIndexService;

    @PostMapping
    public Result addPracticeIndexes(@RequestBody List<PracticeIndex> indexList,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceIndexService.addPracticeIndexes(indexList);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("插入练习索引失败");
        return Result.success("插入练习索引成功");
    }

    @DeleteMapping
    public Result deletePracticeIndexesByIds(@RequestParam List<Integer> indexIds,
                                             @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceIndexService.deletePracticeIndexesByIds(indexIds);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除练习索引失败");
        return Result.success("删除练习索引成功");
    }

    @PutMapping("/index")
    public Result updatePracticeIndexById(@RequestBody PracticeIndex practiceIndex,
                                          @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceIndexService.updatePracticeIndexById(practiceIndex);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新练习索引失败");
        return Result.success("更新练习索引成功");
    }

    @GetMapping
    public Result getPracticeIndexes(@RequestParam(required = false) Integer practiceId,
                                     @RequestParam(required = false) Integer courseSectionId,
                                     @RequestParam(required = false) Integer courseId){
        PracticeIndex practiceIndex = new PracticeIndex();
        practiceIndex.setPracticeId(practiceId);
        practiceIndex.setCourseSectionId(courseSectionId);
        practiceIndex.setCourseId(courseId);
        List<PracticeIndex> list = practiceIndexService.getPracticeIndexes(practiceIndex);
        if (list.isEmpty()) return Result.error("查询练习索引失败");
        return Result.success(list, "查询练习索引成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getPracticeIndexesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                         @RequestParam(required = false) Integer practiceId,
                                         @RequestParam(required = false) Integer courseSectionId,
                                         @RequestParam(required = false) Integer courseId){
        PracticeIndex practiceIndex = new PracticeIndex();
        practiceIndex.setPracticeId(practiceId);
        practiceIndex.setCourseSectionId(courseSectionId);
        practiceIndex.setCourseId(courseId);
        Page<PracticeIndex> page = practiceIndexService.getPracticeIndexesPage(pageNo, pageSize, practiceIndex);
        if (page.getSize() == 0) return Result.error("查询练习索引失败");
        return Result.success(page, "查询练习索引成功");
    }
}
