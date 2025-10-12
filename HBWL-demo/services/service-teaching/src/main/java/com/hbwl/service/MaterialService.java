package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.Material;

import java.util.List;

public interface MaterialService {

    int addMaterial(Material material);

    int deleteMaterialById(Integer id);

    int updateMaterialById(Material material);

    List<Material> getMaterials(Material material);

    Page<Material> getMaterialsPage(int pageNo, int pageSize, Material material);
}
