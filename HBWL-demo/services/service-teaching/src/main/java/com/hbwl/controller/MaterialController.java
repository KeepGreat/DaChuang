package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Material;
import com.hbwl.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/teaching/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @PostMapping
    public String addMaterial(@RequestBody Material material,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = materialService.addMaterial(material);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "增加教材失败";
        return "增加教材成功";
    }

    @DeleteMapping("{id}")
    public String deleteMaterialById(@PathVariable("id") Integer id,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = materialService.deleteMaterialById(id);
        if (row == 0) return "删除教材失败";
        return "删除教材成功";
    }

    @PutMapping
    public String updateMaterial(@RequestBody Material material,
                                 @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return "权限不足";
        int row = materialService.updateMaterialById(material);
        if (row == -1) return "参数不能为空";
        if (row == 0) return "更新教材失败";
        return "更新教材成功";
    }

    @GetMapping
    public List<Material> getMaterials(@RequestParam(required = false) Integer id,
                                       @RequestParam(required = false) String type,
                                       @RequestParam(required = false) LocalDateTime createdTime,
                                       @RequestParam(required = false) LocalDateTime updatedTime,
                                       @RequestParam(required = false) Integer courseId){
        Material material = new Material();
        material.setId(id);
        material.setType(type);
        material.setCreatedTime(createdTime);
        material.setUpdatedTime(updatedTime);
        material.setCourseId(courseId);
        return materialService.getMaterials(material);
    }

    @GetMapping("/{page}/{size}")
    public Page<Material> getMaterialsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                           @RequestParam(required = false) Integer id,
                                           @RequestParam(required = false) String type,
                                           @RequestParam(required = false) LocalDateTime createdTime,
                                           @RequestParam(required = false) LocalDateTime updatedTime,
                                           @RequestParam(required = false) Integer courseId){
        Material material = new Material();
        material.setId(id);
        material.setType(type);
        material.setCreatedTime(createdTime);
        material.setUpdatedTime(updatedTime);
        material.setCourseId(courseId);
        return materialService.getMaterialsPage(pageNo, pageSize, material);
    }
}
