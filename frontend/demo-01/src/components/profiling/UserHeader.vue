<template>
  <div class="user-header-card">
    <div class="avatar-section">
      <!-- 头像 -->
      <img class="avatar" :src="avatarUrl" alt="avatar" />
      
      <!-- 用户名 -->
      <div class="name">{{ username }}</div>
      
      <!-- 证书数量 -->
      <div class="cert-count">{{ certificateCount }} 证书</div>
      
      <!-- 操作按钮 -->
      <div class="actions">
        <button class="btn-edit" @click="$emit('edit')">编辑资料</button>
        <button class="btn-share">分享</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import avatarDefault from "@/assets/avatar-default.png";

// 定义组件 props
defineProps({
  username: { type: String, default: "未命名用户" },
  certificateCount: { type: Number, default: 0 },
  avatarUrl: { type: String, default: avatarDefault },
});
</script>

<style scoped>
/* 卡片整体 */
.user-header-card {
  width: 100%; /* 占满父容器宽度 */
  min-height: 240px; /* 最小高度限制 */
  height: clamp(240px, 30vh, 320px); /* 自适应高度，限制最小最大值 */
  background: linear-gradient(135deg, #e09fff 0%, #ffffff 100%); /* 背景渐变 */
  border-radius: 16px; /* 卡片圆角 */
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06); /* 卡片阴影 */
  display: flex; /* 使用 Flex 布局 */
  flex-direction: column; /* 垂直排列子元素 */
  align-items: center; /* 水平居中 */
  padding-top: 5%; /* 顶部留白百分比 */
  padding-bottom: 5%; /* 底部留白百分比 */
  box-sizing: border-box; /* 包含 padding 在内计算宽高 */
}

.user-header-card:hover {
  transform: scale(1.008);
  box-shadow: 0 8px 20px rgba(0,0,0,0.15);
}

/* 头像区域 */
.avatar-section {
  width: 80%;                      /* 宽度占卡片80% */
  display: flex;                    /* Flex布局 */
  flex-direction: column;           /* 垂直排列 */
  align-items: center;              /* 水平居中 */
  margin-top: 5%;                   /* 顶部留白百分比，保证不贴顶 */
  margin-bottom: 5%;                /* 底部留白百分比，保证不溢出 */
}

/* 头像 */
.avatar {
  width: clamp(40px, 25%, 100px);  /* 宽度自适应，最小40px，最大100px */
  aspect-ratio: 1;                  /* 高宽比1:1 */
  border-radius: 50%;               /* 圆形 */
  object-fit: cover;                /* 填充图片 */
  border: clamp(3px, 2%, 6px) solid #fff; /* 白色边框，随头像缩放 */
  box-shadow: 0 clamp(4px, 0.7%, 8px) clamp(12px, 2%, 18px) rgba(16,24,40,0.12); /* 阴影自适应 */
  background: #f3f4f6;              /* 加载失败背景色 */
  margin-top: -10%;                     /* 不额外顶部间距，靠 avatar-section 控制 */
}

/* 用户名 */
.name {
  margin-top: 2%;                    /* 顶部间距百分比 */
  font-size: clamp(16px, 3%, 24px);  /* 字体自适应 */
  font-weight: 700;                  
  color: #111827;                    
  text-align: center;                
  width: 100%;                       
}

/* 证书数量 */
.cert-count {
  margin-top: 1%;                     /* 顶部间距百分比 */
  font-size: clamp(12px, 2%, 16px);   /* 字体自适应 */
  color: #6b7280;                     
  text-align: center;                 
  width: 100%;                        
}

/* 按钮组 */
.actions {
  margin-top: 3%;                     /* 顶部间距百分比 */
  display: flex;                      
  gap: 3%;                            /* 按钮间距 */
  justify-content: center;            
  width: 100%;                        
}

/* 通用按钮样式 */
.btn-edit,
.btn-share {
  padding: clamp(4px, 1%, 10px) clamp(12px, 3%, 20px); /* 内边距自适应 */
  border-radius: 10px; /* 圆角 */
  border: 1px solid #e5e7eb; /* 边框 */
  background: #ffffff; /* 背景色 */
  cursor: pointer; /* 鼠标手型 */
  font-weight: 600; /* 字体加粗 */
  font-size: clamp(12px, 2%, 16px); /* 字体自适应 */
  transition: all 0.25s ease; /* 悬停动画，延迟平滑 */
}

/* 鼠标悬停效果 */
.btn-edit:hover,
.btn-share:hover {
  transform: translateY(-2%) scale(1.05); /* 悬停上移 + 微放大 */
  background: #f0f4ff; /* 背景颜色变化 */
  border-color: #cbd5e1; /* 边框颜色变化 */
  box-shadow: 0 8px 18px rgba(16,24,40,0.15); /* 阴影增强 */
  color: #1d4ed8; /* 字体颜色变化 */
}


/* 响应式处理，小屏幕下自适应 */
@media (max-width: 640px) {
  .user-header-card {
    height: clamp(220px, 28vh, 280px);
  }
  .avatar { width: clamp(70px, 30%, 120px); }
  .name { font-size: clamp(14px, 4%, 20px); }
  .cert-count { font-size: clamp(10px, 3%, 14px); }
  .btn-edit, .btn-share { font-size: clamp(10px, 3%, 14px); }
}
</style>
