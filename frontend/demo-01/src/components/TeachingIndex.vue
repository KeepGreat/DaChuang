<template>
  <div class="teaching-index-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">编程教学课程</h1>
      <p class="page-subtitle">探索我们精心设计的编程课程，从基础到进阶，助您掌握核心技能</p>
    </div>

    <!-- 课程列表容器 -->
    <div class="courses-container">
      <!-- 课程卡片列表 -->
      <div class="course-list">
        <!-- 注意click事件应针对每项课程 -->
        <el-card v-for="(course, index) in paginatedCourses" :key="index" class="course-card"
          @click="handleClick('teach')">
          <div class="course-card-content">
            <el-icon class="course-icon"><Compass /></el-icon>
            <div class="course-info">
              <h3 class="course-title">{{ course.name }}</h3>
              <p class="course-description">{{ course.description }}</p>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[5, 10, 15]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="courses.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { Compass } from '@element-plus/icons-vue';
import { ElCard, ElPagination, ElIcon } from 'element-plus';
import { useRouter } from 'vue-router';

// 课程模拟数据
const courses = ref([
  { id: 1, name: 'Python基础编程', description: '' },
  { id: 2, name: 'JavaScript核心概念', description: '' },
  { id: 3, name: 'Java面向对象编程', description: '' },
  { id: 4, name: 'C++程序设计入门', description: '' },
  { id: 5, name: '数据结构与算法', description: '' },
  { id: 6, name: 'Web前端开发基础', description: '' },
  { id: 7, name: '数据库原理与SQL', description: '' },
  { id: 8, name: '移动应用开发入门', description: '' },
  { id: 9, name: 'React框架实战', description: '' },
  { id: 10, name: 'Node.js后端开发', description: '' },
  { id: 11, name: '机器学习基础', description: '' },
  { id: 12, name: '网络安全基础', description: '' },
  { id: 13, name: '云计算与Docker', description: '' },
  { id: 14, name: 'DevOps实践指南', description: '' },
  { id: 15, name: '大数据处理技术', description: '' },
  { id: 16, name: '区块链技术入门', description: '' },
  { id: 17, name: '人工智能应用开发', description: '' },
  { id: 18, name: '前端性能优化', description: '' },
  { id: 19, name: '微服务架构设计', description: '' },
  { id: 20, name: '全栈开发实战', description: '' },
]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(5);

const router = useRouter();

const paginatedCourses = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return courses.value.slice(startIndex, startIndex + pageSize.value);
});

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

const handleClick = (item) => {
  switch (item) {
    case 'teach':
      router.push('/teach');
      break;
  }
}

</script>

<style scoped>
.teaching-index-container {
  background-color: #f0f7ff;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  text-align: center;
  margin-bottom: 40px;
  margin-top: 20px;
}

.page-title {
  font-size: 36px;
  margin: 0 0 10px 0;
  color: #333;
}

.page-subtitle {
  font-size: 18px;
  color: #666;
  margin: 0;
}

/* 课程列表容器样式 - 不允许垂直溢出 */
.courses-container {
  margin: 20px;
  height: 70%;
}

/* 课程卡片列表样式 */
.course-list {
  max-height: 100%; 
  overflow-y: auto; /* 列表内容超出时显示滚动条 */
  margin-bottom: 30px;
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f0f2f5;
}

/* 自定义滚动条样式 */
.course-list::-webkit-scrollbar {
  width: 6px;
}

.course-list::-webkit-scrollbar-track {
  background: #f0f2f5;
  border-radius: 3px;
}

.course-list::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.course-list::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

/* 课程卡片样式 */
.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 8px;
  height: 120px;
}

.course-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-card-content {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  height: 100%;
}

.course-icon {
  font-size: 40px;
  color: #409eff;
  margin-right: 20px;
  flex-shrink: 0;
}

.course-info {
  flex: 1;
}

.course-title {
  font-size: 20px;
  margin: 0 0 10px 0;
  color: #333;
}

.course-description {
  color: #666;
  line-height: 1.6;
  margin: 0;
  min-height: 40px;
}

/* 分页容器样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teaching-index-container {
    padding: 15px;
  }

  .page-title {
    font-size: 28px;
  }

  .page-subtitle {
    font-size: 16px;
  }

  .course-card-content {
    padding: 15px;
  }

  .course-icon {
    font-size: 32px;
    margin-right: 15px;
  }

  .course-title {
    font-size: 18px;
  }
}
</style>