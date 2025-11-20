<!-- CourseSection.vue -->

<template>
  <div class="course-section">
    <!-- 标题 + 搜索框 -->
    <div class="header-container">
      <h2 class="section-title">课程系列</h2>
      <el-input
        v-model="searchName"
        placeholder="输入课程系列名称搜索..."
        class="search-input"
        clearable
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch" :icon="Search">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 其余部分（卡片、分页等）保持不变 -->
    <div v-if="!loading && courses.length === 0" class="empty-state">
      <el-icon size="60" class="empty-icon"><Document /></el-icon>
      <p class="empty-text">{{ isSearching ? '未找到匹配的课程系列' : '当前没有课程系列' }}</p>
    </div>

    <div v-else class="course-grid">
      <el-card
        v-for="course in courses"
        :key="course.id"
        class="course-card"
        :body-style="{ padding: '0' }"
        @click="handleCardClick(course)"
      >
        <div class="course-image">
          <el-icon size="60" class="image-placeholder"><Document /></el-icon>
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-description">{{ course.description || '暂无描述' }}</p>
        </div>
      </el-card>
    </div>

    <!-- 分页只在非搜索状态下显示 -->
    <div v-if="!loading && !isSearching && total > pageSize" class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
        @current-change="handleCurrentChange"
      />
    </div>

    <el-skeleton v-if="loading" :rows="4" animated />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { Document, Search } from '@element-plus/icons-vue';
import { ElCard, ElPagination, ElIcon, ElSkeleton, ElInput, ElButton } from 'element-plus';
import { getCourseSections, getCourseSectionsPage } from '@/api';
import { useRouter } from 'vue-router';
import { testClassifm } from '@/store';

const router = useRouter();

// 响应式数据
const pageSize = ref(12);
const currentPage = ref(1);
const courses = ref([]);
const total = ref(0);
const loading = ref(true);
const searchName = ref('');
const isSearching = ref(false); // 标记是否处于搜索模式
const classifm = testClassifm()

// 跳转
const handleCardClick = (course) => {
  router.push(`/coursesection/course/${course.id}`);
};

// 获取分页数据（初始/浏览）
const fetchPageData = async (page = 1) => {
  loading.value = true;
  try {
    const response = await getCourseSectionsPage(page, pageSize.value, null, null);
    courses.value = response.data.records || [];
    total.value = response.data.total || 0;
  } catch (error) {
    console.error('获取分页数据失败:', error);
    courses.value = classifm.class_data;
    total.value = courses.value.length;
  } finally {
    loading.value = false;
    isSearching.value = false;
  }
};

// 搜索：调用不分页的 getCourseSections
const handleSearch = async () => {
  const name = searchName.value?.trim();
  loading.value = true;
  isSearching.value = true;

  try {
    if (!name) {
      // 如果搜索框为空，恢复分页列表
      await fetchPageData(currentPage.value);
      return;
    }

    const response = await getCourseSections(null, name); // 只传 name
    courses.value = Array.isArray(response.data) ? response.data : [];
    total.value = courses.value.length;
  } catch (error) {
    console.error('搜索失败:', error);
    courses.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 分页切换（仅在非搜索状态有效）
const handleCurrentChange = (page) => {
  if (!isSearching.value) {
    currentPage.value = page;
    fetchPageData(page);
  }
};

// 初始化：加载第一页
onMounted(() => {
  fetchPageData();
});
</script>

<style scoped>
/* 样式基本不变，仅微调搜索框 */
.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.section-title {
  color: #0d47a1;
  font-size: 22px;
  font-weight: 600;
  margin: 0;
}

.search-input {
  width: 320px;
  max-width: 100%;
}

/* 其余样式（.empty-state, .course-grid 等）保持不变 */
.course-section {
  padding: 24px;
  background: linear-gradient(160deg, #f8fafc 0%, #ffffff 100%);
  min-height: 100vh;
}
.section-title {
  color: #0d47a1;
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 24px;
  text-align: left;
  position: relative;
}
.section-title::after {
  content: '';
  display: block;
  width: 40px;
  height: 3px;
  background: linear-gradient(to right, #1890ff, #59bfff);
  border-radius: 2px;
  margin-top: 8px;
}
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}
.empty-icon {
  color: #b3d9ff;
  margin-bottom: 16px;
}
.empty-text {
  color: #8c8c8c;
  font-size: 16px;
  font-weight: 500;
}
.course-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 30px;
}
.course-card {
  height: 280px;
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.08);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #e6f4ff;
}
.course-card:hover {
  box-shadow: 0 6px 20px rgba(24, 144, 255, 0.18);
  transform: translateY(-3px);
  border-color: #cce6ff;
}
.course-image {
  height: 140px;
  background: linear-gradient(135deg, #e6f7ff 0%, #f0f9ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
}
.image-placeholder {
  opacity: 0.7;
}
.course-info {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.course-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a365d;
  margin: 0 0 10px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  white-space: normal;
}
.course-description {
  font-size: 14px;
  color: #666666;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  flex: 1;
  margin: 0;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
@media (max-width: 1200px) {
  .course-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 992px) {
  .course-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .course-grid { grid-template-columns: 1fr; }
  .course-card { height: auto; min-height: 240px; }
}
@media (max-width: 576px) {
  .course-section { padding: 16px; }
  .section-title { font-size: 20px; }
}
</style>