package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public String addMdContent(@RequestBody MdContent mdContent){
        int row = mdContentService.addMdContent(mdContent);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加markdown失败";
        return "增加markdown成功";
    }

    @DeleteMapping("{id}")
    public String deleteMdContentById(@PathVariable Integer id){
        int row = mdContentService.deleteMdContentById(id);
        if (row == 0) return "删除markdown失败";
        return "删除markdown成功";
    }

    @PutMapping
    public String updateMdContent(@RequestBody MdContent mdContent){
        int row = mdContentService.updateMdContentById(mdContent);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新markdown失败";
        return "更新markdown成功";
    }

    @GetMapping
    public List<MdContent> getMdContents(@RequestParam(required = false) Integer id,
                                         @RequestParam(required = false) Integer matId){
        MdContent mdContent = new MdContent();
        mdContent.setId(id);
        mdContent.setMatId(matId);
        return mdContentService.getMdContents(mdContent);
    }

    @GetMapping("/{page}/{size}")
    public Page<MdContent> getMdContentsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                             @RequestParam(required = false) Integer id,
                                             @RequestParam(required = false) Integer matId){
        MdContent mdContent = new MdContent();
        mdContent.setId(id);
        mdContent.setMatId(matId);
        return mdContentService.getMdContentsPage(pageNo, pageSize, mdContent);
    }
}
