package com.hbwl.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hbwl.pojo.MdContent;

import java.util.List;

public interface MdContentService {

    int addMdContent(MdContent mdContent);

    int deleteMdContentById(Integer id);

    int updateMdContentById(MdContent mdContent);

    List<MdContent> getMdContents(MdContent mdContent);

    Page<MdContent> getMdContentsPage(int pageNo, int pageSize, MdContent mdContent);
}
