package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.MaterialMapper;
import com.hbwl.pojo.Material;
import com.hbwl.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public int addMaterial(Material material) {
        if (material == null || material.getType() == null || material.getCourseId() == null) return -1;
        return materialMapper.insert(material);
    }

    @Override
    public int deleteMaterialById(Integer id) {
        return materialMapper.deleteById(id);
    }

    @Override
    public int updateMaterialById(Material material) {
        if (material == null || material.getId() == null) return -1;
        UpdateWrapper<Material> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", material.getId());
        if (material.getType() != null) updateWrapper.set("type", material.getType());
        if (material.getDescription() != null) updateWrapper.set("description", material.getDescription());
        if (material.getUpdatedTime() != null) updateWrapper.set("updated_at", material.getUpdatedTime());
        if (material.getCourseId() != null) updateWrapper.set("course_id", material.getCourseId());
        return materialMapper.update(null, updateWrapper);
    }

    @Override
    public List<Material> getMaterials(Material material,
                                       LocalDateTime createdAtStart, LocalDateTime createdAtEnd,
                                       LocalDateTime updatedAtStart, LocalDateTime updatedAtEnd) {
        if (material == null) return materialMapper.selectList(null);
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        if (material.getId() != null) queryWrapper.eq("id", material.getId());
        if (material.getType() != null) queryWrapper.eq("type", material.getType());
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        if (updatedAtStart != null) queryWrapper.ge("updated_at", updatedAtStart);
        if (updatedAtEnd != null) queryWrapper.le("updated_at", updatedAtEnd);
        if (material.getCourseId() != null) queryWrapper.eq("course_id", material.getCourseId());
        return materialMapper.selectList(queryWrapper);
    }

    @Override
    public Page<Material> getMaterialsPage(int pageNo, int pageSize, Material material,
                                           LocalDateTime createdAtStart, LocalDateTime createdAtEnd,
                                           LocalDateTime updatedAtStart, LocalDateTime updatedAtEnd) {
        Page<Material> page = new Page<>(pageNo, pageSize);
        if (material == null) return materialMapper.selectPage(page, null);
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        if (material.getId() != null) queryWrapper.eq("id", material.getId());
        if (material.getType() != null) queryWrapper.eq("type", material.getType());
        if (createdAtStart != null) queryWrapper.ge("created_at", createdAtStart);
        if (createdAtEnd != null) queryWrapper.le("created_at", createdAtEnd);
        if (updatedAtStart != null) queryWrapper.ge("updated_at", updatedAtStart);
        if (updatedAtEnd != null) queryWrapper.le("updated_at", updatedAtEnd);
        if (material.getCourseId() != null) queryWrapper.eq("course_id", material.getCourseId());
        return materialMapper.selectPage(page, queryWrapper);
    }
}
