<template>
  <div class="practice-index-container">
    <!-- 练习部分 -->
    <div class="section practice-section">
      <h2 class="section-title">练习任务</h2>
      <div class="practice-grid">
        <!-- 注意click事件应针对每项练习 -->
        <el-card
          v-for="(practice, index) in paginatedPractices"
          :key="index"
          class="practice-card"
          @click="handleClick('practice')"
        >
          <div class="practice-card-content">
            <el-icon class="practice-icon"><Cpu /></el-icon>
            <h3 class="practice-name">{{ practice.name }}</h3>
          </div>
        </el-card>
      </div>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[6, 12, 18]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="practices.length"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 实践部分 -->
    <div class="section lab-section">
      <h2 class="section-title">实践实验室</h2>
      <div class="lab-container">
        <el-card class="lab-card" @click="handleClick('experiment')">
          <div class="lab-card-content">
            <el-icon class="lab-icon"><Compass /></el-icon>
            <div class="lab-info">
              <h3 class="lab-title">代码实验室</h3>
              <p class="lab-description">{{ lab.description }}</p>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Compass, Cpu } from "@element-plus/icons-vue";
import { ElCard, ElIcon, ElPagination } from "element-plus";
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
// 练习任务模拟数据
const practices = ref([
  { id: 1, name: "基础算法练习" },
  { id: 2, name: "数据结构入门" },
  { id: 3, name: "排序算法实现" },
  { id: 4, name: "查找算法练习" },
  { id: 5, name: "递归思维训练" },
  { id: 6, name: "动态规划初步" },
  { id: 7, name: "贪心算法应用" },
  { id: 8, name: "图论基础" },
  { id: 9, name: "树结构练习" },
  { id: 10, name: "哈希表实现" },
  { id: 11, name: "字符串处理" },
  { id: 12, name: "位运算技巧" },
  { id: 13, name: "复杂度分析" },
  { id: 14, name: "算法优化实践" },
  { id: 15, name: "综合算法题" },
]);

// 分页相关
const currentPage = ref(1);
const pageSize = ref(6);

const paginatedPractices = computed(() => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  return practices.value.slice(startIndex, startIndex + pageSize.value);
});

const handleSizeChange = (val) => {
  pageSize.value = val;
  currentPage.value = 1;
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
};

// 实验室数据
const lab = ref({
  id: 1,
  description: "",
});

const handleClick = (item) => {
  switch (item) {
    case "practice":
      router.push("/prac");
      break;
    case "experiment":
      router.push("/exp");
      break;
  }
};
</script>

<style scoped>
.practice-index-container {
  height: 100%;
  overflow-y: auto;
  background-color: #f0f7ff;
}

.section {
  margin-bottom: 40px;
  padding: 20px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e5e7eb;
}

/* 练习任务样式 */
.practice-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.practice-card {
  height: 100%;
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

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
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
</style>
