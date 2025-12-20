<template>
  <div class="layout">
    <!-- 顶部导航栏 -->
    <header class="top-nav">
      <div class="nav-left">
        <div class="platform-name">慧编未来 · AI 教学平台</div>
      </div>

      <div class="nav-right">
        <!-- 搜索 -->
        <div class="nav-btn">
          🔍 <span class="btn-text">搜索</span>
        </div>

        <!-- 通知 -->
        <div class="nav-btn">
          🔔 <span class="btn-text">通知</span>
        </div>

        <!-- 个人中心 -->
        <div class="nav-btn avatar-btn">
          <img src="https://api.iconify.design/ri:user-3-fill.svg" alt="" />
          <span class="btn-text">个人中心</span>
        </div>
      </div>
    </header>

    <!-- 主体 -->
    <div class="main-content">
      <div class="content full">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
// sidebar removed; content occupies full width
</script>

<style scoped>

/* --- 布局基础 --- */
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fdf6f9;
  font-family: "Segoe UI", Helvetica, Arial;
}

/* --- 顶栏 --- */
.top-nav {
  height: 60px;
  background: #ffffffcc;
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 18px;
  border-bottom: 2px solid #e996b9;
  box-shadow: 0 3px 12px rgba(255, 182, 193, 0.25);
}

/* 左侧 */
.nav-left {
  display: flex;
  align-items: center;
}

.platform-name {
  margin-left: 12px;
  font-size: 18px;
  font-weight: bold;
  color: #d63384;
}

/* 右侧按钮组 */
.nav-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 顶栏按钮（带动画） */
.nav-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  color: #b2246e;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.2s ease;
}

.nav-btn:hover {
  background: #ffe3ef;
  transform: scale(1.08);
  box-shadow: 0 2px 6px rgba(255, 105, 180, 0.3);
}

.avatar-btn img {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

/* 文字样式：大屏可见，小屏隐藏 */
.btn-text {
  font-size: 15px;
  font-weight: 500;
}

/* 小屏隐藏文字，只保留图标 */
@media (max-width: 780px) {
  .btn-text {
    display: none;
  }
}

.main-content {
  flex: 1;
  display: flex;
}
.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
  width: 100%;
}
.content.full {
  width: 100%;
}
</style>
