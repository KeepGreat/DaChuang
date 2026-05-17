package com.hbwl.utils.ExcelUtil.Store;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.hbwl.mapper.KnowledgeGraphEdgeMapper;
import com.hbwl.mapper.KnowledgeGraphNodeMapper;
import com.hbwl.pojo.KnowledgeGraphEdge;
import com.hbwl.pojo.KnowledgeGraphNode;
import com.hbwl.pojo.dto.KnowledgeGraphDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class KnowledgeGraphStore {

    private static final int RELATION_PARENT_CHILD = 0;
    private static final int RELATION_PRE = 1;
    private static final int RELATION_POST = 2;
    private static final int RELATION_RELATED = 3;

    @Autowired
    private KnowledgeGraphNodeMapper knowledgeGraphNodeMapper;

    @Autowired
    private KnowledgeGraphEdgeMapper knowledgeGraphEdgeMapper;

    // 按课程分区维护导入上下文，保证分批写入时仍可补全树结构与未完善节点
    private final Map<Integer, ImportContext> importContextMap = new HashMap<>();

    /**
     * 将 DTO 列表转换为节点和边并写入数据库
     *
     * @param dtoList Excel 解析后的知识图谱 DTO 列表
     * @return 持久化成功的边数量
     */
    @Transactional
    public int storeKnowledgeGraph(List<KnowledgeGraphDTO> dtoList, Integer courseSectionId) {
        if (dtoList == null || dtoList.isEmpty() || courseSectionId == null) {
            return 0;
        }

        ImportContext context = getOrCreateContext(courseSectionId);
        Set<String> edgeDedupSet = new HashSet<>();
        int inserted = 0;

        for (KnowledgeGraphDTO dto : dtoList) {
            if (dto == null) {
                continue;
            }

            List<NodeRef> rowNodes = getRowNodes(dto);
            NodeRef current = rowNodes.isEmpty() ? null : rowNodes.get(rowNodes.size() - 1);

            // 先确保当前行出现的树节点全部有 id，且若是“未完善节点”则补全信息
            for (NodeRef nodeRef : rowNodes) {
                ensureResolvedNode(nodeRef, dto, courseSectionId, context);
            }

            // 建父子边：同一行显式层级优先；否则使用历史最近的上级节点
            for (int i = 0; i < rowNodes.size(); i++) {
                NodeRef child = rowNodes.get(i);
                String parentName = null;
                if (i > 0) {
                    parentName = rowNodes.get(i - 1).name;
                } else if (child.level > 1) {
                    parentName = context.lastNodeNameByLevel.get(child.level - 1);
                }
                if (parentName != null) {
                    Integer parentId = ensurePlaceholderNode(parentName, courseSectionId, context);
                    Integer childId = ensurePlaceholderNode(child.name, courseSectionId, context);
                    inserted += tryInsertEdge(parentId, childId, RELATION_PARENT_CHILD, courseSectionId, edgeDedupSet);
                }
            }

            // 更新树上下文，供后续“同级/下级行”识别父节点
            if (current != null) {
                context.lastNodeNameByLevel.put(current.level, current.name);
                clearDeeperLevels(context.lastNodeNameByLevel, current.level);
            }

            if (current != null) {
                Integer currentId = ensurePlaceholderNode(current.name, courseSectionId, context);
                inserted += addRelationEdgesFromList(dto.getPreNodeName(), currentId, RELATION_PRE, true, courseSectionId, context, edgeDedupSet);
                inserted += addRelationEdgesFromList(dto.getPostNodeName(), currentId, RELATION_POST, false, courseSectionId, context, edgeDedupSet);
                inserted += addRelationEdgesFromList(dto.getRelatedNodeName(), currentId, RELATION_RELATED, false, courseSectionId, context, edgeDedupSet);
                inserted += addRelationEdgesFromList(dto.getRelatedNodeName(), currentId, RELATION_RELATED, true, courseSectionId, context, edgeDedupSet);
            }
        }

        return inserted;
    }

    public void clearImportContext(Integer courseSectionId) {
        if (courseSectionId == null) {
            return;
        }
        synchronized (importContextMap) {
            importContextMap.remove(courseSectionId);
        }
    }

    private ImportContext getOrCreateContext(Integer courseSectionId) {
        synchronized (importContextMap) {
            return importContextMap.computeIfAbsent(courseSectionId, k -> new ImportContext());
        }
    }

    private List<NodeRef> getRowNodes(KnowledgeGraphDTO dto) {
        List<NodeRef> rowNodes = new ArrayList<>();
        String[] names = new String[]{
                dto.getLevel1NodeName(),
                dto.getLevel2NodeName(),
                dto.getLevel3NodeName(),
                dto.getLevel4NodeName(),
                dto.getLevel5NodeName(),
                dto.getLevel6NodeName(),
                dto.getLevel7NodeName()
        };
        for (int i = 0; i < names.length; i++) {
            String normalized = normalize(names[i]);
            if (normalized != null) {
                rowNodes.add(new NodeRef(normalized, i + 1));
            }
        }
        return rowNodes;
    }

    private Integer findNodeIdByName(String name, Integer courseSectionId) {
        QueryWrapper<KnowledgeGraphNode> query = new QueryWrapper<>();
        query.eq("name", name)
                .eq("course_section_id", courseSectionId)
                .last("limit 1");
        KnowledgeGraphNode existed = knowledgeGraphNodeMapper.selectOne(query);
        return existed == null ? null : existed.getId();
    }

    private Integer ensureResolvedNode(NodeRef nodeRef, KnowledgeGraphDTO dto, Integer courseSectionId, ImportContext context) {
        Integer nodeId = ensurePlaceholderNode(nodeRef.name, courseSectionId, context);

        if (context.pendingNodeIdByName.containsKey(nodeRef.name)) {
            UpdateWrapper<KnowledgeGraphNode> update = new UpdateWrapper<>();
            update.eq("id", nodeId)
                    .set("level", nodeRef.level)
                    .set("course_section_id", courseSectionId);

            String label = normalize(dto.getLabel());
            String description = normalize(dto.getDescription());
            String cognition = normalize(dto.getCognition());
            if (label != null) {
                update.set("label", label);
            }
            if (description != null) {
                update.set("description", description);
            }
            if (cognition != null) {
                update.set("cognition", cognition);
            }
            knowledgeGraphNodeMapper.update(null, update);
            context.pendingNodeIdByName.remove(nodeRef.name);
        }

        return nodeId;
    }

    private Integer ensurePlaceholderNode(String name, Integer courseSectionId, ImportContext context) {
        String normalized = normalize(name);
        if (normalized == null) {
            return null;
        }

        Integer existedId = context.nodeIdByName.get(normalized);
        if (existedId != null) {
            return existedId;
        }

        existedId = findNodeIdByName(normalized, courseSectionId);
        if (existedId != null) {
            context.nodeIdByName.put(normalized, existedId);
            return existedId;
        }

        KnowledgeGraphNode placeholder = new KnowledgeGraphNode();
        placeholder.setName(normalized);
        placeholder.setLevel(7);
        placeholder.setCourseSectionId(courseSectionId);
        knowledgeGraphNodeMapper.insert(placeholder);

        Integer id = placeholder.getId();
        context.nodeIdByName.put(normalized, id);
        context.pendingNodeIdByName.put(normalized, id);
        return id;
    }

    private boolean existsEdge(Integer fromId, Integer toId, Integer relationType, Integer courseSectionId) {
        QueryWrapper<KnowledgeGraphEdge> query = new QueryWrapper<>();
        query.eq("from_node_id", fromId)
                .eq("to_node_id", toId)
                .eq("relation_type", relationType)
                .eq("course_section_id", courseSectionId)
                .last("limit 1");
        return knowledgeGraphEdgeMapper.selectOne(query) != null;
    }

    private int tryInsertEdge(
            Integer fromId,
            Integer toId,
            int relationType,
            Integer courseSectionId,
            Set<String> edgeDedupSet
    ) {
        if (fromId == null || toId == null || courseSectionId == null) {
            return 0;
        }

        String dedupKey = fromId + "_" + toId + "_" + relationType + "_" + courseSectionId;
        if (!edgeDedupSet.add(dedupKey)) {
            return 0;
        }
        if (existsEdge(fromId, toId, relationType, courseSectionId)) {
            return 0;
        }

        KnowledgeGraphEdge edge = new KnowledgeGraphEdge();
        edge.setFromNodeId(fromId);
        edge.setToNodeId(toId);
        edge.setRelationType(relationType);
        edge.setCourseSectionId(courseSectionId);
        return knowledgeGraphEdgeMapper.insert(edge);
    }

    private int addRelationEdgesFromList(
            List<String> relationNames,
            Integer anchorId,
            int relationType,
            boolean listAsFrom,
            Integer courseSectionId,
            ImportContext context,
            Set<String> edgeDedupSet
    ) {
        if (relationNames == null || relationNames.isEmpty()) {
            return 0;
        }
        if (anchorId == null) {
            return 0;
        }

        int inserted = 0;
        for (String relationName : relationNames) {
            Integer relationId = ensurePlaceholderNode(relationName, courseSectionId, context);
            if (relationId == null) {
                continue;
            }
            if (listAsFrom) {
                inserted += tryInsertEdge(relationId, anchorId, relationType, courseSectionId, edgeDedupSet);
            } else {
                inserted += tryInsertEdge(anchorId, relationId, relationType, courseSectionId, edgeDedupSet);
            }
        }
        return inserted;
    }

    private void clearDeeperLevels(Map<Integer, String> levelMap, int currentLevel) {
        for (int level = currentLevel + 1; level <= 7; level++) {
            levelMap.remove(level);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trim = value.trim();
        return trim.isEmpty() ? null : trim;
    }

    private static class NodeRef {
        private final String name;
        private final int level;

        private NodeRef(String name, int level) {
            this.name = name;
            this.level = level;
        }
    }

    private static class ImportContext {
        private final Map<Integer, String> lastNodeNameByLevel = new HashMap<>();
        private final Map<String, Integer> nodeIdByName = new HashMap<>();
        private final Map<String, Integer> pendingNodeIdByName = new HashMap<>();
    }
}
