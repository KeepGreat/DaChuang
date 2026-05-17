package com.hbwl.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.hbwl.pojo.dto.KnowledgeGraphDTO;
import com.hbwl.utils.ExcelUtil.Listener.KnowledgeGraphListener;
import com.hbwl.utils.ExcelUtil.Store.KnowledgeGraphStore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class KnowledgeGraphTest {

    @Autowired
    private KnowledgeGraphStore knowledgeGraphStore;

    @Test
    public void test01(){
        String filename = "E:\\data\\Study Profile\\PROGRAMMING\\Misson\\DaChuang\\HBWL-demo\\uploads\\面向对象程序设计（Java）-课程知识点.xlsx";
        EasyExcel.read(filename, KnowledgeGraphDTO.class, new KnowledgeGraphListener(knowledgeGraphStore, 1))
                .excelType(ExcelTypeEnum.XLSX).headRowNumber(2).sheet().doRead();
    }
}
