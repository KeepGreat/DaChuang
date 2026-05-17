package com.hbwl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.mapper.KnowledgeGraphEdgeMapper;
import com.hbwl.mapper.KnowledgeGraphNodeIndexMapper;
import com.hbwl.mapper.KnowledgeGraphNodeMapper;
import com.hbwl.pojo.KnowledgeGraphEdge;
import com.hbwl.pojo.KnowledgeGraphNode;
import com.hbwl.pojo.KnowledgeGraphNodeIndex;
import com.hbwl.service.KnowledgeGraphService;
import com.hbwl.utils.KnowledgeGraphImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
public class KnowledgeGraphServiceImpl implements KnowledgeGraphService {

    @Autowired
    private KnowledgeGraphNodeMapper knowledgeGraphNodeMapper;

    @Autowired
    private KnowledgeGraphEdgeMapper knowledgeGraphEdgeMapper;

    @Autowired
    private KnowledgeGraphNodeIndexMapper knowledgeGraphNodeIndexMapper;

    @Autowired
    private KnowledgeGraphImportUtil knowledgeGraphImportUtil;

    @Override
    public int addKnowledgeGraphNode(KnowledgeGraphNode node) {
        //add node
        if (node == null || node.getName() == null || node.getLevel() == null || node.getCourseSectionId() == null) return -1;
        return knowledgeGraphNodeMapper.insert(node);
    }

    @Override
    public int deleteKnowledgeGraphNodeById(Integer id) {
        //delete node and edge
        if (id == null) return -1;
        // 删除该节点所有相关的边
        QueryWrapper<KnowledgeGraphEdge> edgeQueryWrapper = new QueryWrapper<>();
        edgeQueryWrapper.eq("from_node_id", id).or().eq("to_node_id", id);
        int row = knowledgeGraphEdgeMapper.delete(edgeQueryWrapper);
        // 删除该节点
        row += knowledgeGraphNodeMapper.deleteById(id);
        return row;
    }

    @Override
    public int deleteKnowledgeGraphByCourseSectionId(Integer courseSectionId) {
        if (courseSectionId == null) return -1;
        int row = 0;
        QueryWrapper<KnowledgeGraphNode> nodeQueryWrapper = new QueryWrapper<>();
        nodeQueryWrapper.eq("course_section_id", courseSectionId);
        row += knowledgeGraphNodeMapper.delete(nodeQueryWrapper);
        QueryWrapper<KnowledgeGraphEdge> edgeQueryWrapper = new QueryWrapper<>();
        edgeQueryWrapper.eq("course_section_id", courseSectionId);
        row += knowledgeGraphEdgeMapper.delete(edgeQueryWrapper);
        QueryWrapper<KnowledgeGraphNodeIndex> indexQueryWrapper = new QueryWrapper<>();
        indexQueryWrapper.eq("course_section_id", courseSectionId);
        row += knowledgeGraphNodeIndexMapper.delete(indexQueryWrapper);
        return row;
    }

    @Override
    public int updateKnowledgeGraphNode(KnowledgeGraphNode node) {
        // update node
        if (node == null || node.getId() == null) return -1;
        UpdateWrapper<KnowledgeGraphNode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", node.getId());
        if (node.getName() != null) updateWrapper.set("name", node.getName());
        if (node.getLevel() != null) updateWrapper.set("level", node.getLevel());
        if (node.getLabel() != null) updateWrapper.set("label", node.getLabel());
        if (node.getDescription() != null) updateWrapper.set("description", node.getDescription());
        if (node.getCognition() != null) updateWrapper.set("cognition", node.getCognition());
        return knowledgeGraphNodeMapper.update(null, updateWrapper);
    }

    @Override
    public List<KnowledgeGraphNode> getKnowledgeGraphNodes(KnowledgeGraphNode node) {
        if (node == null) return knowledgeGraphNodeMapper.selectList(null);
        QueryWrapper<KnowledgeGraphNode> queryWrapper = new QueryWrapper<>();
        if (node.getId() != null) queryWrapper.eq("id", node.getId());
        if (node.getName() != null) queryWrapper.eq("name", node.getName());
        if (node.getLevel() != null) queryWrapper.eq("level", node.getLevel());
        if (node.getLabel() != null) queryWrapper.eq("label", node.getLabel());
        if (node.getDescription() != null) queryWrapper.eq("description", node.getDescription());
        if (node.getCognition() != null) queryWrapper.eq("cognition", node.getCognition());
        return knowledgeGraphNodeMapper.selectList(queryWrapper);
    }

    @Override
    public int addKnowledgeGraphEdge(KnowledgeGraphEdge edge) {
        if (edge == null || edge.getFromNodeId() == null || edge.getToNodeId() == null || edge.getRelationType() == null) return -1;
        return knowledgeGraphEdgeMapper.insert(edge);
    }

    @Override
    public int deleteKnowledgeGraphEdgeById(Integer id) {
        if (id == null) return -1;
        return knowledgeGraphEdgeMapper.deleteById(id);
    }

    @Override
    public int updateKnowledgeGraphEdge(KnowledgeGraphEdge edge) {
        if (edge == null || edge.getId() == null) return -1;
        UpdateWrapper<KnowledgeGraphEdge> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", edge.getId());
        if (edge.getFromNodeId() != null) updateWrapper.set("from_node_id", edge.getFromNodeId());
        if (edge.getToNodeId() != null) updateWrapper.set("to_node_id", edge.getToNodeId());
        if (edge.getRelationType() != null) updateWrapper.set("relation_type", edge.getRelationType());
        return knowledgeGraphEdgeMapper.update(null, updateWrapper);
    }

    @Override
    public List<KnowledgeGraphEdge> getKnowledgeGraphEdges(KnowledgeGraphEdge edge) {
        if (edge == null) return knowledgeGraphEdgeMapper.selectList(null);
        QueryWrapper<KnowledgeGraphEdge> queryWrapper = new QueryWrapper<>();
        if (edge.getId() != null) queryWrapper.eq("id", edge.getId());
        if (edge.getFromNodeId() != null) queryWrapper.eq("from_node_id", edge.getFromNodeId());
        if (edge.getToNodeId() != null) queryWrapper.eq("to_node_id", edge.getToNodeId());
        if (edge.getRelationType() != null) queryWrapper.eq("relation_type", edge.getRelationType());
        return knowledgeGraphEdgeMapper.selectList(queryWrapper);
    }

    @Override
    public int addKnowledgeGraphNodeIndex(KnowledgeGraphNodeIndex index) {
        if (index == null || index.getNodeId() == null || index.getMaterialId() == null) return -1;
        return knowledgeGraphNodeIndexMapper.insert(index);
    }

    @Override
    public int deleteKnowledgeGraphNodeIndexById(Integer id) {
        if (id == null) return -1;
        return knowledgeGraphNodeIndexMapper.deleteById(id);
    }

    @Override
    public List<KnowledgeGraphNodeIndex> getKnowledgeGraphNodeIndices(KnowledgeGraphNodeIndex index) {
        if (index == null) return knowledgeGraphNodeIndexMapper.selectList(null);
        QueryWrapper<KnowledgeGraphNodeIndex> queryWrapper = new QueryWrapper<>();
        if (index.getId() != null) queryWrapper.eq("id", index.getId());
        if (index.getNodeId() != null) queryWrapper.eq("node_id", index.getNodeId());
        if (index.getMaterialId() != null) queryWrapper.eq("material_id", index.getMaterialId());
        return knowledgeGraphNodeIndexMapper.selectList(queryWrapper);
    }

    @Override
    public boolean importKnowledgeGraphFromExcel(MultipartFile file, Integer courseSectionId) {
        if (file == null || courseSectionId == null) return false;
        return knowledgeGraphImportUtil.importExcel(file, courseSectionId);
    }
}
