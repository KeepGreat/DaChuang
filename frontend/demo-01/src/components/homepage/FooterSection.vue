<template>
  <footer class="footer">
    <div>© {{ (new Date()).getFullYear() }} 慧编未来 · AI 学习平台</div>
    <div class="footer-links">
      <a @click.prevent="go('community')">社区</a>
      <a @click.prevent="go('profile')">关于我们</a>
    </div>
  </footer>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();
function go(target) {
  if(target==='profile') router.push("/profile");
  else if(target==='community') router.push("/community");
}
</script>

<style scoped>
.footer { margin-top:auto; padding:28px 6vw; display:flex; justify-content:space-between; align-items:center; color:#8b6a76; }
.footer a { color:#336cd6; cursor:pointer; }
</style>
