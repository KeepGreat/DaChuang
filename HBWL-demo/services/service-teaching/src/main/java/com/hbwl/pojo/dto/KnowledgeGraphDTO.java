package com.hbwl.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.hbwl.utils.ExcelUtil.Converter.ExcelStringJavaStringListConverter;
import lombok.Data;

import java.util.List;

@Data
public class KnowledgeGraphDTO {
    @ExcelProperty("一级知识点")
    private String level1NodeName;
    @ExcelProperty("二级知识点")
    private String level2NodeName;
    @ExcelProperty("三级知识点")
    private String level3NodeName;
    @ExcelProperty("四级知识点")
    private String level4NodeName;
    @ExcelProperty("五级知识点")
    private String level5NodeName;
    @ExcelProperty("六级知识点")
    private String level6NodeName;
    @ExcelProperty("七级知识点")
    private String level7NodeName;
    @ExcelProperty(value = "前置知识点", converter = ExcelStringJavaStringListConverter.class)
    private List<String> preNodeName;
    @ExcelProperty(value = "后置知识点", converter = ExcelStringJavaStringListConverter.class)
    private List<String> postNodeName;
    @ExcelProperty(value = "关联知识点", converter = ExcelStringJavaStringListConverter.class)
    private List<String> relatedNodeName;
    @ExcelProperty("标签")
    private String label;
    @ExcelProperty("认知维度")
    private String cognition;
    @ExcelProperty("分类")
    private String type;
    @ExcelProperty("教学目标")
    private String teachingGoal;
    @ExcelProperty("知识点说明")
    private String description;
}
