<template>
  <div class="practice-index-container">
    <!-- 左侧练习列表 -->
    <div class="practice-panel" :class="{ 'is-scrolling': isScrolling }" @scroll="handleScroll">
      <div class="scrollable-content">
        <div class="panel-header">
          <div class="header-left">
            <h3>练习任务</h3>
            <div class="stats">
              <span>共 {{ allPractices.length }} 个练习</span>
            </div>
          </div>
          <div class="header-right">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[6, 12, 18]"
              layout="prev, pager, next"
              :total="allPractices.length"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              small
              background
            />
          </div>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-state">
          <el-icon class="loading-icon"><Cpu /></el-icon>
          <p>正在加载练习数据...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-state">
          <el-icon class="error-icon"><Warning /></el-icon>
          <p>加载失败，请稍后重试</p>
          <el-button type="primary" size="small" @click="fetchPractices">
            重新加载
          </el-button>
        </div>

        <!-- 空状态 -->
        <div v-else-if="allPractices.length === 0" class="empty-state">
          <el-icon class="empty-icon"><Edit /></el-icon>
          <p>暂无练习任务</p>
        </div>

        <!-- 练习列表 -->
        <div v-else class="practice-items">
          <div 
            v-for="(practice, index) in paginatedPractices" 
            :key="practice.id" 
            class="practice-item"
            :class="{ active: selectedPractice === index }"
            @click="selectPractice(index)"
          >
            <div class="practice-icon">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="practice-info">
              <h4>{{ practice.title }}</h4>
              <p>{{ practice.description }}</p>
              <div class="practice-meta">
                <span class="deadline" v-if="practice.deadline">
                  <el-icon><Clock /></el-icon>
                  截止: {{ formatDeadline(practice.deadline) }}
                </span>
                <span class="deadline" v-else>
                  <el-icon><Clock /></el-icon>
                  无截止时间
                </span>
                <div class="practice-status">
                  <el-tag type="info" size="small" v-if="selectedPractice === index && currentPracticeQuestionCount > 0">
                    {{ currentPracticeQuestionCount }} 题
                  </el-tag>
                  <el-tag type="info" size="small" v-else>未开始</el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧详情预览 -->
    <div class="preview-panel">
      <!-- 加载状态 -->
      <div v-if="loadingQuestions" class="loading-state">
        <el-icon class="loading-icon"><Cpu /></el-icon>
        <p>正在加载练习详情...</p>
      </div>
      
      <div v-else-if="selectedPractice !== null && paginatedPractices[selectedPractice]" class="practice-preview">
        <el-card class="preview-card">
          <template #header>
            <div class="preview-header">
              <h4>{{ paginatedPractices[selectedPractice].title }}</h4>
              <el-button type="primary" @click="handleClick('practice')">
                开始练习
              </el-button>
            </div>
          </template>

          <div class="preview-content">
            <div class="preview-section">
              <h5>练习信息</h5>
              <p>
                本练习包含 {{ paginatedPractices[selectedPractice].questionNum }} 个算法题目，
                旨在帮助您掌握基础算法思想和编程技巧。
              </p>
              <div v-if="paginatedPractices[selectedPractice].deadline" class="deadline-info">
                <el-icon><Clock /></el-icon>
                截止时间: {{ new Date(paginatedPractices[selectedPractice].deadline).toLocaleString() }}
                <el-tag 
                  :type="new Date(paginatedPractices[selectedPractice].deadline) < new Date() ? 'danger' : 'info'"
                  size="small"
                >
                  {{ formatDeadline(paginatedPractices[selectedPractice].deadline) }}
                </el-tag>
              </div>
              <div v-else class="deadline-info">
                <el-icon><Clock /></el-icon>
                截止时间: 长期有效
                <el-tag type="success" size="small">
                  长期有效
                </el-tag>
              </div>
            </div>

            <div class="preview-section">
              <h5>练习目标</h5>
              <ul>
                <li>掌握基础算法设计思路</li>
                <li>提高代码实现能力</li>
                <li>理解时间复杂度分析</li>
                <li>培养编程思维</li>
              </ul>
            </div>

            <div class="preview-section">
              <h5>题目信息</h5>
              <p>本练习共包含 {{ questionsStore.questions.length }} 道题目。</p>
              <div v-if="questionsStore.questions.length > 0">
                <el-tag type="info" size="small">{{ questionsStore.questions.length }} 道题</el-tag>
              </div>
            </div>

            <div class="preview-section">
              <h5>难度分布</h5>
              <p>题目难度循序渐进，从简单到复杂，适合不同水平的学员。</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-if="selectedPractice === null" class="empty-preview">
        <el-icon size="60">
          <Cpu />
        </el-icon>
        <p>请选择练习查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Cpu, Clock, Warning, Edit } from "@element-plus/icons-vue";
import { ElCard, ElIcon, ElPagination, ElButton, ElTag, ElMessage } from "element-plus";
import { computed, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getPractices } from "@/api/modules/practice/practice";
import { getPracticeIndexes } from "@/api/modules/practice/practiceIndex";
import { getQuestionByIndex } from "@/api/modules/practice/question";
import { useQuestionsStore, usePracticeStore } from "@/store";

const router = useRouter();
const questionsStore = useQuestionsStore();
const practiceStore = usePracticeStore();

// 选中状态 - 使用 store 的 selectedPractice
const selectedPractice = computed(() => practiceStore.selectedPractice);
const loadingQuestions = ref(false);

// 滚动条显示控制
const isScrolling = ref(false);
let scrollTimeout = null;

// 练习任务数据 - 从 store 获取
const loading = ref(false);
const error = ref(null);

// 从 store 获取所有练习
const allPractices = computed(() => practiceStore.practices);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(6);

const paginatedPractices = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return allPractices.value.slice(startIndex, startIndex + pageSize.value);
});

// 当前选中练习的题目数量
const currentPracticeQuestionCount = computed(() => {
  return questionsStore.questions.length;
});

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};



// 选择练习
const selectPractice = async (index) => {
  // 如果选择的练习与当前已选练习相同，则不需要重新获取数据
  if (selectedPractice.value === index) {
    return;
  }
  
  // 使用 store 的 action 设置选中状态
  practiceStore.setSelectedPractice(index);
  const practice = paginatedPractices.value[index];
  
  if (!practice) {
    return;
  }
  
  const practiceId = practice.id;
  
  // 设置加载状态
  loadingQuestions.value = true;
  
  try {
    // 使用练习ID获取问题
    const response = await getQuestionByIndex(practiceId);
    if (response.code === 200 && response.data) {
      // 将获取到的问题存入questionStore，store会自动处理结构转换
      questionsStore.setQuestions(response.data);
    } else {
      ElMessage.warning('获取题目失败: ' + response.message);
    }
  } catch (error) {
    console.error('获取题目失败:', error);
    ElMessage.error('获取题目失败，请稍后重试');
  } finally {
    loadingQuestions.value = false;
  }
};

// 滚动事件处理
const handleScroll = () => {
  // 显示滚动条
  isScrolling.value = true;
  
  // 清除之前的定时器
  if (scrollTimeout) {
    clearTimeout(scrollTimeout);
  }
  
  // 设置新的定时器，滚动停止1秒后隐藏滚动条
  scrollTimeout = setTimeout(() => {
    isScrolling.value = false;
  }, 1000);
};

// 获取练习数据
const fetchPractices = async () => {
  loading.value = true;
  error.value = null;

  try {
    // 步骤1: 获取所有练习索引信息，建立practiceId到courseSectionId的映射
    const practiceIndexesResponse = await getPracticeIndexes({});
    
    if (practiceIndexesResponse.code !== 200 || !practiceIndexesResponse.data) {
      throw new Error('获取练习索引信息失败');
    }

    // 创建practiceId到课程信息的映射
    const practiceIndexMap = new Map();
    practiceIndexesResponse.data.forEach(index => {
      if (index.practiceId) {
        practiceIndexMap.set(index.practiceId, {
          courseSectionId: index.courseSectionId,
          courseId: index.courseId || 0
        });
      }
    });

    // 步骤2: 如果没有练习索引，直接返回空列表
    if (practiceIndexMap.size === 0) {
      practiceStore.resetPractices([]);
      return;
    }

    // 步骤3: 获取所有可用的练习数据
    const currentTime = new Date().toISOString();
    const response = await getPractices({
      createdAtEnd: currentTime // 获取创建日期在当前时间之前的练习
    });
    
    if (response.code === 200 && response.data) {
      // 步骤4: 过滤和转换练习数据，只保留有索引信息的练习
      const fetchedPractices = response.data
        .filter(practice => {
          // 只保留有索引信息的练习
          if (!practiceIndexMap.has(practice.id)) {
            return false;
          }
          // 确保创建时间在当前时间之前
          if (!practice.createdAt) return true;
          return new Date(practice.createdAt) <= new Date(currentTime);
        })
        .sort((a, b) => {
          // 按截止时间排序，截止时间越晚顺序越前
          if (!a.expiredAt && !b.expiredAt) return 0;
          if (!a.expiredAt) return -1; // 没有截止时间的排在后面
          if (!b.expiredAt) return 1;
          return new Date(b.expiredAt) - new Date(a.expiredAt);
        })
        .map((practice) => {
          const practiceInfo = practiceIndexMap.get(practice.id);
          return {
            id: practice.id.toString(),
            courseSectionId: practiceInfo.courseSectionId,
            courseId: practiceInfo.courseId,
            title: practice.name,
            description: `包含 ${practice.questionNum} 个问题的练习`,
            requirement: "",
            deadline: practice.expiredAt ? new Date(practice.expiredAt).toISOString() : null,
            status: "未开始",
            score: null,
            totalScore: 100,
            difficulty: 1,
            questionNum: practice.questionNum || 0,
            createTime: practice.createdAt || new Date().toISOString()
          };
        });

      // 步骤5: 更新practiceStore中的练习列表
      practiceStore.resetPractices(fetchedPractices);

      // 步骤6: 如果获取到了练习，默认选择第一个
      if (allPractices.value.length > 0) {
        selectPractice(0);
      }
    } else {
      ElMessage.error('获取练习数据失败: ' + response.message);
    }
  } catch (err) {
    error.value = err;
    ElMessage.error('获取练习数据失败，请稍后重试');
    console.error('获取练习数据失败:', err);
  } finally {
    loading.value = false;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchPractices();
});

// 格式化截止时间
const formatDeadline = (deadline) => {
  if (!deadline) return '长期有效';
  
  const date = new Date(deadline);
  const now = new Date();
  const diff = date - now;
  
  if (diff < 0) return '已过期';
  
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  
  if (days > 0) return `${days}天后`;
  if (hours > 0) return `${hours}小时后`;
  return '即将截止';
};

const handleClick = (item) => {
  switch (item) {
    case "practice":
      // 获取当前选中的练习数据
      if (selectedPractice.value !== null && paginatedPractices.value[selectedPractice.value]) {
        const selectedPracticeData = paginatedPractices.value[selectedPractice.value];
        
        // 确保有有效的courseSectionId
        if (!selectedPracticeData.courseSectionId) {
          ElMessage.error('练习数据不完整，无法进入练习');
          return;
        }
        
        // 导航到练习页面，传递课程章节ID和练习ID
        router.push({
          name: "Practice",
          params: {
            courseSectionId: selectedPracticeData.courseSectionId, // 使用练习索引中的课程章节ID
            practiceId: selectedPracticeData.id
          },
        });
      } else {
        // 如果没有选中练习，显示提示信息
        ElMessage.warning('请先选择一个练习');
      }
      break;

  }
};
</script>

<style scoped>
.practice-index-container {
  display: flex;
  gap: 20px;
  height: 100%;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

/* 左侧练习面板 */
.practice-panel {
  flex: 0 0 350px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 0; /* 移除内边距，由内层容器控制 */
  overflow-y: auto;
  backdrop-filter: blur(10px);
  /* 自定义滚动条 - 默认隐藏 */
  scrollbar-width: thin;
  scrollbar-color: transparent transparent;
  transition: scrollbar-color 0.3s ease;
  /* 防止滚动条挤压内容 */
  overflow-y: overlay; /* 现代浏览器支持 */
  scrollbar-gutter: stable; /* 预留滚动条空间，不挤压内容 */
  position: relative;
}

/* 滚动时显示滚动条 */
.practice-panel.is-scrolling {
  scrollbar-color: rgba(144, 147, 153, 0.5) transparent;
}

/* Webkit浏览器滚动条样式 */
.practice-panel::-webkit-scrollbar {
  width: 6px;
}

/* 滚动条轨道 */
.practice-panel::-webkit-scrollbar-track {
  background: transparent;
}

/* 滚动条滑块 */
.practice-panel::-webkit-scrollbar-thumb {
  background-color: transparent;
  border-radius: 3px;
  transition: background-color 0.3s ease;
}

/* 滚动时显示滚动条滑块 */
.practice-panel.is-scrolling::-webkit-scrollbar-thumb {
  background-color: rgba(144, 147, 153, 0.5);
}

/* 移除滚动条按钮（上下箭头） */
.practice-panel::-webkit-scrollbar-button {
  display: none;
}

/* 滚动条角落 */
.practice-panel::-webkit-scrollbar-corner {
  background: transparent;
}

/* 内容容器，确保宽度不被滚动条影响 */
.practice-panel > * {
  width: calc(100% - 6px); /* 减去滚动条宽度 */
}

/* 内层容器，防止滚动条挤压内容 */
.practice-panel .scrollable-content {
  width: 100%;
  box-sizing: border-box;
  padding: 20px; /* 从内层容器控制内边距 */
  padding-right: 14px; /* 20px - 6px滚动条宽度，确保内容不被挤压 */
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-left h3 {
  margin: 0;
  color: #409eff;
  font-size: 18px;
  font-weight: 700;
}

.stats {
  color: #909399;
  font-size: 12px;
}

.header-right {
  display: flex;
  align-items: center;
}

/* 自定义分页样式 */
:deep(.el-pagination) {
  padding: 0;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-light);
  margin: 0 2px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

:deep(.el-pagination .btn-prev:hover),
:deep(.el-pagination .btn-next:hover),
:deep(.el-pagination .el-pager li:hover) {
  background: #f5f7fa;
  border-color: #409eff;
  color: #409eff;
}

:deep(.el-pagination .el-pager li.active) {
  background: var(--gradient-brand);
  border-color: var(--primary);
  color: white;
}

.practice-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.practice-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e5e7eb;
}

.practice-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
}

.practice-item.active {
  background: linear-gradient(135deg, #e6f2ff 0%, #f0f9ff 100%);
  border-color: #409eff;
}

.practice-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 20px;
  background: linear-gradient(135deg, #e6f2ff, #c6e2ff);
  color: #409eff;
  flex-shrink: 0;
}

.practice-info {
  flex: 1;
}

.practice-info h4 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 15px;
  font-weight: 600;
}

.practice-info p {
  margin: 0 0 10px 0;
  color: var(--text-placeholder);
  font-size: 13px;
  line-height: 1.4;
}

.practice-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
}

.practice-status {
  display: flex;
  gap: 4px;
}

.difficulty {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #909399;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 右侧预览面板 */
.preview-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
  backdrop-filter: blur(10px);
}

.practice-preview {
  margin-bottom: 20px;
}

.preview-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.preview-header h4 {
  margin: 0;
  color: var(--primary);
  font-size: 18px;
  font-weight: 700;
}

.preview-content {
  color: #606266;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-section h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.preview-section p,
.preview-section ul {
  margin: 0;
  font-size: 14px;
  line-height: 1.6;
}

.preview-section ul {
  padding-left: 20px;
}

.preview-section li {
  margin-bottom: 5px;
}

.deadline-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  padding: 8px 12px;
  background: var(--bg-primary-alpha);
  border-radius: 6px;
  font-size: 13px;
  color: var(--text-regular);
}



/* 空状态 */
.empty-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: var(--text-placeholder);
}

.empty-preview .el-icon {
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 加载、错误和空状态样式 */
.loading-state,
.error-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--text-placeholder);
  text-align: center;
}

.preview-panel .loading-state {
  height: 200px;
}

.loading-state .loading-icon {
  font-size: 32px;
  margin-bottom: 12px;
  animation: rotate 1s linear infinite;
}

.error-state .error-icon {
  font-size: 32px;
  margin-bottom: 12px;
  color: var(--danger);
}

.empty-state .empty-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .practice-panel {
    flex: 0 0 300px;
  }
}

@media (max-width: 768px) {
  .practice-index-container {
    flex-direction: column;
    padding: 10px;
    gap: 15px;
  }

  .practice-panel {
    flex: none;
    width: 100%;
    padding: 15px;
  }

  .panel-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }

  .header-left {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .header-right {
    justify-content: center;
  }

  :deep(.el-pagination) {
    justify-content: center;
  }
}
</style>
