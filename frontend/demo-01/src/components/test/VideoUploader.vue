<template>
  <div class="video-uploader">
    <h3>视频上传</h3>
    
    <!-- 视频选择部分 -->
    <div class="upload-section">
      <input 
        type="file" 
        accept="video/*" 
        @change="handleFileSelect"
        class="file-input"
      />
      <button 
        @click="startUpload" 
        :disabled="!selectedFile || isUploading"
        class="upload-btn"
      >
        {{ isUploading ? '上传中...' : '开始上传' }}
      </button>
      <button 
        @click="cancelUpload" 
        :disabled="!isUploading"
        class="cancel-btn"
      >
        取消上传
      </button>
    </div>
    
    <!-- VideoContent字段展示 -->
    <div class="video-content-fields">
      <h4>视频信息</h4>
      <div class="fields-grid">
        <div class="field-item">
          <label>类型：</label>
          <input v-model="videoContent.type" type="text" placeholder="视频类型" />
        </div>
        <div class="field-item">
          <label>名称：</label>
          <input v-model="videoContent.name" type="text" placeholder="视频名称" />
        </div>
        <div class="field-item">
          <label>大小：</label>
          <input v-model.number="videoContent.size" type="number" placeholder="视频大小" />
        </div>
        <div class="field-item">
          <label>时长(s)：</label>
          <input v-model.number="videoContent.duration" type="number" placeholder="视频时长(秒)" />
        </div>
        <div class="field-item">
          <label>资料ID：</label>
          <input v-model.number="videoContent.matId" type="number" placeholder="相关资料ID" />
        </div>
      </div>
    </div>
    
    <!-- 分片信息展示 -->
    <div v-if="selectedFile" class="chunk-info">
      <h4>分片信息</h4>
      <p>总分片数：{{ chunks.length }}</p>
      <table class="chunk-table">
        <thead>
          <tr>
            <th>分片序号</th>
            <th>分片大小</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="chunk in chunks" :key="chunk.number">
            <td>{{ chunk.number }}</td>
            <td>{{ formatFileSize(chunk.size) }}</td>
            <td>
              <span 
                :class="['status-badge', chunk.status]"
              >
                {{ chunk.statusText }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 上传进度 -->
    <div v-if="isUploading" class="progress-section">
      <div class="progress-bar">
        <div 
          class="progress-fill" 
          :style="{ width: uploadProgress + '%' }"
        ></div>
      </div>
      <p class="progress-text">
        上传进度：{{ uploadProgress }}% ({{ uploadedChunks }}/{{ chunks.length }} 分片)
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import * as VideoContentAPI from '@/api/modules/teaching/VideoContentAPI';
import * as VideoChunkAPI from '@/api/modules/teaching/VideoChunkAPI';

// 响应式数据
const selectedFile = ref(null);
const videoContent = ref({
  type: '',
  name: '',
  size: 0,
  duration: 0,
  matId: 0
});
const videoId = ref(null);
const videoName = ref(null);
const isUploading = ref(false);
const uploadProgress = ref(0);
const uploadedChunks = ref(0);
const chunks = ref([]);
const chunkSize = 512 * 1024 * 1024; // 512MB

// 计算属性
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 方法
const handleFileSelect = (event) => {
  const file = event.target.files[0];
  if (file) {
    selectedFile.value = file;
    videoContent.value.name = file.name;
    videoContent.value.size = file.size;
    videoContent.value.type = file.type;
    // 解析视频时长
    const video = document.createElement('video');
    video.preload = 'metadata';
    video.onloadedmetadata = function() {
      // 获取视频时长（秒）
      const duration = video.duration;
      videoContent.value.duration = Math.round(duration);
      console.log('视频时长解析完成:', duration, '秒');
    };
    video.onerror = function() {
      console.error('视频时长解析失败');
    };
    video.src = URL.createObjectURL(file);
    
    // 生成分片信息
    generateChunks(file);
  }
};

const generateChunks = (file) => {
  const totalChunks = Math.ceil(file.size / chunkSize);
  const newChunks = [];
  
  for (let i = 0; i < totalChunks; i++) {
    const start = i * chunkSize;
    const end = Math.min(start + chunkSize, file.size);
    const size = end - start;
    
    newChunks.push({
      number: i + 1,
      total: totalChunks,
      size: size,
      status: 'pending',
      statusText: '待上传'
    });
  }
  
  chunks.value = newChunks;
};

const startUpload = async () => {
  if (!selectedFile.value) {
    alert('请先选择视频文件');
    return;
  }
  
  isUploading.value = true;
  uploadProgress.value = 0;
  uploadedChunks.value = 0;
  
  try {
    // 1. 检查是否有之前的videoId，如有则删除
    if (videoId.value) {
      await VideoChunkAPI.deleteChunk(videoId.value);
    }
    
    // 2. 调用addVideoContent获取视频ID
    const addResponse = await VideoContentAPI.addVideoContent(videoContent.value);
    videoId.value = addResponse.data.id;
    videoName.value = addResponse.data.name;
    // 存储videoId到sessionStorage
    sessionStorage.setItem('videoId', videoId.value);
    console.log('获取视频ID:', videoId.value);
    
    // 3. 上传分片
    await uploadChunks(selectedFile.value);
    
    // 4. 合并分片
    const mergeData = {
      id: videoId.value,
      name: videoName.value
    };
    await VideoChunkAPI.mergeChunk(mergeData);
    console.log('分片合并成功');
    
    // 上传成功后清除sessionStorage中的videoId
    sessionStorage.removeItem('videoId');
    videoId.value = null;
    
    alert('视频上传成功！');
  } catch (error) {
    console.error('上传失败:', error);
    alert('上传失败，请重试');
  } finally {
    isUploading.value = false;
  }
};

const uploadChunks = async (file) => {
  for (let i = 0; i < chunks.value.length; i++) {
    const chunkInfo = chunks.value[i];
    chunkInfo.status = 'uploading';
    chunkInfo.statusText = '上传中';
    
    try {
      // 生成VideoChunk对象
      const videoChunk = {
        number: chunkInfo.number,
        total: chunkInfo.total,
        size: chunkInfo.size,
        videoId: videoId.value
      };
      
      // 提取分片文件
      const start = (chunkInfo.number - 1) * chunkSize;
      const end = Math.min(start + chunkSize, file.size);
      const chunkFile = file.slice(start, end);
      
      // 上传分片
      await VideoChunkAPI.uploadChunk(JSON.stringify(videoChunk), chunkFile);
      
      // 检查分片是否上传成功
      await VideoChunkAPI.checkChunk(videoId.value, chunkInfo.number);
      
      chunkInfo.status = 'success';
      chunkInfo.statusText = '上传成功';
      uploadedChunks.value = i + 1;
      uploadProgress.value = Math.round((uploadedChunks.value / chunks.value.length) * 100);
    } catch (error) {
      console.error(`上传分片 ${chunkInfo.number} 失败:`, error);
      chunkInfo.status = 'error';
      chunkInfo.statusText = '上传失败';
      throw error;
    }
  }
};

const cancelUpload = () => {
  isUploading.value = false;
  // 可以在这里添加其他取消逻辑
};

// 组件挂载时检查是否有保存的videoId
onMounted(() => {
  // 这里可以从localStorage或其他地方获取之前保存的videoId
  videoId.value = sessionStorage.getItem('videoId');
});
</script>

<style scoped>
.video-uploader {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
  background-color: #f9f9f9;
}

h3, h4 {
  margin-top: 0;
  color: #333;
}

.upload-section {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.file-input {
  margin-right: 10px;
}

.upload-btn, .cancel-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  margin-right: 10px;
}

.upload-btn {
  background-color: #4CAF50;
  color: white;
}

.upload-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #f44336;
  color: white;
}

.cancel-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.video-content-fields {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.fields-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
}

.field-item {
  display: flex;
  flex-direction: column;
}

.field-item label {
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.field-item input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.chunk-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chunk-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 10px;
}

.chunk-table th,
.chunk-table td {
  padding: 8px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.chunk-table th {
  background-color: #f2f2f2;
  font-weight: 600;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-badge.pending {
  background-color: #ffc107;
  color: #333;
}

.status-badge.uploading {
  background-color: #17a2b8;
  color: white;
}

.status-badge.success {
  background-color: #28a745;
  color: white;
}

.status-badge.error {
  background-color: #dc3545;
  color: white;
}

.progress-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-bar {
  width: 100%;
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background-color: #4CAF50;
  border-radius: 10px;
  transition: width 0.3s ease;
}

.progress-text {
  margin: 0;
  text-align: center;
  color: #555;
}
</style>