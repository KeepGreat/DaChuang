<template>
  <div class="course-learning">
    <!-- 左侧导航栏 -->
    <div class="nav-sidebar">
      <!-- 返回按钮 -->
      <div class="nav-item back-item" @click="handleBack" :title="'返回课程列表'">
        <el-icon>
          <ArrowLeft />
        </el-icon>
        <span class="nav-tooltip">返回课程列表</span>
      </div>

      <div class="nav-menu">
        <div
          class="nav-item"
          :class="{ active: activeModule === 'course' }"
          @click="switchModule('course')"
          :title="'课程学习'"
        >
          <el-icon>
            <VideoPlay />
          </el-icon>
          <span class="nav-tooltip">课程学习</span>
        </div>
        <!-- <div class="nav-item" :class="{ active: activeModule === 'task' }" @click="switchModule('task')" :title="'任务'">
          <el-icon>
            <Bell />
          </el-icon>
          <span class="nav-tooltip">任务</span>
        </div> -->
        <div
          class="nav-item"
          :class="{ active: activeModule === 'practice' }"
          @click="switchModule('practice')"
          :title="'作业'"
        >
          <el-icon>
            <Edit />
          </el-icon>
          <span class="nav-tooltip">作业</span>
        </div>
        <div
          class="nav-item"
          :class="{ active: activeModule === 'exam' }"
          @click="switchModule('exam')"
          :title="'考试'"
        >
          <el-icon>
            <Document />
          </el-icon>
          <span class="nav-tooltip">考试</span>
        </div>
        <!-- <div class="nav-item" :class="{ active: activeModule === 'resource' }" @click="switchModule('resource')"
          :title="'资料'">
          <el-icon>
            <FolderOpened />
          </el-icon>
          <span class="nav-tooltip">资料</span>
        </div> -->
        <!-- <div class="nav-item" :class="{ active: activeModule === 'discussion' }" @click="switchModule('discussion')"
          :title="'讨论'">
          <el-icon>
            <ChatDotRound />
          </el-icon>
          <span class="nav-tooltip">讨论</span>
        </div> -->
        <div
          class="nav-item"
          :class="{ active: activeModule === 'knowledge' }"
          @click="switchModule('knowledge')"
          :title="'知识图谱'"
        >
          <el-icon>
            <Share />
          </el-icon>
          <span class="nav-tooltip">知识图谱</span>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter, useRoute } from "vue-router";
import {
  ArrowLeft,
  VideoPlay,
  Share,
  Edit,
  Document,
  Folder,
  FolderOpened,
  Bell,
  ChatDotRound,
} from "@element-plus/icons-vue";

const router = useRouter();
const route = useRoute();

// 当前激活的模块
const activeModule = ref("course");

// 切换模块
const switchModule = (module) => {
  activeModule.value = module;

  // 根据模块跳转到不同的路由
  const courseId = route.params.id;
  switch (module) {
    case "course":
      router.push(`/teaching/course/${courseId}/learn`);
      break;
    case "task":
      router.push(`/teaching/course/${courseId}/task`);
      break;
    case "practice":
      router.push(`/teaching/course/${courseId}/practice`);
      break;
    case "exam":
      router.push(`/teaching/course/${courseId}/exam`);
      break;
    case "resource":
      router.push(`/teaching/course/${courseId}/resource`);
      break;
    case "discussion":
      router.push(`/teaching/course/${courseId}/discussion`);
      break;
    case "knowledge":
      router.push(`/teaching/course/${courseId}/knowledge`);
      break;
  }
};

// 返回课程列表
const handleBack = () => {
  router.push("/coursesection");
};

// 初始化模块
const initModule = () => {
  const path = route.path;
  if (path.includes("/learn")) {
    activeModule.value = "course";
  } else if (path.includes("/task")) {
    activeModule.value = "task";
  } else if (path.includes("/practice")) {
    activeModule.value = "practice";
  } else if (path.includes("/exam")) {
    activeModule.value = "exam";
  } else if (path.includes("/resource")) {
    activeModule.value = "resource";
  } else if (path.includes("/discussion")) {
    activeModule.value = "discussion";
  } else if (path.includes("/knowledge")) {
    activeModule.value = "knowledge";
  }
};

// 监听路由变化
import { watch } from "vue";
watch(
  () => route.path,
  () => {
    initModule();
  }
);

// 初始化
initModule();

// 如果访问的是 /teaching/course/:id（没有子路径），默认跳转到课程列表页面
if (route.path.match(/^\/teaching\/course\/\w+$/)) {
  const courseId = route.params.id;
  router.replace(`/teaching/course/${courseId}/learn`);
}
</script>

<style scoped>
.course-learning {
  display: flex;
  padding: 20px;
  background: #fff6fb;
  min-height: 100vh;
  font-family: "Inter", Arial, sans-serif;
}

/* 左侧导航栏 */
.nav-sidebar {
  position: fixed;
  left: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  padding: 20px 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
}

.nav-menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  position: relative;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: #9b7a88;
  background: transparent;
}

.nav-item.back-item {
  margin-bottom: 12px;
  border: 1px solid #ffd6e7;
}

.nav-item.back-item:hover {
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  transform: scale(1.05);
  color: #d63384;
}

.nav-item:hover {
  background: linear-gradient(135deg, #fff0f6, #ffd6e7);
  transform: scale(1.1);
  color: #d63384;
}

.nav-item.active {
  background: linear-gradient(135deg, #d63384, #ff7ab1);
  color: white;
  box-shadow: 0 4px 12px rgba(214, 51, 132, 0.3);
  transform: scale(1.05);
}

.nav-item .el-icon {
  font-size: 24px;
  transition: transform 0.2s ease;
}

.nav-item:hover .el-icon {
  transform: scale(1.1);
}

/* 工具提示 */
.nav-tooltip {
  position: absolute;
  left: 70px;
  top: 50%;
  transform: translateY(-50%);
  padding: 8px 12px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  font-size: 13px;
  border-radius: 6px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s ease;
  pointer-events: none;
}

.nav-tooltip::before {
  content: "";
  position: absolute;
  left: -4px;
  top: 50%;
  transform: translateY(-50%);
  border: 4px solid transparent;
  border-right-color: rgba(0, 0, 0, 0.8);
}

.nav-item:hover .nav-tooltip {
  opacity: 1;
  visibility: visible;
  left: 60px;
}

/* 主内容区 */
.main-content {
  margin-left: 100px;
  width: calc(100% - 100px);
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.04);
  min-height: calc(100vh - 40px);
  overflow: hidden;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-sidebar {
    position: fixed;
    left: 10px;
    top: auto;
    bottom: 20px;
    transform: none;
    flex-direction: row;
    padding: 10px 20px;
    border-radius: 30px;
  }

  .nav-menu {
    flex-direction: row;
  }

  .nav-tooltip {
    display: none;
  }

  .back-button {
    left: auto;
    right: 20px;
    top: 20px;
  }

  .main-content {
    margin-left: 0;
    margin-right: 0;
    width: 100%;
    padding-bottom: 80px;
  }
}

/* 动画效果 */
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(214, 51, 132, 0.4);
  }

  70% {
    box-shadow: 0 0 0 10px rgba(214, 51, 132, 0);
  }

  100% {
    box-shadow: 0 0 0 0 rgba(214, 51, 132, 0);
  }
}

.nav-item.active {
  animation: pulse 2s infinite;
}
</style>
