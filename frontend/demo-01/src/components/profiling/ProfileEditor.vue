<template>
  <div
    v-if="visible"
    class="modal-overlay"
    @click.self="close"
  >
    <div class="modal" @click.stop>
      <h2 class="modal-title">编辑个人资料</h2>

      <div class="form-group">
        <label>昵称</label>
        <input v-model="form.name" placeholder="请输入昵称" />
      </div>

      <div class="form-group">
        <label>专业</label>
        <input v-model="form.major" placeholder="例如：软件工程" />
      </div>

      <div class="form-group">
        <label>简介</label>
        <textarea v-model="form.desc" placeholder="写点自己的学习目标..."></textarea>
      </div>

      <div class="actions">
        <button class="btn-cancel" @click="close">取消</button>
        <button class="btn-save" @click="save">保存</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
const visible = ref(false);
const form = ref({
  name: "胡桃同学",
  major: "软件工程",
  desc: "热爱AI与编程~",
});

const open = () => (visible.value = true);
const close = () => (visible.value = false);
const save = () => {
  console.log("保存资料", form.value);
  alert(`保存成功！\n昵称：${form.value.name}\n专业：${form.value.major}`);
  close();
};
defineExpose({ open });
</script>

<style scoped>
/* 遮罩层 */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* 🚀 关键点1：层级足够高 */
  backdrop-filter: blur(4px);
}

/* 弹窗主体 */
.modal {
  background: white;
  border-radius: 16px;
  padding: 28px;
  width: 420px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  position: relative;
  z-index: 10000; /* 🚀 关键点2：比图表还高 */
  animation: fadeInUp 0.25s ease;
}

/* 标题 */
.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f2937;
}

/* 输入区域样式 */
.form-group {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}
label {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}
input,
textarea {
  border: 1px solid #d1d5db;
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s;
}
input:focus,
textarea:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.2);
}

/* 按钮区域 */
.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 16px;
}
button {
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  padding: 8px 16px;
  transition: all 0.2s;
}
.btn-save {
  background: #3b82f6;
  color: white;
}
.btn-save:hover {
  background: #2563eb;
}
.btn-cancel {
  background: #e5e7eb;
  color: #374151;
}
.btn-cancel:hover {
  background: #d1d5db;
}

/* 弹窗动画 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
