<template>
  <div class="prompt-editor">
    <el-card class="editor-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div class="title-wrap">
            <h2>提示词管理</h2>
            <p>查看、维护并指定运行时提示词。</p>
          </div>
          <el-button type="primary" @click="openCreateDialog">新增提示词</el-button>
        </div>
      </template>

      <el-table
        v-loading="tableLoading"
        :data="promptList"
        stripe
        border
        highlight-current-row
        @current-change="handleCurrentRowChange"
        class="prompt-table"
      >
        <el-table-column prop="id" label="ID" width="90" />
        <el-table-column prop="description" label="描述" min-width="260" show-overflow-tooltip />
        <el-table-column prop="creatorId" label="创建者" min-width="140" />

        <el-table-column label="查看提示词" width="120" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleViewPrompt(row)">查看</el-button>
          </template>
        </el-table-column>

        <el-table-column label="操作" min-width="260" align="center" fixed="right">
          <template #default="{ row }">
            <div class="action-group">
              <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
              <el-button link type="danger" @click="openDeleteDialog(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="runtime-actions">
        <el-button
          type="success"
          :disabled="runtimeCooldownActive || !selectedPromptRow"
          @click="handleApplySelectedPrompt"
        >
          选择表格一行应用提示词
        </el-button>
        <el-button
          type="warning"
          plain
          :disabled="runtimeCooldownActive"
          @click="handleRestoreDefaultPrompt"
        >
          恢复默认提示词
        </el-button>
      </div>

      <div class="cooldown-tip" v-if="runtimeCooldownActive">
        运行时提示词切换冷却中，请在 {{ runtimeCooldownText }} 后重试。
      </div>

      <div class="runtime-panel">
        <div class="runtime-title">当前运行时提示词文本</div>
        <div class="runtime-markdown" v-html="runtimePromptHtml"></div>
      </div>
    </el-card>

    <el-dialog
      v-model="viewDialogVisible"
      title="提示词内容"
      width="760px"
      destroy-on-close
    >
      <el-skeleton :loading="viewLoading" animated :rows="8">
        <template #default>
          <div class="view-meta">ID: {{ currentViewInfo.id }}</div>
          <el-input
            :model-value="currentPromptText"
            type="textarea"
            :rows="14"
            resize="none"
            readonly
          />
        </template>
      </el-skeleton>
    </el-dialog>

    <el-dialog
      v-model="editDialogVisible"
      :title="dialogMode === 'create' ? '新增提示词' : '编辑提示词'"
      width="760px"
      destroy-on-close
    >
      <el-form ref="promptFormRef" :model="promptForm" :rules="promptFormRules" label-width="90px">
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="promptForm.description"
            type="textarea"
            :rows="3"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="提示词" prop="promptString">
          <el-input
            v-model="promptForm.promptString"
            type="textarea"
            :rows="10"
            resize="vertical"
            maxlength="500"
            show-word-limit
            placeholder="请输入提示词正文，最多500字"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitPrompt">
          {{ dialogMode === 'create' ? '新增' : '保存' }}
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="deleteDialogVisible" title="删除提示词" width="420px">
      <div class="delete-message">
        确定删除提示词“{{ deleteTarget?.name || '-' }}”吗？删除后不可恢复。
      </div>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="deleteLoading" @click="handleDeletePrompt">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { marked } from "marked";
import { useUserStore } from "@/store/modules/auth/user";
import {
  addPrompt,
  deletePrompt,
  getPromptInfos,
  getRuntimePrompt,
  loadPrompt,
  setRuntimePrompt,
  updatePrompt,
} from "@/api/modules/teaching/PromptAPI";

const COOLDOWN_SECONDS = 60;
const COOLDOWN_STORAGE_KEY = "promptRuntimeCooldownEnd";

const userStore = useUserStore();

const tableLoading = ref(false);
const promptList = ref([]);
const selectedPromptRow = ref(null);

const viewDialogVisible = ref(false);
const viewLoading = ref(false);
const currentPromptText = ref("");
const currentViewInfo = reactive({
  id: null,
});
const promptContentCache = reactive({});

const editDialogVisible = ref(false);
const dialogMode = ref("create");
const submitLoading = ref(false);
const promptFormRef = ref();
const promptForm = reactive({
  id: null,
  name: null,
  description: "",
  creatorId: "",
  promptString: "",
});

const promptFormRules = {
  creatorId: [{ required: true, message: "创建者不能为空", trigger: "blur" }],
  promptString: [
    { required: true, message: "提示词正文不能为空", trigger: "blur" },
    { max: 500, message: "提示词正文最多500字", trigger: "blur" },
  ],
};

const deleteDialogVisible = ref(false);
const deleteLoading = ref(false);
const deleteTarget = ref(null);
const runtimePromptText = ref("");

const cooldownEnd = ref(Number(sessionStorage.getItem(COOLDOWN_STORAGE_KEY) || 0));
const currentTime = ref(Date.now());
let cooldownTimer = null;

const runtimeCooldownRemain = computed(() => {
  const seconds = Math.ceil((cooldownEnd.value - currentTime.value) / 1000);
  return Math.max(0, seconds);
});

const runtimeCooldownActive = computed(() => runtimeCooldownRemain.value > 0);

const runtimeCooldownText = computed(() => {
  const remain = runtimeCooldownRemain.value;
  const minute = Math.floor(remain / 60);
  const second = remain % 60;
  return `${minute}分${String(second).padStart(2, "0")}秒`;
});

const runtimePromptHtml = computed(() => {
  if (!runtimePromptText.value) {
    return "<p>当前尚未设置运行时提示词</p>";
  }

  return marked.parse(runtimePromptText.value, {
    gfm: true,
    breaks: true,
  });
});

onMounted(() => {
  fetchPromptList();
  fetchRuntimePromptText();
  cooldownTimer = window.setInterval(() => {
    currentTime.value = Date.now();
  }, 1000);
});

onBeforeUnmount(() => {
  if (cooldownTimer) {
    window.clearInterval(cooldownTimer);
    cooldownTimer = null;
  }
});

async function fetchPromptList() {
  tableLoading.value = true;
  try {
    const res = await getPromptInfos();
    if (res.code === 200) {
      promptList.value = Array.isArray(res.data) ? res.data : [];
      selectedPromptRow.value = null;
      return;
    }
    ElMessage.error(res.message || "加载提示词列表失败");
  } catch (error) {
    console.error("Failed to load prompt list:", error);
    ElMessage.error("加载提示词列表失败");
  } finally {
    tableLoading.value = false;
  }
}

function handleCurrentRowChange(row) {
  selectedPromptRow.value = row || null;
}

async function handleViewPrompt(row) {
  viewDialogVisible.value = true;
  viewLoading.value = true;
  currentViewInfo.id = row.id;
  currentPromptText.value = "";

  try {
    if (promptContentCache[row.id]) {
      currentPromptText.value = promptContentCache[row.id];
      return;
    }

    const res = await loadPrompt(row.id);
    if (res.code === 200) {
      currentPromptText.value = res.data || "";
      promptContentCache[row.id] = currentPromptText.value;
      return;
    }
    ElMessage.error(res.message || "加载提示词内容失败");
  } catch (error) {
    console.error("Failed to load prompt content:", error);
    ElMessage.error("加载提示词内容失败");
  } finally {
    viewLoading.value = false;
  }
}

function openCreateDialog() {
  dialogMode.value = "create";
  resetPromptForm();
  editDialogVisible.value = true;
}

async function openEditDialog(row) {
  dialogMode.value = "edit";
  resetPromptForm();
  promptForm.id = row.id;
  promptForm.description = row.description || "";
  promptForm.creatorId = row.creatorId || "";
  editDialogVisible.value = true;

  try {
    submitLoading.value = true;
    if (promptContentCache[row.id]) {
      promptForm.promptString = promptContentCache[row.id];
      return;
    }

    const res = await loadPrompt(row.id);
    if (res.code === 200) {
      promptForm.promptString = res.data || "";
      promptContentCache[row.id] = promptForm.promptString;
      return;
    }
    ElMessage.error(res.message || "加载提示词内容失败");
  } catch (error) {
    console.error("Failed to load editor data:", error);
    ElMessage.error("加载编辑内容失败");
  } finally {
    submitLoading.value = false;
  }
}

function resetPromptForm() {
  promptForm.id = null;
  promptForm.description = "";
  promptForm.creatorId = userStore.userId || "";
  promptForm.promptString = "";
  promptFormRef.value?.clearValidate?.();
}

async function handleSubmitPrompt() {
  try {
    await promptFormRef.value.validate();
  } catch {
    return;
  }

  submitLoading.value = true;
  const creatorId = userStore.userId ? String(userStore.userId).trim() : "";
  if (!creatorId) {
    ElMessage.error("未获取到当前用户ID，无法提交");
    submitLoading.value = false;
    return;
  }

  const promptInfo = {
    id: dialogMode.value === "edit" ? promptForm.id : null,
    name: null,
    description: promptForm.description.trim(),
    creatorId,
  };
  const promptString = promptForm.promptString.trim();

  try {
    const res =
      dialogMode.value === "create"
        ? await addPrompt(promptInfo, promptString)
        : await updatePrompt(promptInfo, promptString);

    if (res.code === 200) {
      ElMessage.success(dialogMode.value === "create" ? "新增提示词成功" : "更新提示词成功");
      editDialogVisible.value = false;
      if (dialogMode.value === "edit" && promptForm.id) {
        delete promptContentCache[promptForm.id];
      }
      await fetchPromptList();
      return;
    }
    ElMessage.error(res.message || "保存提示词失败");
  } catch (error) {
    console.error("Failed to save prompt:", error);
    ElMessage.error("保存提示词失败");
  } finally {
    submitLoading.value = false;
  }
}

function openDeleteDialog(row) {
  deleteTarget.value = row;
  deleteDialogVisible.value = true;
}

async function handleDeletePrompt() {
  if (!deleteTarget.value?.id) {
    return;
  }

  deleteLoading.value = true;
  try {
    const res = await deletePrompt(deleteTarget.value.id);
    if (res.code === 200) {
      ElMessage.success("删除提示词成功");
      deleteDialogVisible.value = false;
      delete promptContentCache[deleteTarget.value.id];
      deleteTarget.value = null;
      await fetchPromptList();
      return;
    }
    ElMessage.error(res.message || "删除提示词失败");
  } catch (error) {
    console.error("Failed to delete prompt:", error);
    ElMessage.error("删除提示词失败");
  } finally {
    deleteLoading.value = false;
  }
}

async function handleSetRuntime(row) {
  if (runtimeCooldownActive.value || !row?.id) {
    return;
  }

  try {
    const res = await setRuntimePrompt(row.id);
    if (res.code === 200) {
      ElMessage.success("运行时提示词应用成功");
      await fetchRuntimePromptText();
      startRuntimeCooldown();
      return;
    }
    ElMessage.error(res.message || "设置运行时提示词失败");
  } catch (error) {
    console.error("Failed to set runtime prompt:", error);
    ElMessage.error("设置运行时提示词失败");
  }
}

async function handleApplySelectedPrompt() {
  if (!selectedPromptRow.value?.id) {
    ElMessage.warning("请先在表格中选择一条提示词");
    return;
  }
  await handleSetRuntime(selectedPromptRow.value);
}

async function handleRestoreDefaultPrompt() {
  if (runtimeCooldownActive.value) {
    return;
  }

  try {
    const res = await setRuntimePrompt(0);
    if (res.code === 200) {
      ElMessage.success("已恢复默认提示词");
      await fetchRuntimePromptText();
      startRuntimeCooldown();
      return;
    }
    ElMessage.error(res.message || "恢复默认提示词失败");
  } catch (error) {
    console.error("Failed to restore default prompt:", error);
    ElMessage.error("恢复默认提示词失败");
  }
}

function startRuntimeCooldown() {
  cooldownEnd.value = Date.now() + COOLDOWN_SECONDS * 1000;
  currentTime.value = Date.now();
  sessionStorage.setItem(COOLDOWN_STORAGE_KEY, String(cooldownEnd.value));
}

async function fetchRuntimePromptText() {
  try {
    const res = await getRuntimePrompt();
    if (res.code === 200) {
      runtimePromptText.value = res.data || "";
      return;
    }

    runtimePromptText.value = "";
  } catch (error) {
    console.error("Fetch runtime prompt text failed:", error);
    runtimePromptText.value = "";
  }
}
</script>

<style lang="scss" scoped>
.prompt-editor {
  padding: 20px;
  min-height: calc(100vh - 40px);
  background: linear-gradient(180deg, var(--bg-primary-light) 0%, var(--bg-white) 60%);
}

.editor-card {
  border: 1px solid var(--border-light);
  border-radius: 12px;
  background-color: var(--bg-white);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.title-wrap {
  h2 {
    margin: 0;
    font-size: 20px;
    color: var(--text-primary);
  }

  p {
    margin: 6px 0 0;
    font-size: 13px;
    color: var(--text-regular);
  }
}

.prompt-table {
  :deep(.el-table__header th) {
    background-color: var(--bg-primary-light);
    color: var(--text-primary);
  }

  :deep(.el-button.is-link) {
    font-weight: 600;
  }
}

.action-group {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.cooldown-tip {
  margin-top: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  background-color: var(--primary-alpha-10);
  color: var(--primary-dark);
  font-size: 13px;
}

.runtime-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 14px;
}

.runtime-panel {
  margin-top: 12px;
  padding: 14px;
  border: 1px solid var(--border-light);
  border-radius: 10px;
  background-color: var(--bg-light);
}

.runtime-markdown {
  min-height: 40vh;
  max-height: 40vh;
  padding: 12px;
  border: 1px solid var(--border-light);
  border-radius: 8px;
  background-color: var(--bg-white);
  color: var(--text-primary);
  line-height: 1.75;
  overflow: auto;

  :deep(p) {
    margin: 0 0 10px;
  }

  :deep(h1),
  :deep(h2),
  :deep(h3),
  :deep(h4),
  :deep(h5),
  :deep(h6) {
    margin: 12px 0 8px;
    color: var(--text-primary);
  }

  :deep(ul),
  :deep(ol) {
    margin: 8px 0;
    padding-left: 20px;
  }

  :deep(code) {
    font-family: Consolas, Monaco, monospace;
  }

  :deep(pre) {
    margin: 10px 0;
    padding: 10px;
    border-radius: 6px;
    background-color: var(--bg-primary-grey);
    overflow-x: auto;
  }
}

.runtime-title {
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.view-meta {
  margin-bottom: 10px;
  color: var(--text-regular);
  font-size: 13px;
}

.delete-message {
  color: var(--text-primary);
  line-height: 1.7;
}

@media (max-width: 768px) {
  .prompt-editor {
    padding: 12px;
  }

  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .action-group {
    flex-wrap: wrap;
    justify-content: center;
  }

  .runtime-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>