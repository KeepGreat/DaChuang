<template>
  <div class="workstation-page">
    <section class="hero-section">
      <h1 class="platform-title">慧编未来</h1>
      <p class="platform-subtitle">智能编程教育平台</p>
      <p class="platform-slogan">让每一次教学，都更高效、更精准、更有温度。</p>
    </section>

    <section class="entry-section">
      <button class="entry-card" type="button" @click="goCourse">
        <h2>课程</h2>
        <p>进入课程管理与教学资源组织空间</p>
      </button>

      <button class="entry-card" type="button" @click="goPractice">
        <h2>练习</h2>
        <p>进入练习发布、批改与数据查看空间</p>
      </button>

      <button class="entry-card" type="button" @click="goPromptEditor">
        <h2>提示词</h2>
        <p>进入提示词配置与运行时应用空间</p>
      </button>

      <button v-if="isAdmin" class="entry-card" type="button" @click="goUserEditor">
        <h2>用户管理</h2>
        <p>进入用户信息维护与权限配置空间</p>
      </button>
    </section>
  </div>
</template>

<script setup>
import { useUserStore } from '@/store'
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const isAdmin = computed(() => userStore.role === 'admin')

const goCourse = () => {
  router.push('/teacher/cs')
}

const goPractice = () => {
  router.push('/teacher/pd')
}

const goPromptEditor = () => {
  router.push('/teacher/pme')
}

const goUserEditor = () => {
  router.push('/admin/user')
}
</script>

<style scoped lang="scss">
.workstation-page {
  min-height: 100vh;
  padding: 40px 24px;
  background:
    radial-gradient(circle at 16% 18%, var(--primary-alpha-20) 0%, transparent 42%),
    radial-gradient(circle at 88% 86%, var(--primary-alpha-10) 0%, transparent 40%),
    linear-gradient(180deg, var(--bg-white) 0%, var(--bg-light) 100%);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.hero-section {
  height: 30vh;
  min-height: 220px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.platform-title {
  margin: 0;
  font-size: clamp(80px, 14vw, 160px);
  font-weight: 900;
  line-height: 1;
  letter-spacing: 4px;
  color: #3f51f2;
}

.platform-subtitle {
  margin: 16px 0 8px;
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-dark);
}

.platform-slogan {
  margin: 0;
  font-size: 18px;
  color: var(--text-regular);
}

.entry-section {
  width: min(900px, 100%);
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.entry-card {
  border: 1px solid var(--border-primary-lighter);
  border-radius: 16px;
  padding: 24px;
  text-align: left;
  background: linear-gradient(145deg, var(--bg-white), var(--bg-primary-light));
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease, border-color 0.25s ease;
}

.entry-card:hover {
  border-color: var(--border-primary);
  box-shadow: 0 12px 24px var(--primary-alpha-20);
  transform: translateY(-4px);
}

.entry-card h2 {
  margin: 0 0 10px;
  font-size: 24px;
  color: var(--primary-dark);
}

.entry-card p {
  margin: 0;
  color: var(--text-regular);
  line-height: 1.6;
}

@media (max-width: 768px) {
  .workstation-page {
    padding: 28px 16px;
    gap: 24px;
  }

  .hero-section {
    min-height: 180px;
  }

  .platform-title {
    font-size: clamp(56px, 18vw, 88px);
    letter-spacing: 2px;
  }

  .platform-subtitle {
    font-size: 20px;
  }

  .platform-slogan {
    font-size: 15px;
  }

  .entry-section {
    grid-template-columns: 1fr;
  }
}
</style>
