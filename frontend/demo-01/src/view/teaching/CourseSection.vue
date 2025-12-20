<!-- CourseSection.vue -->

<template>
  <div class="course-section">
    <!-- 标题 + 搜索框 -->
    <div class="header-container">
      <h2 class="section-title">课程系列</h2>
      <el-input v-model="searchName" placeholder="输入课程系列名称搜索..." class="search-input" clearable
        @input="handleInputSearch" @keyup.enter="handleSearch" @clear="handleClearSearch">
        <template #append>
          <el-button @click="handleSearch" :icon="Search">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 其余部分（卡片、分页等）保持不变 -->
    <div v-if="!loading && courses.length === 0" class="empty-state">
      <el-icon size="60" class="empty-icon">
        <Document />
      </el-icon>
      <p class="empty-text">{{ isSearching ? '未找到匹配的课程系列' : '当前没有课程系列' }}</p>
    </div>

    <div v-else class="course-grid">
      <el-card v-for="course in courses" :key="course.id" class="course-card" :body-style="{ padding: '0' }"
        @click="handleCardClick(course)">
        <div class="course-image">
          <el-icon size="60" class="image-placeholder">
            <Document />
          </el-icon>
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-description">{{ course.description || '暂无描述' }}</p>
        </div>
      </el-card>
    </div>

    <!-- 分页只在非搜索状态下显示 -->
    <div v-if="!loading && !isSearching && total > pageSize" class="pagination-container">
      <el-pagination v-model:current-page="currentPage" :page-size="pageSize" layout="total, prev, pager, next, jumper"
        :total="total" @current-change="handleCurrentChange" />
    </div>

    <el-skeleton v-if="loading" :rows="4" animated />
  </div>
</template>

<script setup>
import { onMounted, computed } from 'vue';
import { Document, Search } from '@element-plus/icons-vue';
import { ElCard, ElPagination, ElIcon, ElSkeleton, ElInput, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import { useCourseStore } from '@/store';
import { testClassifm } from '@/store';

const router = useRouter();

// 使用Pinia store
const courseStore = useCourseStore();
const classifm = testClassifm();

// 计算属性
const courses = computed(() => courseStore.courses);
const total = computed(() => courseStore.pagination.total);
const loading = computed(() => courseStore.loading);
const searchName = computed({
  get: () => courseStore.searchName,
  set: (value) => courseStore.searchName = value
});
const isSearching = computed(() => courseStore.isSearching);
const currentPage = computed({
  get: () => courseStore.pagination.current,
  set: (value) => courseStore.pagination.current = value
});
const pageSize = computed(() => courseStore.pagination.pageSize);

// 跳转
const handleCardClick = (course) => {
  courseStore.setCurrentCourse(course);
  router.push(`/teaching/course/${course.id}`);
};

// 搜索
const handleSearch = async () => {
  const searchTerm = searchName.value?.trim();

  // if (!searchTerm) {
  //   // 如果搜索内容为空，加载所有课程
  //   await courseStore.fetchCoursePage(1, 12);
  //   currentPage.value = 1;
  // } else {
  //   // 有搜索内容时执行搜索
  await courseStore.searchCourses(searchTerm);
  // }
};

// 实时搜索（输入时触发）
const handleInputSearch = async () => {
  const searchTerm = searchName.value?.trim();

  // 使用防抖，避免频繁请求
  clearTimeout(handleInputSearch.debounceTimer);
  handleInputSearch.debounceTimer = setTimeout(async () => {
    if (!searchTerm) {
      // 搜索内容为空时，加载所有课程
      await courseStore.fetchCoursePage(1, 12);
      currentPage.value = 1;
    } else {
      // 有内容时搜索
      await courseStore.searchCourses(searchTerm);
    }
  }, 300);
};

// 清空搜索
const handleClearSearch = async () => {
  searchName.value = '';
  await courseStore.fetchCoursePage(1, 12);
  currentPage.value = 1;
};

// 分页切换
const handleCurrentChange = async (page) => {
  const searchTerm = searchName.value?.trim();

  if (!searchTerm) {
    // 非搜索状态下的分页
    await courseStore.fetchCoursePage(page, pageSize.value);
  }
  // 搜索状态下不显示分页，所以不需要处理
};

// 初始化：加载第一页
onMounted(async () => {
  try {
    await courseStore.fetchCoursePage(1, 12);
  } catch (error) {
    // 使用默认数据 - 直接修改store中的数据而不是计算属性
    if (classifm.class_data && classifm.class_data.length > 0) {
      courseStore.courses = classifm.class_data;
      courseStore.originalCourses = classifm.class_data;
    }
    console.log('使用默认课程数据');
  }
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
  color: #d63384;
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.search-input {
  width: 320px;
  max-width: 100%;
}

/* 其余样式（.empty-state, .course-grid 等）保持不变 */
.course-section {
  padding: 24px;
  background: #fff6fb;
  min-height: 100vh;
  font-family: "Inter", Arial, sans-serif;
}

.section-title {
  color: #d63384;
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: left;
  position: relative;
}

.section-title::after {
  content: '';
  display: block;
  width: 40px;
  height: 3px;
  background: linear-gradient(to right, #ff7ab1, #d63384);
  border-radius: 2px;
  margin-top: 8px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
}

.empty-icon {
  color: #ffb6d9;
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
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #ffd6e7;
}

.course-card:hover {
  box-shadow: 0 12px 28px rgba(214, 51, 132, 0.08);
  transform: translateY(-6px);
  border-color: #ffb6d9;
}

.course-image {
  height: 140px;
  background: linear-gradient(135deg, #ffd6e8 0%, #fff0f4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #d63384;
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
  font-weight: 700;
  color: #7b2a53;
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
  color: #9b7a88;
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

  .course-card {
    height: auto;
    min-height: 240px;
  }
}

@media (max-width: 576px) {
  .course-section {
    padding: 16px;
  }

  .section-title {
    font-size: 20px;
  }
}
</style>