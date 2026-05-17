package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("knowledge_graph_node_index")
public class KnowledgeGraphNodeIndex {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer nodeId; //知识点id
    private Integer materialId; //资料id
    private Integer courseSectionId; //课程系列id, notnull
}
