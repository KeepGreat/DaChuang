package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.Practice;
import com.hbwl.pojo.PracticeIndex;
import com.hbwl.pojo.dto.PracticeDTO;
import com.hbwl.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/practice/practice")
public class PracticeController {

    @Autowired
    private PracticeService practiceService;

    @PostMapping
    public Result addPractice(@RequestBody PracticeDTO practiceDTO,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceService.addPractice(practiceDTO.getPractice(), practiceDTO.getPracticeIndex());
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("插入练习失败");
        return Result.success("插入练习成功");
    }

    @DeleteMapping("{id}")
    public Result deletePracticeById(@PathVariable Integer id,
                                 @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceService.deletePractice(id);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除练习失败");
        return Result.success("删除练习成功");
    }

    @DeleteMapping
    public Result deletePracticesByIndex(@RequestParam Integer courseSectionId,
                                  @RequestParam(required = false) Integer courseId,
                                  @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        PracticeIndex practiceIndex = new PracticeIndex();
        practiceIndex.setCourseSectionId(courseSectionId);
        practiceIndex.setCourseId(courseId);
        int row = practiceService.deletePractices(practiceIndex);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除练习失败");
        return Result.success("删除练习成功");
    }

    @PutMapping
    public Result updatePracticeById(@RequestBody Practice practice,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = practiceService.updatePracticeById(practice);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新练习失败");
        return Result.success("更新练习成功");
    }

    @GetMapping
    public Result getPractices(@RequestParam(required = false) Integer id,
                               @RequestParam(required = false) LocalDateTime createdAtStart,
                               @RequestParam(required = false) LocalDateTime createdAtEnd,
                               @RequestParam(required = false) LocalDateTime expiredAtStart,
                               @RequestParam(required = false) LocalDateTime expiredAtEnd){
        List<Practice> list = practiceService.getPractices(id, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd);
        if (list.isEmpty()) return Result.error("查询练习失败");
        return Result.success(list, "查询练习成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getPracticesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                   @RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) LocalDateTime createdAtStart,
                                   @RequestParam(required = false) LocalDateTime createdAtEnd,
                                   @RequestParam(required = false) LocalDateTime expiredAtStart,
                                   @RequestParam(required = false) LocalDateTime expiredAtEnd){
        Page<Practice> practicesPage = practiceService.getPracticesPage(pageNo, pageSize, id, createdAtStart, createdAtEnd, expiredAtStart, expiredAtEnd);
        if (practicesPage.getSize() == 0) return Result.error("查询练习失败");
        return Result.success(practicesPage, "查询练习成功");
    }

    @GetMapping("/index")
    public Result getPracticesByIndex(@RequestParam Integer courseSectionId,
                                      @RequestParam(required = false) Integer courseId){
        PracticeIndex practiceIndex = new PracticeIndex();
        practiceIndex.setCourseSectionId(courseSectionId);
        practiceIndex.setCourseId(courseId);
        List<Practice> list = practiceService.getPracticesByIndex(practiceIndex);
        if (list.isEmpty()) return Result.error("查询练习失败");
        return Result.success(list, "查询练习成功");
    }

    @GetMapping("/index/{page}/{size}")
    public Result getPracticesPageByIndex(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                          @RequestParam Integer courseSectionId,
                                          @RequestParam(required = false) Integer courseId){
        PracticeIndex practiceIndex = new PracticeIndex();
        practiceIndex.setCourseSectionId(courseSectionId);
        practiceIndex.setCourseId(courseId);
        Page<Practice> page = practiceService.getPracticesPageByIndex(pageNo, pageSize, practiceIndex);
        if (page.getSize() == 0) return Result.error("查询练习失败");
        return Result.success(page, "查询练习成功");
    }
}
