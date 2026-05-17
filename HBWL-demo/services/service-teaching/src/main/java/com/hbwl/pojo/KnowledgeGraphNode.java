package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_graph_node")
public class KnowledgeGraphNode {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name; // notnull
    private Integer level; //知识点等级, 越低等级越高, notnull
    private String label; //知识点标签, 最长10个字符, 本质是枚举，由前端决定
    private String description; //知识点描述, 最长100个字符
    private String cognition; //知识点认知维度,最长10个字符,本质是枚举，由前端决定
    private Integer courseSectionId; //课程系列id, notnull
}
