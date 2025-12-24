package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.QuestionIndex;
import com.hbwl.service.QuestionIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practice/questionindex")
public class QuestionIndexController {

    @Autowired
    private QuestionIndexService questionIndexService;

    @PostMapping
    public Result addQuestionIndexes(@RequestBody List<QuestionIndex> questionIndexList,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionIndexService.addQuestionIndexes(questionIndexList);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("插入问题索引失败");
        return Result.success("插入问题索引成功");
    }

    @DeleteMapping
    public Result deleteQuestionIndexesByIds(@RequestParam List<Integer> indexIds,
                                             @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionIndexService.deleteQuestionIndexes(indexIds);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除问题索引失败");
        return Result.success("删除问题索引成功");
    }

    @PutMapping
    public Result updateQuestionIndexById(@RequestBody QuestionIndex questionIndex,
                                          @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = questionIndexService.updateQuestionIndex(questionIndex);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新问题索引失败");
        return Result.success("更新问题索引成功");
    }

    @GetMapping
    public Result getQuestionIndexes(@RequestParam(required = false) Integer practiceId,
                                     @RequestParam(required = false) Integer questionId){
        QuestionIndex index = new QuestionIndex();
        index.setPracticeId(practiceId);
        index.setQuestionId(questionId);
        List<QuestionIndex> list = questionIndexService.getQuestionIndexes(index);
        if (list.isEmpty()) return Result.error("查询问题索引失败");
        return Result.success(list, "查询问题索引成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getQuestionIndexesPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                         @RequestParam(required = false) Integer practiceId,
                                         @RequestParam(required = false) Integer questionId){
        QuestionIndex index = new QuestionIndex();
        index.setPracticeId(practiceId);
        index.setQuestionId(questionId);
        Page<QuestionIndex> page = questionIndexService.getQuestionIndexesPage(pageNo, pageSize, index);
        if (page.getSize() == 0) return Result.error("查询问题索引失败");
        return Result.success(page, "查询问题索引成功");
    }
}
