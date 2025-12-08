package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
import com.hbwl.pojo.MdContent;
import com.hbwl.service.MdContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching/markdown")
public class MdContentController {

    @Autowired
    private MdContentService mdContentService;

    @PostMapping
    public Result addMdContent(@RequestBody MdContent mdContent,
                               @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = mdContentService.addMdContent(mdContent);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加markdown失败");
        return Result.success("增加markdown成功");
    }

    @DeleteMapping("{id}")
    public Result deleteMdContentById(@PathVariable Integer id,
                                      @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = mdContentService.deleteMdContentById(id);
        if (row == 0) return Result.error("删除markdown失败");
        return Result.success("删除markdown成功");
    }

    @PutMapping
    public Result updateMdContent(@RequestBody MdContent mdContent,
                                  @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = mdContentService.updateMdContentById(mdContent);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新markdown失败");
        return Result.success("更新markdown成功");
    }

    @GetMapping
    public Result getMdContents(@RequestParam(required = false) Integer id,
                                         @RequestParam(required = false) Integer matId){
        MdContent mdContent = new MdContent();
        mdContent.setId(id);
        mdContent.setMatId(matId);
        List<MdContent> list = mdContentService.getMdContents(mdContent);
        if (list == null || list.isEmpty()) return Result.error("查询markdown失败");
        return Result.success(list, "查询markdown成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getMdContentsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                             @RequestParam(required = false) Integer id,
                                             @RequestParam(required = false) Integer matId){
        MdContent mdContent = new MdContent();
        mdContent.setId(id);
        mdContent.setMatId(matId);
        Page<MdContent> page = mdContentService.getMdContentsPage(pageNo, pageSize, mdContent);
        if (page == null || page.getSize() == 0) return Result.error("查询markdown失败");
        return Result.success(page, "查询markdown成功");
    }
}
