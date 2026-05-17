package com.hbwl.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbwl.pojo.dto.KnowledgeGraphDTO;
import com.hbwl.utils.ExcelUtil.Listener.KnowledgeGraphListener;
import com.hbwl.utils.ExcelUtil.Store.KnowledgeGraphStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class KnowledgeGraphImportUtil {

    @Autowired
    private KnowledgeGraphStore knowledgeGraphStore;

    public boolean importExcel(MultipartFile file, Integer courseSectionId) {
        try {
            EasyExcel.read(file.getInputStream(), KnowledgeGraphDTO.class, new KnowledgeGraphListener(knowledgeGraphStore, courseSectionId))
                    .excelType(ExcelTypeEnum.XLSX).headRowNumber(2).sheet().doRead();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
