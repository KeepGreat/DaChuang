package com.hbwl.controller;

import com.hbwl.common.Result;
import com.hbwl.pojo.KnowledgeGraphEdge;
import com.hbwl.pojo.KnowledgeGraphNode;
import com.hbwl.pojo.KnowledgeGraphNodeIndex;
import com.hbwl.service.KnowledgeGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/teaching/knowledgegraph")
public class KnowledgeGraphController {

    @Autowired
    private KnowledgeGraphService knowledgeGraphService;

    @PostMapping("/node")
    public Result addKnowledgeGraphNode(@RequestBody KnowledgeGraphNode node,
                                        @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.addKnowledgeGraphNode(node);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加知识图谱节点失败");
        return Result.success("增加知识图谱节点成功");
    }

    @DeleteMapping("/node/{id}")
    public Result deleteKnowledgeGraphNode(@PathVariable("id") Integer id,
                                           @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.deleteKnowledgeGraphNodeById(id);
        if (row == 0) return Result.error("删除知识图谱节点失败");
        return Result.success("删除知识图谱节点成功");
    }

    @PutMapping("/node")
    public Result updateKnowledgeGraphNode(@RequestBody KnowledgeGraphNode node,
                                           @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.updateKnowledgeGraphNode(node);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新知识图谱节点失败");
        return Result.success("更新知识图谱节点成功");
    }

    @GetMapping("/node")
    public Result getKnowledgeGraphNodes(@RequestParam(required = false) Integer id,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Integer level,
                                         @RequestParam(required = false) Integer courseSectionId) {
        KnowledgeGraphNode node = new KnowledgeGraphNode();
        node.setId(id);
        node.setName(name);
        node.setLevel(level);
        node.setCourseSectionId(courseSectionId);
        return Result.success(knowledgeGraphService.getKnowledgeGraphNodes(node), "查询知识图谱节点成功");
    }

    @PostMapping("/edge")
    public Result addKnowledgeGraphEdge(@RequestBody KnowledgeGraphEdge edge,
                                        @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.addKnowledgeGraphEdge(edge);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加知识图谱边失败");
        return Result.success("增加知识图谱边成功");
    }

    @DeleteMapping("/edge/{id}")
    public Result deleteKnowledgeGraphEdge(@PathVariable("id") Integer id,
                                           @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.deleteKnowledgeGraphEdgeById(id);
        if (row == 0) return Result.error("删除知识图谱边失败");
        return Result.success("删除知识图谱边成功");
    }

    @PutMapping("/edge")
    public Result updateKnowledgeGraphEdge(@RequestBody KnowledgeGraphEdge edge,
                                           @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.updateKnowledgeGraphEdge(edge);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("更新知识图谱边失败");
        return Result.success("更新知识图谱边成功");
    }

    @GetMapping("/edge")
    public Result getKnowledgeGraphEdges(@RequestParam(required = false) Integer id,
                                         @RequestParam(required = false) Integer fromNodeId,
                                         @RequestParam(required = false) Integer toNodeId) {
        KnowledgeGraphEdge edge = new KnowledgeGraphEdge();
        edge.setId(id);
        edge.setFromNodeId(fromNodeId);
        edge.setToNodeId(toNodeId);
        return Result.success(knowledgeGraphService.getKnowledgeGraphEdges(edge), "查询知识图谱边成功");
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result importKnowledgeGraphFromExcel(@RequestPart("file") MultipartFile file,
                                                @RequestParam("courseSectionId") Integer courseSectionId,
                                                @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        boolean success = knowledgeGraphService.importKnowledgeGraphFromExcel(file, courseSectionId);
        if (!success) return Result.error("从Excel导入知识图谱失败");
        return Result.success("从Excel导入知识图谱成功");
    }

    @PostMapping("/index")
    public Result addKnowledgeGraphNodeIndex(@RequestBody KnowledgeGraphNodeIndex knowledgeGraphNodeIndex,
                                             @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.addKnowledgeGraphNodeIndex(knowledgeGraphNodeIndex);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("增加知识图谱节点索引失败");
        return Result.success("增加知识图谱节点索引成功");
    }

    @DeleteMapping("/index/{id}")
    public Result deleteKnowledgeGraphNodeIndex(@PathVariable("id") Integer id,
                                                @RequestHeader("role") String role) {
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.deleteKnowledgeGraphNodeIndexById(id);
        if (row == 0) return Result.error("删除知识图谱节点索引失败");
        return Result.success("删除知识图谱节点索引成功");
    }

    @GetMapping("/index")
    public Result getKnowledgeGraphNodeIndexes(@RequestParam(required = false) Integer id,
                                               @RequestParam(required = false) Integer nodeId,
                                               @RequestParam(required = false) Integer materialId) {
        KnowledgeGraphNodeIndex knowledgeGraphNodeIndex = new KnowledgeGraphNodeIndex();
        knowledgeGraphNodeIndex.setId(id);
        knowledgeGraphNodeIndex.setNodeId(nodeId);
        knowledgeGraphNodeIndex.setMaterialId(materialId);
        return Result.success(knowledgeGraphService.getKnowledgeGraphNodeIndices(knowledgeGraphNodeIndex), "查询知识图谱节点索引成功");
    }

    @DeleteMapping("/courseSection/{courseSectionId}")
    public Result deleteKnowledgeGraphByCourseSectionId(@PathVariable("courseSectionId") Integer courseSectionId,
                                                        @RequestHeader("role") String role){
        if (!(role.equals("teacher") || role.equals("admin"))) return Result.error("权限不足");
        int row = knowledgeGraphService.deleteKnowledgeGraphByCourseSectionId(courseSectionId);
        if (row == -1) return Result.error("参数不能为空");
        if (row == 0) return Result.error("删除知识图谱失败");
        return Result.success("删除知识图谱成功");
    }
}
