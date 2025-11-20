<template>
  <div class="profile-page">
    
    <!-- 顶部个人信息 -->
    <UserHeader
      :username="user.name"
      :certificateCount="user.certs"
      :avatarUrl="user.avatar"
      @edit="openEdit"
    />
    <div class="section-title">
      <RouterView v-slot="{Component, route}">
        <transition name="fade" mode="out-in">
          <component :is="Component" :key="route.fullPath" />
        </transition>
      </RouterView>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import UserHeader from "@/components/profiling/UserHeader.vue";

const user = ref({
  name: "Qingtian1111",
  certs: 0,
  avatar: avatarDefault,  
  progress: 2,
  finished: 0,
  hours: 0
});

const openEdit = () => console.log("打开编辑弹窗");
</script>

<style scoped>
.profile-page {
  max-width: 1200px;
  margin: auto;
  padding: 40px 20px;
   margin-top: 20px;
}

.section-title {
  margin-top: 32px;      /* 顶部间距保持不变 */
  margin-bottom: -16px;   /* 改小底部间距，让它离下面内容更近 */
  font-size: 20px;
  font-weight: bold;
}

</style>
