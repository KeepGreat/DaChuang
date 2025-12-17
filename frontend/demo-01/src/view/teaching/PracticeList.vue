<template>
  <div class="practice-list">
    <!-- 中间练习列表 -->
    <div class="practice-panel">
      <div class="panel-header">
        <h3>练习列表</h3>
        <div class="stats">
          <span>已完成: {{ completedCount }}/{{ totalCount }}</span>
        </div>
      </div>

      <div class="practice-items" v-loading="componentLoading || practiceStore.loading"
        element-loading-text="加载中...">
        <div v-if="componentError || practiceStore.error" class="error-message">
          <el-icon>
            <Warning />
          </el-icon>
          加载失败，请稍后重试
        </div>
        <div v-else-if="practices.length === 0" class="empty-message">
          <el-icon>
            <Edit />
          </el-icon>
          暂无练习
        </div>
        <div v-else>
          <div v-for="(practice, index) in practices" :key="index" class="practice-item" :class="{
            active: selectedPractice === index,
            completed: practice.status === '已提交',
            overdue: isOverdue(practice.deadline)
          }" @click="selectPractice(index)">
            <div class="practice-icon">
              <el-icon v-if="practice.status === '已提交'">
                <Check />
              </el-icon>
              <el-icon v-else-if="isOverdue(practice.deadline)">
                <Warning />
              </el-icon>
              <el-icon v-else>
                <Edit />
              </el-icon>
            </div>
            <div class="practice-info">
              <h4>{{ practice.title }}</h4>
              <p>{{ practice.description }}</p>
              <div class="practice-meta">
                <span class="deadline">
                  <el-icon>
                    <Clock />
                  </el-icon>
                  截止: {{ formatDeadline(practice.deadline) }}
                </span>
                <el-tag :type="getStatusType(practice.status)" size="small">
                  {{ practice.status }}
                </el-tag>
              </div>
            </div>
            <div class="practice-score" v-if="practice.score">
              <span>{{ practice.score }}/{{ practice.totalScore }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧练习详情预览 -->
    <div class="preview-panel">
      <div v-if="selectedPractice !== null" class="practice-preview">
        <el-card class="preview-card">
          <template #header>
            <div class="preview-header">
              <h4>{{ practices[selectedPractice].title }}</h4>
              <el-button type="primary" @click="openPracticeDetail">
                开始做题
              </el-button>
            </div>
          </template>

          <div class="preview-content">
            <div class="preview-section">
              <h5>练习说明</h5>
              <p>{{ practices[selectedPractice].description }}</p>
            </div>

            <div class="preview-section">
              <h5>练习要求</h5>
              <div v-html="practices[selectedPractice].requirement"></div>
            </div>

            <div class="preview-section">
              <h5>评分标准</h5>
              <ul>
                <li>代码规范性: 20分</li>
                <li>算法正确性: 50分</li>
                <li>边界处理: 20分</li>
                <li>时间复杂度: 10分</li>
              </ul>
            </div>

            <div class="preview-section">
              <h5>测试用例概览</h5>
              <p>共 {{ practices[selectedPractice].testCases?.length || 0 }} 个测试用例</p>
            </div>
          </div>
        </el-card>

        <!-- 提交历史卡片 -->
        <el-card class="history-card" v-if="submissionHistory.length > 0">
          <template #header>
            <h5>最近提交</h5>
          </template>
          <div v-for="(record, index) in submissionHistory.slice(0, 3)" :key="index" class="history-item">
            <div class="history-time">{{ record.submitTime }}</div>
            <el-tag :type="getSubmissionType(record.status)" size="small">
              {{ record.status }}
            </el-tag>
            <span v-if="record.score" class="history-score">
              {{ record.score }}分
            </span>
          </div>
        </el-card>
      </div>

      <!-- 空状态 -->
      <div v-else class="empty-preview">
        <el-icon size="60">
          <Edit />
        </el-icon>
        <p>请选择练习查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {
  Edit, Check, Warning, Clock
} from '@element-plus/icons-vue';
import { ElCard, ElButton, ElTag, ElMessage } from 'element-plus';
import { usePracticeStore, useQuestionsStore } from '@/store';
import { getQuestionByIndex, getPracticesByIndex } from '@/api';

const router = useRouter();
const route = useRoute();

// 使用Pinia store
const practiceStore = usePracticeStore();
const questionsStore = useQuestionsStore();

const selectedPractice = ref(null);
const submissionHistory = ref([]);
const componentLoading = ref(false);
const componentError = ref(null);

// 计算当前课程的练习列表
const practices = computed(() => {
  return practiceStore.getPracticesByCourseId(route.params.id);
});

// 加载练习数据
onMounted(async () => {
  componentLoading.value = true;
  componentError.value = null;

  try {
    // 从路由参数中获取课程章节ID，如果没有则使用默认值1
    const courseSectionId = parseInt(route.params.id || 1);

    // 直接调用接口获取练习列表
    const response = await getPracticesByIndex({
      courseSectionId: parseInt(courseSectionId)
    });

    // 转换API返回的Practice数据为Practice格式
    const fetchedPractices = response.data.map(practice => ({
      id: practice.id.toString(),
      courseId: route.params.id,
      title: practice.name,
      description: `包含 ${practice.questionNum} 个问题的练习`,
      requirement: '',
      deadline: practice.expiredAt ? new Date(practice.expiredAt).toISOString() : null,
      status: '未开始',
      score: null,
      totalScore: 100,
      difficulty: 1,
      questionNum: practice.questionNum
    }));

    // 更新practiceStore中的练习列表
    // 直接重置整个练习列表，而不是合并
    practiceStore.resetPractices(fetchedPractices);

    // 如果获取到了练习，默认选择第一个
    if (practices.value.length > 0) {
      selectPractice(0);
    }
  } catch (err) {
    componentError.value = err;
    ElMessage.error('加载练习列表失败');
    console.error('获取练习列表失败:', err);
  } finally {
    componentLoading.value = false;
  }
});

// 计算统计
const totalCount = computed(() => practices.value.length);
const completedCount = computed(() =>
  practices.value.filter(p => p.status === '已提交' || p.status === '部分正确').length
);

// 选择练习
const selectPractice = async (index) => {
  selectedPractice.value = index;
  const practice = practices.value[index];
  const practiceId = practice.id;
  submissionHistory.value = practiceStore.getSubmissionHistory(practiceId);

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
  }
};

// 打开练习详情
const openPracticeDetail = () => {
  const practiceId = practices.value[selectedPractice.value].id;
  const courseId = route.params.id;
  router.push(`/teaching/course/${courseId}/practice/${practiceId}`);
};

// 判断是否过期
const isOverdue = (deadline) => {
  return new Date(deadline) < new Date();
};

// 格式化截止时间
const formatDeadline = (deadline) => {
  const date = new Date(deadline);
  const now = new Date();
  const diff = date - now;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));

  if (diff < 0) return '已过期';
  if (days > 0) return `${days}天后`;
  if (hours > 0) return `${hours}小时后`;
  return '即将截止';
};

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    '未开始': 'info',
    '未提交': 'warning',
    '已提交': 'success',
    '部分正确': 'warning',
    '全部正确': 'success'
  };
  return types[status] || 'info';
};

// 获取提交状态类型
const getSubmissionType = (status) => {
  const types = {
    '评判中': 'warning',
    '部分正确': 'warning',
    '全部正确': 'success',
    '编译错误': 'danger',
    '运行错误': 'danger'
  };
  return types[status] || 'info';
};
</script>

<style scoped>
/* 加载和错误状态样式 */
.error-message,
.empty-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #666;
  text-align: center;
}

.error-message {
  color: #f56c6c;
}

.error-message .el-icon,
.empty-message .el-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.error-message span,
.empty-message span {
  font-size: 14px;
}

.practice-list {
  display: flex;
  gap: 20px;
  height: 100%;
  padding: 20px;
}

/* 左侧练习列表面板 */
.practice-panel {
  flex: 0 0 350px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5dbe7;
}

.panel-header h3 {
  margin: 0;
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.stats {
  color: #9b7a88;
  font-size: 13px;
}

.practice-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
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
  border: 1px solid #ffd6e7;
  position: relative;
}

.practice-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(214, 51, 132, 0.1);
}

.practice-item.active {
  background: linear-gradient(135deg, #ffd6e8 0%, #fff0f4 100%);
  border-color: #d63384;
}

.practice-item.completed {
  border-color: #67c23a;
}

.practice-item.overdue {
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  border-color: #f56c6c;
}

.practice-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 20px;
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  color: #d63384;
  flex-shrink: 0;
}

.practice-item.completed .practice-icon {
  background: linear-gradient(135deg, #f0f9ff, #e0f7fa);
  color: #67c23a;
}

.practice-item.overdue .practice-icon {
  background: linear-gradient(135deg, #fef0f0, #fde2e2);
  color: #f56c6c;
}

.practice-info {
  flex: 1;
}

.practice-info h4 {
  margin: 0 0 8px 0;
  color: #7b2a53;
  font-size: 15px;
  font-weight: 600;
}

.practice-info p {
  margin: 0 0 10px 0;
  color: #9b7a88;
  font-size: 13px;
  line-height: 1.4;
}

.practice-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 12px;
}

.deadline {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #9b7a88;
}

.practice-score {
  padding: 4px 8px;
  background: linear-gradient(135deg, #ffd6e8, #fff0f4);
  color: #d63384;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
}

/* 右侧预览面板 */
.preview-panel {
  flex: 1;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 20px;
  overflow-y: auto;
}

.practice-preview {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.preview-card,
.history-card {
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
  color: #d63384;
  font-size: 18px;
  font-weight: 700;
}

.preview-content {
  color: #6b3b52;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-section h5 {
  margin: 0 0 10px 0;
  color: #7b2a53;
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

.history-card h5 {
  margin: 0;
  color: #7b2a53;
  font-size: 16px;
  font-weight: 600;
}

.history-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background: white;
  border-radius: 6px;
  margin-bottom: 8px;
}

.history-time {
  color: #9b7a88;
  font-size: 13px;
}

.history-score {
  margin-left: auto;
  color: #d63384;
  font-weight: 600;
  font-size: 13px;
}

/* 空状态 */
.empty-preview {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #9b7a88;
}

.empty-preview .el-icon {
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .practice-panel {
    flex: 0 0 300px;
  }
}

@media (max-width: 768px) {
  .practice-list {
    flex-direction: column;
  }

  .practice-panel {
    flex: none;
    width: 100%;
  }
}
</style>