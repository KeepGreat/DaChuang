<template>
  <div class="practice-index-container">
    <!-- 练习部分 -->
    <div class="section practice-section">
      <div class="practice-grid">
        <!-- 注意click事件应针对每项练习 -->
        <el-card
          v-for="practice in paginatedPractices"
          :key="practice.id"
          class="practice-card"
          @click="handleClick(practice)"
        >
          <div class="practice-card-content">
            <el-icon class="practice-icon"><Cpu /></el-icon>
            <h3 class="practice-name">{{ practice.name }}</h3>
          </div>
        </el-card>
        <!-- 加载中状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="3" animated />
        </div>
        <!-- 空数据状态 -->
        <div v-else-if="paginatedPractices.length === 0" class="empty-container">
          <el-empty description="暂无练习数据" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Cpu } from "@element-plus/icons-vue";
import { ElCard, ElIcon, ElMessage } from "element-plus";
import { computed, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { getPracticesByIndex } from "@/api/modules/practice/practice";

const props = defineProps({
  id: {
    type: String,
    required: true,
  },
});

const router = useRouter();
// 练习任务数据
const practices = ref([]);
// 加载状态
const loading = ref(false);

// 直接返回所有练习，不进行分页
const paginatedPractices = computed(() => {
  return practices.value;
});

// 获取练习列表
const fetchPractices = async () => {
  loading.value = true;
  try {
    // 假设props.id是courseSectionId
    const response = await getPracticesByIndex({
      courseSectionId: parseInt(props.id),
    });
    if (response.code === 200 && response.data) {
      practices.value = response.data;
    } else {
      ElMessage.error('获取练习列表失败');
    }
  } catch (error) {
    console.error('获取练习列表出错:', error);
    ElMessage.error('获取练习列表失败');
  } finally {
    loading.value = false;
  }
};

const handleClick = (practice) => {
  router.push(`practice/${practice.id}`);
};

// 组件挂载时获取数据
onMounted(() => {
  fetchPractices();
});
</script>

<style scoped>
.practice-index-container {
  height: 100%;
  /* 使用flex布局，轴线为竖向向下 */
  display: flex;
  flex-direction: column;
  /* 隐藏滚动条但保留滚动功能 */
  overflow-y: auto;
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
  background-color: #f0f7ff;
}

/* 隐藏Chrome、Safari等浏览器的滚动条 */
.practice-index-container::-webkit-scrollbar {
  display: none;
}

.section {
  padding: 20px;
  /* 让section占满可用空间 */
  flex: 1;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e5e7eb;
}

/* 练习任务样式 */
.practice-grid {
  /* 使用flex布局，轴线为竖向向下 */
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.practice-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.practice-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.practice-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30px 20px;
  text-align: center;
}

.practice-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 15px;
}

.practice-name {
  font-size: 18px;
  margin: 0;
  color: #333;
}

/* 实验室样式 */
.lab-container {
  width: 100%;
}

.lab-card {
  width: 100%;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.lab-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.lab-card-content {
  display: flex;
  align-items: center;
  padding: 30px;
}

.lab-icon {
  font-size: 56px;
  color: #67c23a;
  margin-right: 25px;
}

.lab-title {
  font-size: 22px;
  margin: 0 0 10px 0;
  color: #333;
}

.lab-description {
  color: #666;
  line-height: 1.6;
  margin: 0;
}

/* 加载状态样式 */
.loading-container {
  width: 100%;
  padding: 20px;
}

/* 空数据状态样式 */
.empty-container {
  width: 100%;
  padding: 40px 20px;
  text-align: center;
}
</style>
