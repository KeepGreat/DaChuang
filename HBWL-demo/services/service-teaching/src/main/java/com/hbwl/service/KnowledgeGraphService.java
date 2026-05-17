package com.hbwl.service;

import com.hbwl.pojo.KnowledgeGraphEdge;
import com.hbwl.pojo.KnowledgeGraphNode;
import com.hbwl.pojo.KnowledgeGraphNodeIndex;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface KnowledgeGraphService {

    int addKnowledgeGraphNode(KnowledgeGraphNode node); //add node

    int deleteKnowledgeGraphNodeById(Integer id); //delete node and edge

    int deleteKnowledgeGraphByCourseSectionId(Integer courseSectionId); // delete knowledge graph by course section id

    int updateKnowledgeGraphNode(KnowledgeGraphNode node); // update node

    List<KnowledgeGraphNode> getKnowledgeGraphNodes(KnowledgeGraphNode node); // get nodes

    int addKnowledgeGraphEdge(KnowledgeGraphEdge edge); // add edge

    int deleteKnowledgeGraphEdgeById(Integer id); // delete edge

    int updateKnowledgeGraphEdge(KnowledgeGraphEdge edge); // update edge

    List<KnowledgeGraphEdge> getKnowledgeGraphEdges(KnowledgeGraphEdge edge); // get edges

    int addKnowledgeGraphNodeIndex(KnowledgeGraphNodeIndex index); // add node index

    int deleteKnowledgeGraphNodeIndexById(Integer id); // delete node index

    List<KnowledgeGraphNodeIndex> getKnowledgeGraphNodeIndices(KnowledgeGraphNodeIndex index); // get node indices

    boolean importKnowledgeGraphFromExcel(MultipartFile file, Integer courseSectionId); // import knowledge graph from excel
}
