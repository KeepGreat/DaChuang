<template>
  <div class="video-player-container">
    <h3>视频播放器</h3>
    
    <div class="video-input-section">
      <label for="videoFileName">视频文件名：</label>
      <input 
        type="text" 
        id="videoFileName" 
        v-model="fileName" 
        placeholder="请输入视频文件名"
      />
      <button 
        @click="loadVideo" 
        :disabled="!fileName"
        class="load-btn"
      >
        加载视频
      </button>
    </div>
    
    <div class="video-container">
      <video 
        ref="videoPlayer" 
        class="video-js vjs-default-skin vjs-big-play-centered"
        controls
        preload="auto"
        width="100%"
        height="auto"
      >
        <p class="vjs-no-js">
          要观看此视频，请启用JavaScript，并考虑升级到支持HTML5视频的
          <a href="https://videojs.com/html5-video-support/" target="_blank">现代浏览器</a>
        </p>
      </video>
    </div>
    
    <div v-if="loading" class="loading-overlay">
      <div class="loading-spinner"></div>
      <p>加载视频中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import videojs from 'video.js';
import 'video.js/dist/video-js.css';
import * as VideoContentAPI from '@/api/modules/teaching/VideoContentAPI';

// 响应式数据
const videoPlayer = ref(null);
const player = ref(null);
const fileName = ref('');
const loading = ref(false);
const currentVideoUrl = ref(null); // 保存当前视频URL

// 初始化播放器
onMounted(() => {
  // 当DOM元素可用时初始化video.js
  if (videoPlayer.value) {
    player.value = videojs(videoPlayer.value, {
      autoplay: false,
      controls: true,
      preload: 'auto',
      responsive: true,
      fluid: true
    });
    
    // 监听播放器的时间更新，用于断点续传
    player.value.on('timeupdate', handleTimeUpdate);
  }
});

// 清理播放器实例
onUnmounted(() => {
  // 释放blob URL
  if (currentVideoUrl.value) {
    URL.revokeObjectURL(currentVideoUrl.value);
    currentVideoUrl.value = null;
  }
  // 清理播放器
  if (player.value) {
    player.value.dispose();
  }
});

// 加载视频
const loadVideo = async () => {
  if (!fileName.value) {
    alert('请输入视频文件名');
    return;
  }
  
  loading.value = true;
  
  try {
    // 调用streamVideo方法获取视频流
    const response = await VideoContentAPI.streamVideo(fileName.value);
    
    // 从响应中获取blob数据
    // 注意：根据axios的配置，responseType为blob时，响应数据在response.data中
    const blob = response.data;
    
    // 创建blob URL
    const videoUrl = URL.createObjectURL(blob);
    currentVideoUrl.value = videoUrl; // 保存视频URL
    
    // 重新加载视频源
    if (player.value) {
      // 先暂停播放器
      player.value.pause();
      // 清除之前的src
      player.value.src('');
      // 加载新的src
      player.value.src({
        src: videoUrl,
        type: 'video/mp4'
      });
      // 重新加载
      player.value.load();
      // 释放blob URL
    }
    
    console.log('视频加载成功');
  } catch (error) {
    console.error('视频加载失败:', error);
    alert('视频加载失败，请检查文件名是否正确');
  } finally {
    loading.value = false;
  }
};

// 处理时间更新，用于断点续传
const handleTimeUpdate = () => {
  // 这里可以添加断点续传的逻辑
  // 例如记录当前播放位置，以便下次继续播放
};
</script>

<style scoped>
.video-player-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

h3 {
  margin-top: 0;
  color: #333;
}

.video-input-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.video-input-section label {
  margin-right: 10px;
  font-weight: 500;
  color: #555;
}

.video-input-section input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 300px;
  margin-right: 10px;
}

.load-btn {
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.load-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.video-container {
  margin-top: 20px;
  background-color: #000;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

/* 视频播放器样式 */
:deep(.video-js) {
  width: 100% !important;
  height: auto !important;
  aspect-ratio: 16/9;
}

:deep(.vjs-big-play-button) {
  font-size: 2.5em !important;
  line-height: 2.3em !important;
  height: 2.5em !important;
  width: 2.5em !important;
  border-radius: 50% !important;
  background-color: rgba(0, 0, 0, 0.5) !important;
  border: 0.1em solid #fff !important;
  margin-top: -1.25em !important;
  margin-left: -1.25em !important;
}

/* 确保控制栏正确显示 */
:deep(.vjs-control-bar) {
  display: flex !important;
  visibility: visible !important;
  opacity: 1 !important;
}

:deep(.vjs-time-control) {
  display: flex !important;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  border-radius: 4px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid rgba(255, 255, 255, 0.3);
  border-top: 5px solid #fff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

.loading-overlay p {
  color: #fff;
  font-size: 16px;
  margin: 0;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>