<template>
  <div class="knowledge-graph-page">
    <div class="page-header">
      <div class="title-group">
        <h2>知识图谱</h2>
        <p>支持图谱展示、节点关系编辑、节点资料索引维护与 Excel 导入。</p>
      </div>
      <div class="header-actions">
        <el-button type="danger" @click="removeGraph" :loading="deletingGraph">
          <el-icon><Delete /></el-icon>
          删除图谱
        </el-button>
        <el-button @click="reloadAll" :loading="loadingAll">
          <el-icon><Refresh /></el-icon>
          刷新图谱
        </el-button>
        <el-button type="primary" @click="importDialogVisible = true">
          <el-icon><Upload /></el-icon>
          导入 XLSX
        </el-button>
      </div>
    </div>

    <el-card class="toolbar-card" shadow="never">
      <div class="toolbar-row">
        <div class="toolbar-item">
          <span class="toolbar-label">当前课程系列 ID</span>
          <el-tag type="primary" effect="plain">{{ currentCourseSectionId || '-' }}</el-tag>
        </div>
        <div class="toolbar-item">
          <span class="toolbar-label">节点名称搜索</span>
          <el-input
            v-model="searchKeyword"
            placeholder="输入关键字过滤图谱"
            clearable
            style="width: 260px"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <div class="toolbar-item">
          <span class="toolbar-label">关系类型</span>
          <el-select v-model="relationTypeFilter" style="width: 180px">
            <el-option :value="-1" label="全部关系" />
            <el-option :value="0" label="父子关系" />
            <el-option :value="1" label="前置关系" />
            <el-option :value="2" label="后置关系" />
            <el-option :value="3" label="关联关系" />
          </el-select>
        </div>
      </div>
    </el-card>

    <div class="main-grid">
      <el-card class="toc-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>一级目录</span>
            <el-tag type="primary" effect="plain">{{ rootNodes.length }}</el-tag>
          </div>
        </template>
        <div class="toc-list">
          <el-empty v-if="rootNodes.length === 0" description="暂无一级节点" :image-size="56" />
          <el-button
            v-for="root in rootNodes"
            :key="root.id"
            class="toc-item"
            :type="Number(activeRootId) === Number(root.id) ? 'primary' : 'default'"
            @click="activeRootId = root.id"
          >
            <span class="toc-name">{{ root.name || `节点-${root.id}` }}</span>
            <span class="toc-level">L{{ root.level }}</span>
          </el-button>
        </div>
      </el-card>

      <el-card class="graph-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>图谱展示（单章视图）</span>
            <el-tag type="primary">节点 {{ displayNodes.length }}</el-tag>
          </div>
        </template>
        <div ref="chartRef" class="graph-canvas" />
        <div class="graph-controls">
          <el-button-group>
            <el-button @click="zoomOutGraph">缩小</el-button>
            <el-button @click="resetGraphViewport">重置</el-button>
            <el-button @click="zoomInGraph">放大</el-button>
          </el-button-group>
          <el-button :type="graphRoamEnabled ? 'primary' : 'default'" @click="toggleGraphRoam">
            {{ graphRoamEnabled ? '关闭鼠标拖动' : '开启鼠标拖动' }}
          </el-button>
        </div>
      </el-card>

      <div class="side-column">
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>节点详情</span>
            </div>
          </template>
          <div v-if="selectedNode" class="node-detail">
            <div class="detail-row"><span>节点 ID</span><strong>{{ selectedNode.id }}</strong></div>
            <div class="detail-row"><span>名称</span><strong>{{ selectedNode.name }}</strong></div>
            <div class="detail-row"><span>层级</span><strong>{{ selectedNode.level }}</strong></div>
            <div class="detail-row"><span>标签</span><strong>{{ selectedNode.label || '-' }}</strong></div>
            <div class="detail-row"><span>认知维度</span><strong>{{ selectedNode.cognition || '-' }}</strong></div>
            <div class="detail-row"><span>课程章节 ID</span><strong>{{ selectedNode.courseSectionId }}</strong></div>
            <div class="detail-row block">
              <span>描述</span>
              <p>{{ selectedNode.description || '暂无描述' }}</p>
            </div>
            <div class="detail-row block">
              <span>关联资料 ID</span>
              <div class="tag-wrap">
                <el-tag
                  v-for="materialId in selectedNodeMaterialIds"
                  :key="materialId"
                  effect="plain"
                  type="primary"
                >
                  {{ materialId }}
                </el-tag>
                <span v-if="selectedNodeMaterialIds.length === 0" class="empty-text">暂无关联</span>
              </div>
            </div>
          </div>
          <el-empty v-else description="点击图谱中的节点查看详情" :image-size="72" />
        </el-card>

        <el-card class="manager-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>图谱编辑</span>
            </div>
          </template>

          <el-tabs v-model="activeTab">
            <el-tab-pane label="节点 Node" name="node">
              <div class="tab-actions">
                <el-button type="primary" @click="openNodeDialog()">新增节点</el-button>
              </div>
              <el-table :data="scopedNodes" stripe height="260">
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="name" label="名称" min-width="120" />
                <el-table-column prop="level" label="层级" width="80" />
                <el-table-column prop="courseSectionId" label="章节ID" width="90" />
                <el-table-column label="操作" width="150" fixed="right">
                  <template #default="scope">
                    <el-button link type="primary" @click="openNodeDialog(scope.row)">编辑</el-button>
                    <el-button link type="danger" @click="removeNode(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="关系 Edge" name="edge">
              <div class="tab-actions">
                <el-button type="primary" @click="openEdgeDialog()">新增关系</el-button>
              </div>
              <el-table :data="scopedEdges" stripe height="260">
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="fromNodeId" label="起点" width="90" />
                <el-table-column prop="toNodeId" label="终点" width="90" />
                <el-table-column label="关系类型" min-width="110">
                  <template #default="scope">
                    <el-tag effect="plain">{{ relationText(scope.row.relationType) }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="150" fixed="right">
                  <template #default="scope">
                    <el-button link type="primary" @click="openEdgeDialog(scope.row)">编辑</el-button>
                    <el-button link type="danger" @click="removeEdge(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="索引 NodeIndex" name="index">
              <div class="tab-actions">
                <el-button type="primary" @click="openIndexDialog()">新增索引</el-button>
              </div>
              <el-table :data="scopedIndexes" stripe height="260">
                <el-table-column prop="id" label="ID" width="70" />
                <el-table-column prop="nodeId" label="节点ID" width="90" />
                <el-table-column prop="materialId" label="资料ID" width="90" />
                <el-table-column label="操作" width="120" fixed="right">
                  <template #default="scope">
                    <el-button link type="danger" @click="removeIndex(scope.row)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
    </div>

    <el-dialog
      v-model="nodeDialog.visible"
      :title="nodeDialog.isEdit ? '编辑节点' : '新增节点'"
      width="560px"
      destroy-on-close
    >
      <el-form :model="nodeForm" label-width="96px">
        <el-form-item label="名称">
          <el-input v-model="nodeForm.name" maxlength="50" />
        </el-form-item>
        <el-form-item label="层级">
          <el-input-number v-model="nodeForm.level" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="nodeForm.label" maxlength="10" />
        </el-form-item>
        <el-form-item label="认知维度">
          <el-input v-model="nodeForm.cognition" maxlength="10" />
        </el-form-item>
        <el-form-item label="章节 ID">
          <el-input-number v-model="nodeForm.courseSectionId" :min="1" :controls="false" disabled style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="nodeForm.description" type="textarea" :rows="3" maxlength="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="nodeDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="nodeDialog.saving" @click="submitNode">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="edgeDialog.visible"
      :title="edgeDialog.isEdit ? '编辑关系' : '新增关系'"
      width="520px"
      destroy-on-close
    >
      <el-form :model="edgeForm" label-width="96px">
        <el-form-item label="起点节点 ID">
          <el-input-number v-model="edgeForm.fromNodeId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="终点节点 ID">
          <el-input-number v-model="edgeForm.toNodeId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="关系类型">
          <el-select v-model="edgeForm.relationType" style="width: 100%">
            <el-option :value="0" label="父子关系" />
            <el-option :value="1" label="前置关系" />
            <el-option :value="2" label="后置关系" />
            <el-option :value="3" label="关联关系" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="edgeDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="edgeDialog.saving" @click="submitEdge">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="indexDialog.visible"
      title="新增节点索引"
      width="520px"
      destroy-on-close
    >
      <el-form :model="indexForm" label-width="96px">
        <el-form-item label="节点 ID">
          <el-input-number v-model="indexForm.nodeId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
        <el-form-item label="资料 ID">
          <el-input-number v-model="indexForm.materialId" :min="1" :controls="false" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="indexDialog.visible = false">取消</el-button>
        <el-button type="primary" :loading="indexDialog.saving" @click="submitIndex">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="importDialogVisible"
      title="导入知识图谱（XLSX）"
      width="520px"
      destroy-on-close
    >
      <el-form label-width="96px">
        <el-form-item label="章节 ID">
          <el-input :model-value="String(currentCourseSectionId || '')" disabled />
        </el-form-item>
        <el-form-item label="Excel 文件">
          <el-upload
            :show-file-list="true"
            :limit="1"
            accept=".xlsx"
            :auto-upload="false"
            :on-change="onImportFileChange"
            :on-remove="onImportFileRemove"
          >
            <template #trigger>
              <el-button>选择 XLSX 文件</el-button>
            </template>
            <template #tip>
              <div class="upload-tip">仅支持 .xlsx 文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="importing" @click="submitImport">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Refresh, Search, Upload } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import {
  addKnowledgeGraphEdge,
  addKnowledgeGraphNode,
  addKnowledgeGraphNodeIndex,
  deleteKnowledgeGraphByCourseSectionId,
  deleteKnowledgeGraphEdge,
  deleteKnowledgeGraphNode,
  deleteKnowledgeGraphNodeIndex,
  getKnowledgeGraphEdges,
  getKnowledgeGraphNodeIndexes,
  getKnowledgeGraphNodes,
  importKnowledgeGraphFromExcel,
  updateKnowledgeGraphEdge,
  updateKnowledgeGraphNode,
} from "@/api/modules/teaching/KnowledgeGraphAPI";

const props = defineProps({
  courseSectionId: {
    type: Number,
    default: null,
  },
});

const chartRef = ref(null);
let chartInstance = null;

const loadingAll = ref(false);
const deletingGraph = ref(false);
const importing = ref(false);
const importDialogVisible = ref(false);
const importFile = ref(null);

const searchKeyword = ref("");
const relationTypeFilter = ref(-1);
const activeTab = ref("node");
const activeRootId = ref(null);
const graphZoom = ref(1);
const graphRoamEnabled = ref(true);

const nodes = ref([]);
const edges = ref([]);
const indexes = ref([]);

const selectedNodeId = ref(null);

const currentCourseSectionId = computed(() => {
  const id = Number(props.courseSectionId);
  return Number.isFinite(id) && id > 0 ? id : null;
});

const nodeDialog = reactive({
  visible: false,
  isEdit: false,
  saving: false,
});

const edgeDialog = reactive({
  visible: false,
  isEdit: false,
  saving: false,
});

const indexDialog = reactive({
  visible: false,
  saving: false,
});

const nodeForm = reactive({
  id: null,
  name: "",
  level: 1,
  label: "",
  description: "",
  cognition: "",
  courseSectionId: undefined,
});

const edgeForm = reactive({
  id: null,
  fromNodeId: undefined,
  toNodeId: undefined,
  relationType: 0,
});

const indexForm = reactive({
  nodeId: undefined,
  materialId: undefined,
});

const relationText = (relationType) => {
  const relationMap = {
    0: "父子关系",
    1: "前置关系",
    2: "后置关系",
    3: "关联关系",
  };
  return relationMap[relationType] || "未知";
};

const extractList = (res) => {
  if (Array.isArray(res)) return res;
  if (Array.isArray(res?.data)) return res.data;
  if (Array.isArray(res?.data?.records)) return res.data.records;
  return [];
};

const filteredNodes = computed(() => {
  return scopedNodes.value.filter((node) => {
    const matchKeyword = !searchKeyword.value || (node.name || "").toLowerCase().includes(searchKeyword.value.toLowerCase());
    return matchKeyword;
  });
});

const filteredEdges = computed(() => {
  return scopedEdges.value.filter((edge) => {
    const matchType = relationTypeFilter.value < 0 || Number(edge.relationType) === Number(relationTypeFilter.value);
    const nodeSet = new Set(filteredNodes.value.map((node) => node.id));
    return matchType && nodeSet.has(edge.fromNodeId) && nodeSet.has(edge.toNodeId);
  });
});

const scopedNodes = computed(() => {
  if (!currentCourseSectionId.value) return [];
  return nodes.value.filter((node) => Number(node.courseSectionId) === Number(currentCourseSectionId.value));
});

const scopedEdges = computed(() => {
  const nodeIdSet = new Set(scopedNodes.value.map((item) => Number(item.id)));
  return edges.value.filter((edge) => nodeIdSet.has(Number(edge.fromNodeId)) && nodeIdSet.has(Number(edge.toNodeId)));
});

const scopedIndexes = computed(() => {
  const nodeIdSet = new Set(scopedNodes.value.map((item) => Number(item.id)));
  return indexes.value.filter((index) => nodeIdSet.has(Number(index.nodeId)));
});

const rootNodes = computed(() => {
  return scopedNodes.value
    .filter((node) => Number(node.level) === 1)
    .sort((a, b) => Number(a.id) - Number(b.id));
});

const rootSubtreeNodeIds = computed(() => {
  if (!activeRootId.value) return new Set();

  const idSet = new Set([Number(activeRootId.value)]);
  const queue = [Number(activeRootId.value)];
  const hierarchyEdges = scopedEdges.value.filter((edge) => Number(edge.relationType) === 0);

  while (queue.length) {
    const current = queue.shift();
    hierarchyEdges
      .filter((edge) => Number(edge.fromNodeId) === Number(current))
      .forEach((edge) => {
        const childId = Number(edge.toNodeId);
        if (!idSet.has(childId)) {
          idSet.add(childId);
          queue.push(childId);
        }
      });
  }

  if (idSet.size === 1) {
    let changed = true;
    while (changed) {
      changed = false;
      hierarchyEdges.forEach((edge) => {
        const fromId = Number(edge.fromNodeId);
        const toId = Number(edge.toNodeId);
        if (idSet.has(fromId) && !idSet.has(toId)) {
          idSet.add(toId);
          changed = true;
        }
        if (idSet.has(toId) && !idSet.has(fromId)) {
          idSet.add(fromId);
          changed = true;
        }
      });
    }
  }

  return idSet;
});

const displayNodes = computed(() => {
  const visibleIdSet = rootSubtreeNodeIds.value;
  if (visibleIdSet.size === 0) return [];
  return filteredNodes.value.filter((node) => visibleIdSet.has(Number(node.id)));
});

const displayEdges = computed(() => {
  const nodeIdSet = new Set(displayNodes.value.map((node) => Number(node.id)));
  return filteredEdges.value.filter((edge) => nodeIdSet.has(Number(edge.fromNodeId)) && nodeIdSet.has(Number(edge.toNodeId)));
});

const selectedNode = computed(() => {
  if (!selectedNodeId.value) return null;
  return nodes.value.find((node) => Number(node.id) === Number(selectedNodeId.value)) || null;
});

const selectedNodeMaterialIds = computed(() => {
  if (!selectedNode.value) return [];
  return indexes.value
    .filter((item) => Number(item.nodeId) === Number(selectedNode.value.id))
    .map((item) => item.materialId);
});

const buildChartOption = () => {
  const nodeColorByLevel = {
    1: "#93c5fd",
    2: "#a5d2fd",
    3: "#bfdbfe",
    4: "#dbeafe",
  };

  const data = displayNodes.value.map((node) => ({
    id: String(node.id),
    name: node.name || `节点-${node.id}`,
    value: node.description || "暂无描述",
    symbolSize: Math.max(28, 82 - (Number(node.level) || 1) * 12),
    itemStyle: {
      color: nodeColorByLevel[node.level] || "#3b82f6",
      borderColor: "#60a5fa",
      borderWidth: 2,
    },
    label: {
      show: true,
      color: "#1e293b",
      fontSize: 12,
      fontWeight: 600,
    },
  }));

  const links = displayEdges.value.map((edge) => ({
    source: String(edge.fromNodeId),
    target: String(edge.toNodeId),
    value: relationText(edge.relationType),
    lineStyle: {
      width: 2,
      color: "#60a5fa",
      opacity: 0.9,
      curveness: 0.1,
    },
    label: {
      show: true,
      formatter: relationText(edge.relationType),
      color: "#1e40af",
      fontSize: 11,
      backgroundColor: "rgba(255,255,255,0.7)",
      padding: [2, 4],
      borderRadius: 4,
    },
  }));

  return {
    backgroundColor: "transparent",
    tooltip: {
      trigger: "item",
      formatter: (params) => {
        if (params.dataType === "node") {
          const row = nodes.value.find((item) => String(item.id) === String(params.data.id));
          return [
            `<strong>${params.name}</strong>`,
            `ID: ${row?.id ?? "-"}`,
            `层级: ${row?.level ?? "-"}`,
            `标签: ${row?.label || "-"}`,
            `认知维度: ${row?.cognition || "-"}`,
          ].join("<br/>");
        }
        return `关系: ${params.data.value || "-"}`;
      },
    },
    series: [
      {
        type: "graph",
        layout: "force",
        roam: graphRoamEnabled.value,
        zoom: graphZoom.value,
        draggable: true,
        force: {
          repulsion: 520,
          edgeLength: [120, 220],
          gravity: 0.08,
        },
        edgeSymbol: ["none", "arrow"],
        edgeSymbolSize: [4, 10],
        emphasis: {
          focus: "adjacency",
          lineStyle: {
            width: 3,
            color: "#2563eb",
          },
        },
        data,
        links,
      },
    ],
  };
};

const renderChart = () => {
  if (!chartRef.value) return;

  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value);
    chartInstance.on("click", (params) => {
      if (params.dataType === "node") {
        selectedNodeId.value = Number(params.data.id);
      }
    });
  }

  chartInstance.setOption(buildChartOption(), true);
};

const reloadAll = async () => {
  loadingAll.value = true;
  try {
    const [nodeRes, edgeRes, indexRes] = await Promise.all([
      getKnowledgeGraphNodes(undefined, undefined, undefined, currentCourseSectionId.value || undefined),
      getKnowledgeGraphEdges(undefined, undefined, undefined, currentCourseSectionId.value || undefined),
      getKnowledgeGraphNodeIndexes(undefined, undefined, undefined, currentCourseSectionId.value || undefined),
    ]);

    nodes.value = extractList(nodeRes);
    edges.value = extractList(edgeRes);
    indexes.value = extractList(indexRes);

    if (rootNodes.value.length > 0) {
      const exists = rootNodes.value.some((item) => Number(item.id) === Number(activeRootId.value));
      if (!exists) {
        activeRootId.value = rootNodes.value[0].id;
      }
    } else {
      activeRootId.value = null;
    }

    if (selectedNodeId.value && !nodes.value.some((item) => Number(item.id) === Number(selectedNodeId.value))) {
      selectedNodeId.value = null;
    }

    await nextTick();
    renderChart();
  } catch (error) {
    ElMessage.error(error?.message || "加载知识图谱失败");
  } finally {
    loadingAll.value = false;
  }
};

const openNodeDialog = (row) => {
  if (row) {
    nodeDialog.isEdit = true;
    Object.assign(nodeForm, {
      id: row.id,
      name: row.name || "",
      level: row.level ?? 1,
      label: row.label || "",
      description: row.description || "",
      cognition: row.cognition || "",
      courseSectionId: row.courseSectionId,
    });
  } else {
    nodeDialog.isEdit = false;
    Object.assign(nodeForm, {
      id: null,
      name: "",
      level: 1,
      label: "",
      description: "",
      cognition: "",
      courseSectionId: currentCourseSectionId.value,
    });
  }
  nodeDialog.visible = true;
};

const submitNode = async () => {
  if (!nodeForm.name || !nodeForm.level || !nodeForm.courseSectionId) {
    ElMessage.warning("请填写节点名称、层级、章节 ID");
    return;
  }

  nodeDialog.saving = true;
  try {
    const payload = {
      id: nodeForm.id,
      name: nodeForm.name,
      level: Number(nodeForm.level),
      label: nodeForm.label,
      description: nodeForm.description,
      cognition: nodeForm.cognition,
      courseSectionId: Number(currentCourseSectionId.value),
    };

    if (nodeDialog.isEdit) {
      await updateKnowledgeGraphNode(payload);
      ElMessage.success("节点更新成功");
    } else {
      delete payload.id;
      await addKnowledgeGraphNode(payload);
      ElMessage.success("节点新增成功");
    }

    nodeDialog.visible = false;
    await reloadAll();
  } catch (error) {
    ElMessage.error(error?.message || "节点保存失败");
  } finally {
    nodeDialog.saving = false;
  }
};

const removeNode = async (row) => {
  try {
    await ElMessageBox.confirm(`确认删除节点「${row.name}」吗？`, "删除节点", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
    await deleteKnowledgeGraphNode(row.id);
    ElMessage.success("节点删除成功");
    await reloadAll();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.message || "节点删除失败");
    }
  }
};

const openEdgeDialog = (row) => {
  if (row) {
    edgeDialog.isEdit = true;
    Object.assign(edgeForm, {
      id: row.id,
      fromNodeId: row.fromNodeId,
      toNodeId: row.toNodeId,
      relationType: row.relationType,
    });
  } else {
    edgeDialog.isEdit = false;
    Object.assign(edgeForm, {
      id: null,
      fromNodeId: undefined,
      toNodeId: undefined,
      relationType: 0,
    });
  }
  edgeDialog.visible = true;
};

const submitEdge = async () => {
  if (!edgeForm.fromNodeId || !edgeForm.toNodeId) {
    ElMessage.warning("请填写起点和终点节点 ID");
    return;
  }
  if (!currentCourseSectionId.value) {
    ElMessage.warning("当前课程系列 ID 无效");
    return;
  }

  edgeDialog.saving = true;
  try {
    const payload = {
      id: edgeForm.id,
      fromNodeId: Number(edgeForm.fromNodeId),
      toNodeId: Number(edgeForm.toNodeId),
      relationType: Number(edgeForm.relationType),
      courseSectionId: Number(currentCourseSectionId.value),
    };

    if (edgeDialog.isEdit) {
      await updateKnowledgeGraphEdge(payload);
      ElMessage.success("关系更新成功");
    } else {
      delete payload.id;
      await addKnowledgeGraphEdge(payload);
      ElMessage.success("关系新增成功");
    }

    edgeDialog.visible = false;
    await reloadAll();
  } catch (error) {
    ElMessage.error(error?.message || "关系保存失败");
  } finally {
    edgeDialog.saving = false;
  }
};

const removeEdge = async (row) => {
  try {
    await ElMessageBox.confirm("确认删除该关系吗？", "删除关系", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
    await deleteKnowledgeGraphEdge(row.id);
    ElMessage.success("关系删除成功");
    await reloadAll();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.message || "关系删除失败");
    }
  }
};

const openIndexDialog = () => {
  Object.assign(indexForm, {
    nodeId: selectedNode.value?.id,
    materialId: undefined,
  });
  indexDialog.visible = true;
};

const submitIndex = async () => {
  if (!indexForm.nodeId || !indexForm.materialId) {
    ElMessage.warning("请填写节点 ID 与资料 ID");
    return;
  }
  if (!currentCourseSectionId.value) {
    ElMessage.warning("当前课程系列 ID 无效");
    return;
  }

  indexDialog.saving = true;
  try {
    await addKnowledgeGraphNodeIndex({
      nodeId: Number(indexForm.nodeId),
      materialId: Number(indexForm.materialId),
      courseSectionId: Number(currentCourseSectionId.value),
    });
    ElMessage.success("索引新增成功");
    indexDialog.visible = false;
    await reloadAll();
  } catch (error) {
    ElMessage.error(error?.message || "索引新增失败");
  } finally {
    indexDialog.saving = false;
  }
};

const removeIndex = async (row) => {
  try {
    await ElMessageBox.confirm("确认删除该索引吗？", "删除索引", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });
    await deleteKnowledgeGraphNodeIndex(row.id);
    ElMessage.success("索引删除成功");
    await reloadAll();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.message || "索引删除失败");
    }
  }
};

const removeGraph = async () => {
  if (!currentCourseSectionId.value) {
    ElMessage.warning("当前课程系列 ID 无效，无法删除图谱");
    return;
  }

  try {
    await ElMessageBox.confirm("确认删除该章节的整张知识图谱吗？", "删除图谱", {
      type: "warning",
      confirmButtonText: "删除",
      cancelButtonText: "取消",
    });

    deletingGraph.value = true;
    await deleteKnowledgeGraphByCourseSectionId(Number(currentCourseSectionId.value));
    ElMessage.success("图谱删除成功");
    await reloadAll();
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error(error?.message || "图谱删除失败");
    }
  } finally {
    deletingGraph.value = false;
  }
};

const onImportFileChange = (file) => {
  importFile.value = file.raw;
};

const onImportFileRemove = () => {
  importFile.value = null;
};

const submitImport = async () => {
  if (!currentCourseSectionId.value) {
    ElMessage.warning("当前课程系列 ID 无效，无法导入");
    return;
  }
  if (!importFile.value) {
    ElMessage.warning("请先选择 xlsx 文件");
    return;
  }

  importing.value = true;
  try {
    await importKnowledgeGraphFromExcel(importFile.value, Number(currentCourseSectionId.value));
    ElMessage.success("导入成功");
    importDialogVisible.value = false;
    importFile.value = null;
    await reloadAll();
  } catch (error) {
    ElMessage.error(error?.message || "导入失败");
  } finally {
    importing.value = false;
  }
};

const zoomInGraph = () => {
  graphZoom.value = Math.min(3, Number((graphZoom.value + 0.2).toFixed(2)));
  renderChart();
};

const zoomOutGraph = () => {
  graphZoom.value = Math.max(0.4, Number((graphZoom.value - 0.2).toFixed(2)));
  renderChart();
};

const resetGraphViewport = () => {
  graphZoom.value = 1;
  renderChart();
};

const toggleGraphRoam = () => {
  graphRoamEnabled.value = !graphRoamEnabled.value;
  renderChart();
};

watch([displayNodes, displayEdges, activeRootId], () => {
  const visibleSet = new Set(displayNodes.value.map((item) => Number(item.id)));
  if (selectedNodeId.value && !visibleSet.has(Number(selectedNodeId.value))) {
    selectedNodeId.value = null;
  }
  nextTick(() => renderChart());
});

watch(currentCourseSectionId, async () => {
  await reloadAll();
});

onMounted(async () => {
  await reloadAll();
  window.addEventListener("resize", renderChart);
});

onUnmounted(() => {
  window.removeEventListener("resize", renderChart);
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});
</script>

<style scoped>
.knowledge-graph-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px;
  background: linear-gradient(180deg, var(--bg-light) 0%, var(--bg-primary-grey) 100%);
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid var(--border-light);
  border-radius: 10px;
  background: var(--bg-white);
}

.title-group h2 {
  margin: 0;
  color: var(--primary-dark);
  font-size: 22px;
  line-height: 1.2;
}

.title-group p {
  margin: 6px 0 0;
  color: var(--text-regular);
  font-size: 13px;
}

.header-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.toolbar-card {
  border: 1px solid var(--border-light);
}

.toolbar-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.toolbar-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toolbar-label {
  color: var(--text-primary);
  font-size: 13px;
  white-space: nowrap;
}

.main-grid {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 250px minmax(0, 1fr) 420px;
  gap: 12px;
}

.toc-card,
.graph-card,
.info-card,
.manager-card {
  border: 1px solid var(--border-light);
}

.toc-card :deep(.el-card__body) {
  height: calc(100% - 56px);
  overflow: auto;
}

.toc-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.toc-item {
  width: 100%;
  justify-content: space-between;
}

.toc-list :deep(.el-button + .el-button) {
  margin-left: 0;
}

.toc-name {
  max-width: 130px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.toc-level {
  opacity: 0.8;
  font-size: 12px;
}

.graph-card :deep(.el-card__body) {
  height: calc(100% - 56px);
  padding: 8px;
  display: flex;
  flex-direction: column;
}

.graph-canvas {
  width: 100%;
  flex: 1;
  min-height: 360px;
  border-radius: 10px;
  border: 1px solid var(--primary-alpha-20);
  background:
    radial-gradient(circle at 12% 18%, var(--primary-alpha-10), transparent 40%),
    radial-gradient(circle at 86% 82%, var(--primary-alpha-10), transparent 38%),
    var(--bg-white);
}

.graph-controls {
  margin-top: 10px;
  padding-top: 8px;
  border-top: 1px dashed var(--border-primary-lighter);
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.side-column {
  min-height: 0;
  display: grid;
  grid-template-rows: 280px minmax(0, 1fr);
  gap: 12px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: var(--text-primary);
  font-weight: 600;
}

.info-card :deep(.el-card__body) {
  height: calc(100% - 56px);
  overflow: auto;
}

.node-detail {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  color: var(--text-primary);
  font-size: 13px;
}

.detail-row.block {
  flex-direction: column;
  align-items: flex-start;
}

.detail-row p {
  margin: 0;
  color: var(--text-regular);
  line-height: 1.5;
}

.tag-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.empty-text {
  color: var(--text-placeholder);
  font-size: 12px;
}

.manager-card :deep(.el-card__body) {
  height: calc(100% - 56px);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.manager-card :deep(.el-tabs) {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.manager-card :deep(.el-tabs__content) {
  flex: 1;
  min-height: 0;
}

.manager-card :deep(.el-tab-pane) {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tab-actions {
  display: flex;
  justify-content: flex-end;
}

.upload-tip {
  color: var(--text-placeholder);
  font-size: 12px;
}

:deep(.el-button--primary) {
  --el-button-bg-color: var(--primary);
  --el-button-border-color: var(--primary);
  --el-button-hover-bg-color: var(--primary-hover);
  --el-button-hover-border-color: var(--primary-hover);
  --el-button-active-bg-color: var(--primary-dark);
  --el-button-active-border-color: var(--primary-dark);
}

:deep(.el-tag.el-tag--primary) {
  --el-tag-bg-color: var(--primary-alpha-10);
  --el-tag-border-color: var(--primary-alpha-20);
  --el-tag-text-color: var(--primary-dark);
}

@media (max-width: 1280px) {
  .main-grid {
    grid-template-columns: 220px minmax(0, 1fr);
  }

  .side-column {
    grid-column: 1 / -1;
    grid-template-columns: 1fr;
    grid-template-rows: auto auto;
  }

  .graph-canvas {
    min-height: 420px;
  }
}

@media (max-width: 768px) {
  .knowledge-graph-page {
    padding: 10px;
  }

  .main-grid {
    grid-template-columns: minmax(0, 1fr);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .toolbar-row {
    gap: 12px;
  }

  .toolbar-item {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
