<template>
  <div v-if="visible" class="modal-overlay" @click.self="close">
    <div class="modal">
      <h2>编辑个人资料</h2>
      <label>昵称</label>
      <input v-model="form.name" placeholder="请输入昵称" />
      <label>专业</label>
      <input v-model="form.major" placeholder="例如：软件工程" />
      <label>简介</label>
      <textarea v-model="form.desc" placeholder="写点自己的学习目标..."></textarea>
      <div class="actions">
        <button @click="save">保存</button>
        <button class="cancel" @click="close">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const visible = ref(false)
const form = ref({ name: '胡桃同学', major: '软件工程', desc: '热爱AI与编程~' })

const open = () => (visible.value = true)
const close = () => (visible.value = false)
const save = () => {
  console.log('保存资料', form.value)
  close()
}
defineExpose({ open })
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal {
  background: white;
  border-radius: 16px;
  padding: 24px;
  width: 400px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
}
label {
  margin-top: 10px;
  font-weight: bold;
}
input, textarea {
  margin-top: 6px;
  border: 1px solid #ddd;
  border-radius: 6px;
  padding: 8px;
  width: 100%;
  font-size: 14px;
}
.actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  gap: 10px;
}
button {
  background: #409eff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
}
button.cancel {
  background: #bbb;
}
</style>
