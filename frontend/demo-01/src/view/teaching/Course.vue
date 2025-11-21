<!-- Course.vue -->
<template>
  <div class="course-page">
    <el-button 
      type="primary"
      class="back-button"
      @click="handleBack"
    >
      返回
    </el-button>

    <div class="section-intro">
      <div class="intro-image">
        <el-icon size="120" class="image-placeholder"><Document /></el-icon>
      </div>
      <div class="intro-info">
        <h1 class="section-main-title">{{ courseSection.name || '课程系列' }}</h1>
        <p class="section-description">{{ courseSection.description || '加载中...' }}</p>
      </div>
    </div>

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
            <div class="course-card-image">
              <el-icon size="40" class="course-image-placeholder"><Document /></el-icon>
            </div>
            <div class="course-card-info">
              <h3 class="course-card-title">{{ course.name }}</h3>
              <p class="course-card-description">{{ course.description }}</p>
            </div>
          </div>
        </el-card>
      </div>

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
import { ElCard, ElPagination, ElIcon, ElButton, ElMessage } from 'element-plus';
import { useRoute, useRouter } from 'vue-router'; // ✅ 引入 useRoute
import { getCourseSections } from '@/api';
import { getCoursesPage } from '@/api';

const route = useRoute(); // ✅ 获取路由参数
const router = useRouter();

const courseSection = ref({});
const courses = ref([]);
const pageSize = ref(5);
const currentPage = ref(1);
const total = ref(0);

const totalPages = computed(() => {
  return Math.ceil(total.value / pageSize.value);
});

const paginatedCourses = computed(() => courses.value);

const handleBack = () => {
  router.push('/coursesection');
};

const handleCardClick = (course) => {
  // 如果后续 /teach 也需要 course.id，建议也改为 /teach/:id
  sessionStorage.setItem('selectedCourseId', course.id); // 保留，因 teach 页面可能仍需
  router.push('/coursesection/teach');
};

const fetchCourseSection = async () => {
  try {
    // ✅ 从路由参数获取 ID
    const sectionId = route.params.id;
    if (!sectionId) {
      ElMessage.warning('课程系列ID无效');
      router.push('/coursesection');
      return;
    }

    const response = await getCourseSections(sectionId, null);
    if (response.data && response.data.length > 0) {
      courseSection.value = response.data[0];
      await fetchCourses(sectionId);
    } else {
      throw new Error('课程系列数据为空');
    }
  } catch (error) {
    console.error('获取课程系列信息失败:', error);
    ElMessage.error('加载课程系列信息失败，请稍后重试');
    // courseSection.value = { name: '加载失败', description: '无法获取课程系列详情' };
    courseSection.value = {name: "Python 入门系列",
    description: "从零开始学习 Python 编程语言，掌握基础语法、数据类型与流程控制。"}
    courses.value = [
  {
    "id": 1,
    "name": "Python 入门系列",
    "description": "从零开始学习 Python 编程语言，掌握基础语法、数据类型与流程控制。"
  },
  {
    "id": 2,
    "name": "JavaScript 核心编程",
    "description": "深入理解 JavaScript 基础与高级特性，包括闭包、原型链、异步编程等。"
  },
  {
    "id": 3,
    "name": "Web 前端开发实战",
    "description": "从 HTML/CSS 到 Vue/React，打造现代响应式网页与交互式用户界面。"
  },
  {
    "id": 4,
    "name": "数据结构与算法精讲",
    "description": "系统学习数组、链表、栈、队列、树、图等结构及经典算法实现。"
  },
  {
    "id": 5,
    "name": "Java 企业级应用开发",
    "description": "掌握 Spring Boot、MyBatis 等主流框架，构建高性能后端服务。"
  },
  {
    "id": 6,
    "name": "数据库原理与 SQL 实战",
    "description": "从 MySQL 基础到复杂查询优化，理解事务、索引与数据库设计范式。"
  },
  {
    "id": 7,
    "name": "人工智能与机器学习入门",
    "description": "使用 Python 和 scikit-learn 探索监督学习、分类、回归与模型评估。"
  },
  {
    "id": 8,
    "name": "Linux 与 Shell 编程",
    "description": "掌握常用命令、脚本编写、权限管理及服务器环境配置技巧。"
  },
  {
    "id": 9,
    "name": "Git 与团队协作开发",
    "description": "从版本控制基础到分支管理、代码审查与 CI/CD 流程实践。"
  },
  {
    "id": 10,
    "name": "计算机网络与安全基础",
    "description": "理解 HTTP/HTTPS、TCP/IP、DNS 等协议，初步掌握网络安全防护知识。"
  }
]
    total.value = courses.value.length
  }
};

const fetchCourses = async (sectionId) => {
  try {
    const response = await getCoursesPage(currentPage.value, pageSize.value, null, null, sectionId);
    courses.value = response.data.records || [];
    total.value = response.data.total || 0;
  } catch (error) {
    console.error('获取课程列表失败:', error);
    ElMessage.error('加载课程列表失败，请稍后重试');
    courses.value = [];
    total.value = 0;
  }
};

const handleCurrentChange = (val) => {
  const newPage = Math.min(Math.max(val, 1), totalPages.value || 1);
  if (newPage !== currentPage.value) {
    currentPage.value = newPage;
    const sectionId = route.params.id;
    if (sectionId) {
      fetchCourses(sectionId);
    }
  }
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
  const sectionId = route.params.id;
  if (sectionId) {
    fetchCourses(sectionId);
  }
};

onMounted(() => {
  fetchCourseSection();
});
</script>

<style scoped>
/* 样式保持不变，此处省略 */
.course-page {
  padding: 20px;
  background-color: #f8fafc;
  min-height: 100vh;
}
.back-button {
  margin-bottom: 20px;
  background-color: #1890ff;
  border-color: #1890ff;
}
.back-button:hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
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
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
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