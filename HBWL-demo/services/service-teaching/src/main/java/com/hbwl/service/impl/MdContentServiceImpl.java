package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.mapper.MdContentMapper;
import com.hbwl.pojo.MdContent;
import com.hbwl.service.MdContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MdContentServiceImpl implements MdContentService {

    @Autowired
    private MdContentMapper mdContentMapper;

    @Override
    public int addMdContent(MdContent mdContent) {
        if (mdContent == null) return -1;
        return mdContentMapper.insert(mdContent);
    }

    @Override
    public int deleteMdContentById(Integer id) {
        return mdContentMapper.deleteById(id);
    }

    @Override
    public int updateMdContentById(MdContent mdContent) {
        if (mdContent == null || mdContent.getId() == null) return -1;
        UpdateWrapper<MdContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", mdContent.getId());
        if (mdContent.getContent() != null) updateWrapper.set("content", mdContent.getContent());
        if (mdContent.getMatId() != null) updateWrapper.set("mat_id", mdContent.getMatId());
        return mdContentMapper.update(null, updateWrapper);
    }

    @Override
    public List<MdContent> getMdContents(MdContent mdContent) {
        if (mdContent == null) return mdContentMapper.selectList(null);
        QueryWrapper<MdContent> queryWrapper = new QueryWrapper<>();
        if (mdContent.getId() != null) queryWrapper.eq("id", mdContent.getId());
        if (mdContent.getMatId() != null) queryWrapper.eq("mat_id", mdContent.getMatId());
        return mdContentMapper.selectList(queryWrapper);
    }

    @Override
    public Page<MdContent> getMdContentsPage(int pageNo, int pageSize, MdContent mdContent) {
        Page<MdContent> page = new Page<>(pageNo, pageSize);
        if (mdContent == null) return mdContentMapper.selectPage(page, null);
        QueryWrapper<MdContent> queryWrapper = new QueryWrapper<>();
        if (mdContent.getId() != null) queryWrapper.eq("id", mdContent.getId());
        if (mdContent.getMatId() != null) queryWrapper.eq("mat_id", mdContent.getMatId());
        return mdContentMapper.selectPage(page, queryWrapper);
    }
}
