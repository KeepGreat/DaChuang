package com.hbwl.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.common.Result;
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
    public Result addMaterial(@RequestBody Material material,
                              @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = materialService.addMaterial(material);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加教材失败");
        return Result.success("增加教材成功");
    }

    @DeleteMapping("{id}")
    public Result deleteMaterialById(@PathVariable("id") Integer id,
                                     @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = materialService.deleteMaterialById(id);
        if (row == 0) return Result.error("删除教材失败");
        return Result.success("删除教材成功");
    }

    @PutMapping
    public Result updateMaterial(@RequestBody Material material,
                                 @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = materialService.updateMaterialById(material);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新教材失败");
        return Result.success("更新教材成功");
    }

    @GetMapping
    public Result getMaterials(@RequestParam(required = false) Integer id,
                               @RequestParam(required = false) String type,
                               @RequestParam(required = false) LocalDateTime createdAtStart,
                               @RequestParam(required = false) LocalDateTime createdAtEnd,
                               @RequestParam(required = false) LocalDateTime updatedAtStart,
                               @RequestParam(required = false) LocalDateTime updatedAtEnd,
                               @RequestParam(required = false) Integer courseId){
        Material material = new Material();
        material.setId(id);
        material.setType(type);
        material.setCourseId(courseId);
        List<Material> list = materialService.getMaterials(material, createdAtStart, createdAtEnd, updatedAtStart, updatedAtEnd);
        if (list == null || list.isEmpty()) return Result.error("查询资料信息失败");
        return Result.success(list, "查询资料信息成功");
    }

    @GetMapping("/{page}/{size}")
    public Result getMaterialsPage(@PathVariable("page") int pageNo, @PathVariable("size") int pageSize,
                                   @RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String type,
                                   @RequestParam(required = false) LocalDateTime createdAtStart,
                                   @RequestParam(required = false) LocalDateTime createdAtEnd,
                                   @RequestParam(required = false) LocalDateTime updatedAtStart,
                                   @RequestParam(required = false) LocalDateTime updatedAtEnd,
                                   @RequestParam(required = false) Integer courseId){
        Material material = new Material();
        material.setId(id);
        material.setType(type);
        material.setCourseId(courseId);
        Page<Material> page = materialService.getMaterialsPage(pageNo, pageSize, material, createdAtStart, createdAtEnd, updatedAtStart, updatedAtEnd);
        if (page == null || page.getSize() == 0) return Result.error("查询资料信息失败");
        return Result.success(page, "查询资料信息成功");
    }
}
