<template>
  <div class="profile-card">
    <img src="@/assets/avatar-default.png" class="avatar" alt="avatar" />
    <div class="info">
      <h2 class="name">胡桃同学</h2>
      <p class="desc">软件工程专业 · 大三学生</p>
    </div>
    <div class="edit-section">
      <button class="edit-button" @click="$emit('edit-profile')">编辑资料</button>
    </div>
  </div>
</template>


<script setup>
// 添加更改头像的方法
const changeAvatar = () => {
  // 这里可以实现更改头像的逻辑
  console.log('更改头像功能')
}
</script>

<style scoped>
.profile-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 100%; /* 占据容器的全部宽度 */
  box-sizing: border-box;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 24px;
  flex-shrink: 0; /* 防止头像被压缩 */
}

.info {
  flex: 1;
  min-width: 0; /* 允许内容收缩 */
}

.name {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin: 0 0 8px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.desc {
  color: #666;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.edit-section {
  margin-left: 24px;
  flex-shrink: 0; /* 防止按钮被压缩 */
}

.edit-button {
  background: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  white-space: nowrap;
}

.edit-button:hover {
  background: #337ecc;
}
</style>