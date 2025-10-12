<template>
  <div class="course-page">
    <!-- 返回按钮 -->
    <el-button 
      type="primary" 
      icon="el-icon-back"
      class="back-button"
      @click="handleBack"
    >
      返回
    </el-button>

    <!-- 课程系列介绍部分 -->
    <div class="section-intro">
      <div class="intro-image">
        <el-icon size="120" class="image-placeholder"><Document /></el-icon>
      </div>
      <div class="intro-info">
        <h1 class="section-main-title">{{ courseSection.name || '课程系列' }}</h1>
        <p class="section-description">{{ courseSection.description || '加载中...' }}</p>
      </div>
    </div>

    <!-- 课程卡片展示部分 -->
    <div class="course-cards-container">
      <h2 class="courses-title">相关课程</h2>
      
      <div class="course-cards">
        <el-card 
          v-for="course in paginatedCourses" 
          :key="course.id" 
          class="course-card"
          :body-style="{ padding: '0' }"
          @click="handleCardClick(course)"
        >
          <div class="course-card-content">
            <!-- 课程图片 -->
            <div class="course-card-image">
              <el-icon size="40" class="course-image-placeholder"><Document /></el-icon>
            </div>
            
            <!-- 课程信息 -->
            <div class="course-card-info">
              <h3 class="course-card-title">{{ course.name }}</h3>
              <p class="course-card-description">{{ course.description }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页控件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          :page-sizes="[5]"
          layout="total, prev, pager, next, jumper"
          :total="total"
          :page-count="totalPages"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Document } from '@element-plus/icons-vue';
import { ElCard, ElPagination, ElIcon, ElButton } from 'element-plus';
import { useRouter } from 'vue-router';
import { getCourseSections } from '../../utils/teaching/CourseSectionAPI';
import { getCoursesPage } from '../../utils/teaching/CourseAPI';

// 创建router实例
const router = useRouter();

// 课程系列信息
const courseSection = ref({});
// 课程数据
const courses = ref([]);
// 每页显示的课程数量
const pageSize = ref(5);
// 当前页码
const currentPage = ref(1);
// 数据总量
const total = ref(0);

// 计算总页数
const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value);
});

// 计算当前页显示的课程
const paginatedCourses = computed(() => {
  return courses.value;
});

// 处理返回按钮点击
const handleBack = () => {
  // 删除sessionStorage中的sectionId
  sessionStorage.removeItem('selectedCourseSectionId');
  // 跳转到课程系列页面
  router.push('/coursesection');
};

// 处理课程卡片点击
const handleCardClick = (course) => {
  // 将课程id写入sessionStorage
  sessionStorage.setItem('selectedCourseId', course.id);
  // 跳转到教学页面
  router.push('/teach');
};

// 获取课程系列信息
const fetchCourseSection = async () => {
  try {
    // 从sessionStorage获取课程系列id
    const sectionId = sessionStorage.getItem('selectedCourseSectionId');
    if (sectionId) {
      const response = await getCourseSections(sectionId, null);
      courseSection.value = response.data[0] || { name: '未知课程系列', description: '暂无描述' };
      // 获取该课程系列下的课程
      await fetchCourses(sectionId);
    } else {
      console.error('未找到课程系列ID');
      courseSection.value = { name: '未知课程系列', description: '暂无描述' };
    }
  } catch (error) {
    console.error('获取课程系列信息失败:', error);
  }
};

// 获取课程数据
const fetchCourses = async (sectionId) => {
  try {
    const response = await getCoursesPage(currentPage.value, pageSize.value, null, null, sectionId);
    courses.value = response.data.records || [];
    total.value = response.data.total || 0;
  } catch (error) {
  }
};

// 处理页码变化
const handleCurrentChange = (val) => {
  const newPage = Math.min(Math.max(val, 1), totalPages.value);
  if (newPage !== currentPage.value) {
    currentPage.value = newPage;
    const sectionId = sessionStorage.getItem('selectedCourseSectionId');
    if (sectionId) {
      fetchCourses(sectionId);
    }
  }
};

// 处理每页条数变化
const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  const sectionId = sessionStorage.getItem('selectedCourseSectionId');
  if (sectionId) {
    fetchCourses(sectionId);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchCourseSection();
});
</script>

<style scoped>
.course-page {
  padding: 20px;
  background-color: #f8fafc;
  min-height: 100vh;
}

/* 返回按钮样式 */
.back-button {
  margin-bottom: 20px;
  background-color: #1890ff;
  border-color: #1890ff;
}

.back-button:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

/* 课程系列介绍部分样式 */
.section-intro {
  display: flex;
  align-items: center;
  margin-bottom: 40px;
  padding: 24px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.intro-image {
  width: 160px;
  height: 160px;
  background-color: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 24px;
  border-radius: 8px;
  color: #1890ff;
}

.image-placeholder {
  opacity: 0.8;
}

.intro-info {
  flex: 1;
}

.section-main-title {
  font-size: 24px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 12px 0;
}

.section-description {
  font-size: 16px;
  color: #595959;
  line-height: 1.6;
  margin: 0;
}

/* 课程卡片容器样式 */
.course-cards-container {
  background-color: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.courses-title {
  font-size: 20px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 20px 0;
}

/* 课程卡片样式 */
.course-cards {
  margin-bottom: 30px;
}

.course-card {
  margin-bottom: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
}

.course-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateY(-1px);
}

.course-card-content {
  display: flex;
  height: 120px;
}

.course-card-image {
  width: 120px;
  background-color: #e6f7ff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
}

.course-image-placeholder {
  opacity: 0.7;
}

.course-card-info {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.course-card-title {
  font-size: 16px;
  font-weight: 500;
  color: #262626;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.course-card-description {
  font-size: 14px;
  color: #595959;
  line-height: 1.5;
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}

/* 分页控件样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .section-intro {
    flex-direction: column;
    text-align: center;
  }
  
  .intro-image {
    margin-right: 0;
    margin-bottom: 20px;
  }
  
  .course-card-content {
    flex-direction: column;
    height: auto;
  }
  
  .course-card-image {
    width: 100%;
    height: 80px;
  }
}

@media (max-width: 576px) {
  .course-page {
    padding: 12px;
  }
  
  .section-intro {
    padding: 16px;
  }
  
  .intro-image {
    width: 120px;
    height: 120px;
  }
  
  .section-main-title {
    font-size: 20px;
  }
  
  .section-description {
    font-size: 14px;
  }
  
  .course-cards-container {
    padding: 16px;
  }
}
</style>