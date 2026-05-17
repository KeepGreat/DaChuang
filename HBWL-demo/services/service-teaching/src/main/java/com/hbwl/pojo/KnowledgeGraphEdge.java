package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_graph_edge")
public class KnowledgeGraphEdge {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer fromNodeId; //起始节点id
    private Integer toNodeId; //目标节点id
    private Integer relationType; //联系类型, 0:父子关系，1：前置关系，2：后置关系，3：关联关系
    private Integer courseSectionId; //课程系列id, notnull
}
