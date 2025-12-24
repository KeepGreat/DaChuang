package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Material;

import java.time.LocalDateTime;
import java.util.List;

public interface MaterialService {

    int addMaterial(Material material);

    int deleteMaterialById(Integer id);

    int updateMaterialById(Material material);

    List<Material> getMaterials(Material material,
                                LocalDateTime createdAtStart, LocalDateTime createdAtEnd,
                                LocalDateTime updatedAtStart, LocalDateTime updatedAtEnd);

    Page<Material> getMaterialsPage(int pageNo, int pageSize, Material material,
                                    LocalDateTime createdAtStart, LocalDateTime createdAtEnd,
                                    LocalDateTime updatedAtStart, LocalDateTime updatedAtEnd);
}
