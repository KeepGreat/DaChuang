<template>
  <div class="course-section">
    <!-- 课程系列标题 -->
    <h2 class="section-title">课程系列</h2>
    
    <!-- 课程卡片网格 -->
    <div class="course-grid">
      <el-card 
        v-for="course in paginatedCourses" 
        :key="course.id" 
        class="course-card"
        :body-style="{ padding: '0' }"
        @click="handleCardClick(course)"
        style="cursor: pointer;"
      >
        <!-- 课程图片 -->
        <div class="course-image">
          <el-icon size="60" class="image-placeholder"><Document /></el-icon>
        </div>
        
        <!-- 课程信息 -->
        <div class="course-info">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-description">{{ course.description }}</p>
        </div>
      </el-card>
    </div>
    
    <!-- 分页控件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-sizes="[12]"
        layout="total, prev, pager, next, jumper"
        :total="total"
        :page-count="totalPages"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Document } from '@element-plus/icons-vue';
import { ElCard, ElPagination, ElIcon } from 'element-plus';
import { getCourseSectionsPage } from '@/api';
import {useRouter} from 'vue-router'

// 每页显示的课程数量
const pageSize = ref(12);
// 当前页码
const currentPage = ref(1);
// 课程数据
const courses = ref([]);
// 数据总量
const total = ref(0);

// 路由实例
const router = useRouter()

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value);
});

// 计算当前页显示的课程
const paginatedCourses = computed(() => {
  return courses.value;
});

// 获取课程数据
const fetchCourses = async () => {
  try {
    const response = await getCourseSectionsPage(currentPage.value, pageSize.value, null, null);
    courses.value = response.data.records || [];
    total.value = courses.value.length || 0;
  } catch (error) {
    console.error('获取课程数据失败:', error);
  }
};

// 处理卡片点击事件
const handleCardClick = (course) => {
  // 将课程id存入sessionStorage
  sessionStorage.setItem('selectedCourseSectionId', course.id.toString());
  // 跳转到course页面
  router.push('/course')
};

// 处理页码变化 - 添加边界检查
const handleCurrentChange = (val) => {
  // 确保页码在有效范围内
  const newPage = Math.min(Math.max(val, 1), totalPages.value);
  if (newPage !== currentPage.value) {
    currentPage.value = newPage;
    fetchCourses();
  }
};

// 处理每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  fetchCourses();
};

// 组件挂载时获取数据
onMounted(() => {
  fetchCourses();
});
</script>

<style scoped>
.course-section {
  padding: 20px;
  background-color: #f8fafc;
  min-height: 100vh;
}

.section-title {
  color: #000;
  font-size: 18px;
  font-weight: normal;
  margin-bottom: 20px;
  text-align: left;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.course-card {
  height: 280px;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  transition: all 0.3s ease;
}

.course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.course-image {
  height: 140px;
  background-color: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
}

.image-placeholder {
  opacity: 0.6;
}

.course-info {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.course-title {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-description {
  font-size: 14px;
  color: #8c8c8c;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .course-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 992px) {
  .course-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .course-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 576px) {
  .course-grid {
    grid-template-columns: 1fr;
  }
}
</style>