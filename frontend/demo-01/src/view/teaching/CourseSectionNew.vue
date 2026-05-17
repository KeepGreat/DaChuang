<template>
  <div class="course-section-new">
    <!-- 页面标题 -->
    <div class="page-header animate-fade-in-up">
      <div class="page-header-content">
        <h1 class="page-title animate-slide-in-left">
          <span class="title-text animate-fade-in-up">智慧课程</span>
        </h1>
        <p class="page-subtitle animate-fade-in-up">
          AI驱动的个性化、精准化、高效率的教学体验
        </p>
      </div>
    </div>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <div class="filter-group">
        <span class="filter-label">课程类型：</span>
        <el-select v-model="selectedCourseSectionTypeId" placeholder="全部" class="filter-select" @change="handleTypeFilterChange">
          <el-option label="全部" value="all"></el-option>
          <el-option
            v-for="type in courseSectionTypes"
            :key="type.id"
            :label="type.name"
            :value="type.id"
          ></el-option>
        </el-select>
      </div>
      <div class="filter-search">
        <el-input v-model="searchName" placeholder="输入课程名称搜索" class="search-input" clearable
          @input="handleInputSearch" @keyup.enter="handleSearch" @clear="handleClearSearch">
          <template #append>
            <el-button @click="handleSearch" :icon="Search">搜索</el-button>
          </template>
        </el-input>
      </div>
    </div>

    <!-- 加载中动画 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-animation">
        <div class="loading-spinner"></div>
        <p class="loading-text">加载课程中...</p>
      </div>
    </div>

    <!-- 课程卡片容器 -->
    <div v-else class="course-container">
      <!-- 课程卡片 -->
      <div v-for="course in displayedCourses" :key="course.id" class="course-card" @click="handleCardClick(course)">
        <div class="course-image">
          <img 
            :src="getCourseImage(course)" 
            :alt="course.name" 
            class="course-img" 
            loading="lazy"
            @error="handleImageError($event, course)"
          />
        </div>
        <div class="course-info">
          <h3 class="course-title">{{ course.name }}</h3>
          <p class="course-description">{{ course.description || '暂无描述' }}</p>
          <div class="course-meta">
            <span class="course-subject">{{ getTypeName(course.courseSectionTypeId) }}</span>
            <span class="course-teacher">{{ course.teacher }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页只在非搜索状态下显示 -->
    <div v-if="!loading && !isSearching && courses.length > 0" class="pagination-container">
      <el-pagination v-model:current-page="currentPage" :page-size="pageSize" layout="total, prev, pager, next, jumper"
        :total="total" @current-change="handleCurrentChange" :background="true" :hide-on-single-page="false" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useCourseStore } from '@/store';
import { getCourseSectionTypes } from '@/api';
import { testClassifm } from '@/store';
import image1 from '@/assets/teaching/course/2.jpg';

const router = useRouter();
const courseStore = useCourseStore();
const classifm = testClassifm();

// 筛选条件
const selectedCourseSectionTypeId = ref('all');
const courseSectionTypes = ref([]);

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
const displayedCourses = computed(() => {
  if (selectedCourseSectionTypeId.value === 'all') {
    return courses.value;
  }

  return courses.value.filter(
    (course) => String(course.courseSectionTypeId) === String(selectedCourseSectionTypeId.value)
  );
});

const getTypeName = (typeId) => {
  const type = courseSectionTypes.value.find((item) => item.id === typeId);
  return type ? type.name : '未分类';
};

const loadCourseSectionTypes = async () => {
  try {
    const response = await getCourseSectionTypes();
    courseSectionTypes.value = response.data || [];
  } catch (error) {
    console.error('加载课程章节类型失败:', error);
    courseSectionTypes.value = [];
  }
};

const handleTypeFilterChange = async () => {
  currentPage.value = 1;
  const searchTerm = searchName.value?.trim();

  if (!searchTerm) {
    await courseStore.fetchCoursePage(1, pageSize.value);
    return;
  }

  await courseStore.searchCourses(searchTerm);
};

const getCourseImage = (course) => {
  // return `https://picsum.photos/400/225?random=${course.id}`
  return image1;
};

// 图片错误处理
const handleImageError = (event, course) => {
  // 使用占位图
  event.target.src = `https://picsum.photos/400/225?random=${course.id}`;
};

// 搜索
const handleSearch = async () => {
  const searchTerm = searchName.value?.trim();
  await courseStore.searchCourses(searchTerm);
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

// 跳转
const handleCardClick = (course) => {
  courseStore.setCurrentCourse(course);
  router.push(`/teaching/course/${course.id}`);
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

// 初始化
onMounted(async () => {
  try {
    await loadCourseSectionTypes();

    // 尝试从 store 加载课程数据
    await courseStore.fetchCoursePage(1, 12);
    
    // 如果没有数据，使用模拟数据
    if (courses.value.length === 0) {
      // 将模拟数据添加到 store 中
      courseStore.originalCourses = mockCourses;
      courseStore.courses = mockCourses;
      courseStore.pagination.total = mockCourses.length;
      courseStore.pagination.current = 1;
      courseStore.pagination.pageSize = 12;
      console.log('使用模拟数据初始化，总课程数:', mockCourses.length);
    }
  } catch (error) {
    // 出错时使用模拟数据
    console.error('加载课程数据失败，使用模拟数据:', error);
    courseStore.originalCourses = mockCourses;
    courseStore.courses = mockCourses;
    courseStore.pagination.total = mockCourses.length;
    courseStore.pagination.current = 1;
    courseStore.pagination.pageSize = 12;
    console.log('使用模拟数据初始化，总课程数:', mockCourses.length);
  }
});
</script>

<style scoped>
.course-section-new {
  padding: 20px;
  min-height: 100vh;
  background-color: var(--bg-light);
}

.page-header {
  min-height: 25vh;
  display: flex;
  align-items: center;
  background-color: var(--bg-primary-light);
  background-image:
    linear-gradient(
      to left,
      rgba(232, 244, 255, 0.12) 18%,
      rgba(232, 244, 255, 0.68) 56%,
      rgba(232, 244, 255, 1) 100%
    ),
    url('@/assets/teaching/CourseSectionListBackground.jpg');
  background-repeat: no-repeat;
  background-size: cover;
  background-position: right center;
  padding: 0 2rem;
  margin-bottom: 2rem;
  border-radius: 12px;
  box-shadow: 0 6px 18px var(--primary-alpha-10);
}

.page-header-content {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: 2rem 0;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0 0 0.5rem 0;
}

.title-text {
  color: var(--text-primary);
  font-size: 2.5rem;
  
}

.page-subtitle {
  font-size: 1.125rem;
  color: var(--text-regular);
  margin: 0.5rem 0 0 0;
  animation-delay: 0.2s;
}

/* 自定义动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.8s ease-out forwards;
}

.animate-slide-in-left {
  animation: slideInLeft 0.8s ease-out forwards;
}

.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  padding: 15px 20px;
  background-color: var(--bg-white);
  border-radius: 8px;
  box-shadow: 0 2px 4px var(--primary-alpha-10);
  margin-bottom: 30px;
  border: 1px solid var(--border-light);
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filter-label {
  font-size: 14px;
  color: var(--text-regular);
  white-space: nowrap;
  font-weight: 500;
}

.filter-select {
  width: 120px;
}

.filter-search {
  margin-left: auto;
}

.search-input {
  width: 280px;
}

.loading-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 40px 0;
}

.loading-animation {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid var(--bg-primary-light);
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  color: var(--text-regular);
  font-size: 16px;
  font-weight: 500;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.course-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.course-card {
  background-color: var(--bg-white);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px var(--primary-alpha-10);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid var(--border-light);
}

.course-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px var(--primary-alpha-20);
  border-color: var(--primary-light);
}

.course-image {
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, var(--primary-lightest) 0%, var(--bg-primary-light) 100%);
}

.course-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-img {
  transform: scale(1.05);
}

.course-info {
  padding: 15px;
}

.course-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 10px 0;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-description {
  font-size: 14px;
  color: var(--text-regular);
  line-height: 1.4;
  margin: 0 0 12px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: var(--text-placeholder);
}

.course-subject {
  padding: 2px 8px;
  background-color: var(--bg-primary-light);
  color: var(--primary);
  border-radius: 10px;
  font-weight: 500;
}

.loading-more {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px 0;
  color: var(--text-regular);
  gap: 8px;
  grid-column: 1 / -1;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-section-new {
    padding: 15px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .filter-search {
    margin-left: 0;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .course-container {
    grid-template-columns: 1fr;
  }
}

@media (min-width: 769px) and (max-width: 992px) {
  .course-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 993px) and (max-width: 1200px) {
  .course-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (min-width: 1201px) {
  .course-container {
    grid-template-columns: repeat(4, 1fr);
  }
}
</style>