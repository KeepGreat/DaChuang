<template>
  <div class="question-resources">
     <!-- 图片问题描述资料 - 移到标题前展示 -->
    <div v-if="imageDescriptionResources.length > 0" class="resource-group image-resources-group">
      <div class="resource-list">
        <div v-for="resource in imageDescriptionResources" :key="resource.id" class="resource-item image-resource-item">
          <!-- 图片文件直接展示 -->
          <div class="image-preview-direct">
            <!-- 加载中状态 -->
            <div v-if="!getImageUrl(resource) && !failedImages.has(resource.id)" class="image-loading">
              <el-icon class="loading-icon"><Loading /></el-icon>
              <span class="loading-text">图片加载中...</span>
            </div>
            <!-- 加载失败状态 -->
            <div v-else-if="failedImages.has(resource.id)" class="image-error">
              <el-icon class="error-icon"><Picture /></el-icon>
              <span class="error-text">图片加载失败</span>
              <el-button size="small" type="primary" @click="retryLoadImage(resource)">
                重新加载
              </el-button>
            </div>
            <!-- 图片显示 -->
            <img 
              v-else
              :src="getImageUrl(resource)" 
              :alt="resource.name"
              class="direct-image"
              @error="handleImageError(resource)"
            />
          </div>
        </div>
      </div>
    </div>

    <div class="resources-header">
      <h4 class="resources-title">
        <el-icon><FolderOpened /></el-icon>
        题目资源
      </h4>
    </div>
    
    <div class="resources-content">
      <!-- 非图片问题描述资料 -->
      <div v-if="nonImageDescriptionResources.length > 0" class="resource-group">
        <h5 class="resource-group-title" @click="toggleGroup('description')" style="cursor: pointer; user-select: none;">
          <el-icon><Reading /></el-icon>
          问题描述资料
          <el-icon style="margin-left: auto; transition: transform 0.3s;" :style="{ transform: expandedGroups.description ? 'rotate(0deg)' : 'rotate(-90deg)' }">
            <ArrowDown />
          </el-icon>
        </h5>
        <div v-show="expandedGroups.description" class="resource-list">
          <div v-for="resource in nonImageDescriptionResources" :key="resource.id" class="resource-item">
            <!-- 非图片文件的信息展示 -->
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
              <span v-if="resource.description" class="resource-description">{{ resource.description }}</span>
            </div>
            
            <!-- 非图片文件的操作按钮 -->
            <div class="resource-actions">
              <el-button size="small" type="success" @click="previewResource(resource)">
                预览
              </el-button>
              <el-button size="small" type="primary" @click="downloadResource(resource)">
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 测试用例资源 -->
      <div v-if="testCaseResources.length > 0" class="resource-group">
        <h5 class="resource-group-title" @click="toggleGroup('testCase')" style="cursor: pointer; user-select: none;">
          <i class="el-icon-document-copy"></i>
          测试用例
          <el-icon style="margin-left: auto; transition: transform 0.3s;" :style="{ transform: expandedGroups.testCase ? 'rotate(0deg)' : 'rotate(-90deg)' }">
            <ArrowDown />
          </el-icon>
        </h5>
        <div v-show="expandedGroups.testCase" class="resource-list">
          <div v-for="resource in testCaseResources" :key="resource.id" class="resource-item">
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
            </div>
            <div class="resource-actions">
              <el-button size="small" type="primary" @click="downloadResource(resource)">
                下载
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 用例答案资源 -->
      <div v-if="answerResources.length > 0" class="resource-group">
        <h5 class="resource-group-title" @click="toggleGroup('answer')" style="cursor: pointer; user-select: none;">
          <i class="el-icon-check"></i>
          参考答案
          <el-icon style="margin-left: auto; transition: transform 0.3s;" :style="{ transform: expandedGroups.answer ? 'rotate(0deg)' : 'rotate(-90deg)' }">
            <ArrowDown />
          </el-icon>
        </h5>
        <div v-show="expandedGroups.answer" class="resource-list">
          <div v-for="resource in answerResources" :key="resource.id" class="resource-item">
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
            </div>
            <div class="resource-actions">
              <el-button size="small" type="primary" @click="downloadResource(resource)">
                下载
              </el-button>
            </div>
          </div>
          
          <!-- 文件预览区域 - 放在循环内部但只显示当前预览的文件 -->
          <div v-if="previewFileUrl && currentPreviewResource && descriptionResources.some(r => r.id === currentPreviewResource.id)" class="file-preview-container">
            <div class="preview-header">
              <span class="preview-title">{{ currentPreviewResource.name }}</span>
              <el-button size="small" circle @click="closePreview">
                <el-icon><Close /></el-icon>
              </el-button>
            </div>
            
            <!-- 图片预览 -->
            <img
              v-if="previewFileType.startsWith('image/')"
              :src="previewFileUrl"
              :alt="currentPreviewResource.name"
              class="preview-image"
            />

            <!-- PDF预览 -->
            <div v-else-if="previewFileType.startsWith('application/pdf')" class="pdf-preview-container">
              <div v-if="pdfPreviewError" class="pdf-preview-error">
                <p>PDF文件加载失败，可能是由于浏览器安全限制或文件格式不支持。</p>
                <el-button type="primary" size="small" @click="downloadResource(currentPreviewResource)">
                  下载PDF文件
                </el-button>
              </div>
              <iframe
                v-else
                :src="previewFileUrl"
                class="preview-iframe"
                @error="handlePdfPreviewError"
                @load="handlePdfPreviewLoad"
              ></iframe>
            </div>

            <!-- TXT预览 -->
            <div v-else-if="previewFileType === 'text/plain'" class="text-preview-container">
              <pre class="preview-text">{{ textContent }}</pre>
            </div>

            <!-- JSON预览 -->
            <div v-else-if="previewFileType === 'application/json'" class="json-preview-container">
              <pre class="preview-json">{{ jsonContent }}</pre>
            </div>

            <!-- PPT预览提示 -->
            <div v-else-if="previewFileType.includes('powerpoint') || previewFileType.includes('pptx') || previewFileType.includes('ppt')" class="ppt-preview-container">
              <div class="ppt-preview-info">
                <i class="el-icon-warning-outline"></i>
                <p>PPT文件无法直接在页面中预览</p>
                <p>请下载后查看完整内容</p>
                <el-button type="primary" size="small" @click="downloadResource(currentPreviewResource)">
                  下载PPT文件
                </el-button>
              </div>
            </div>

            <!-- 其他文件类型 -->
            <div v-else class="other-preview-container">
              <div class="other-preview-info">
                <i class="el-icon-document"></i>
                <p>该文件类型暂不支持预览</p>
                <p>文件类型：{{ previewFileType }}</p>
                <el-button type="primary" size="small" @click="downloadResource(currentPreviewResource)">
                  下载文件
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 无资源提示 -->
      <div v-if="allResources.length === 0" class="no-resources">
        <el-empty description="暂无相关资源" :image-size="80"></el-empty>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, nextTick, onMounted, watch } from 'vue';
import { downloadQuestionResource } from '@/api';
import { ElMessage } from 'element-plus';
import { useQuestionResourceStore } from '@/store';
import { FolderOpened, Reading, Close, Loading, Picture, ArrowDown } from '@element-plus/icons-vue';
import axios from 'axios';

// 初始化store
const questionResourceStore = useQuestionResourceStore();

// 文件预览相关状态
const previewFileUrl = ref('');
const previewFileType = ref('');
const currentPreviewResource = ref(null);
const pdfPreviewError = ref(false);
const textContent = ref('');
const jsonContent = ref('');

// 资源组展开/收起状态
const expandedGroups = ref({
  description: true,  // 问题描述资料默认展开
  testCase: true,     // 测试用例默认展开
  answer: true        // 参考答案默认展开
});

// Props定义
const props = defineProps({
  questionId: {
    type: [String, Number],
    required: true
  }
});

// 从store获取资源 - 直接调用方法，而不是使用computed
const getResourcesByQuestionId = questionResourceStore.getResourcesByQuestionId;
const getResourcesByType = questionResourceStore.getResourcesByType;

// 获取当前问题的所有资源
const allResources = computed(() => {
  return getResourcesByQuestionId(props.questionId) || [];
});

// 按类型分组资源
const testCaseResources = computed(() => {
  return getResourcesByType(0).filter(resource => resource.questionId === props.questionId);
});

const answerResources = computed(() => {
  return getResourcesByType(1).filter(resource => resource.questionId === props.questionId);
});

const descriptionResources = computed(() => {
  return getResourcesByType(2).filter(resource => resource.questionId === props.questionId);
});

// 获取所有图片资源
const imageResources = computed(() => {
  return descriptionResources.value.filter(resource => isImageFile(resource.name));
});

// 分离图片和非图片问题描述资料
const imageDescriptionResources = computed(() => {
  return descriptionResources.value.filter(resource => isImageFile(resource.name));
});

const nonImageDescriptionResources = computed(() => {
  return descriptionResources.value.filter(resource => !isImageFile(resource.name));
});

// 格式化文件大小
const formatFileSize = (size) => {
  if (!size) return '未知大小';
  
  if (size < 1024) {
    return size + ' B';
  } else if (size < 1024 * 1024) {
    return (size / 1024).toFixed(2) + ' KB';
  } else {
    return (size / (1024 * 1024)).toFixed(2) + ' MB';
  }
};

// 判断是否为图片文件
const isImageFile = (fileName) => {
  const imageExtensions = ['png', 'jpg', 'jpeg', 'gif'];
  const extension = fileName.split('.').pop().toLowerCase();
  return imageExtensions.includes(extension);
};

// 获取图片MIME类型
const getImageMimeType = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  const mimeTypes = {
    'png': 'image/png',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'gif': 'image/gif'
  };
  return mimeTypes[extension] || 'image/jpeg';
};

// 存储图片URL的映射
const imageUrls = ref(new Map());
// 存储加载失败的图片ID
const failedImages = ref(new Set());
// 强制更新计数器
const imageUpdateCounter = ref(0);

// 获取图片URL
const getImageUrl = (resource) => {
  // 如果已经有缓存的URL，直接返回
  if (imageUrls.value.has(resource.id)) {
    return imageUrls.value.get(resource.id);
  }
  
  return ''; // 还没有加载完成，返回空字符串
};

// 异步加载图片
const loadImage = async (resource) => {
  try {
    console.log('正在加载图片:', resource.name, 'ID:', resource.id);
    const response = await downloadQuestionResource(resource.id);
    console.log('图片下载成功:', resource.name, '数据大小:', response.data.size);
    
    // 验证响应数据
    if (!response.data || response.data.size === 0) {
      throw new Error('下载的文件为空');
    }
    
    const blob = new Blob([response.data], { type: getImageMimeType(resource.name) });
    const imageUrl = URL.createObjectURL(blob);
    
    // 验证URL是否创建成功
    if (!imageUrl) {
      throw new Error('无法创建图片URL');
    }
    
    // 缓存URL
    imageUrls.value.set(resource.id, imageUrl);
    // 强制更新界面
    imageUpdateCounter.value++;
    console.log('图片URL创建成功:', resource.name);
  } catch (error) {
    console.error('图片加载失败:', error);
    handleImageError(resource);
    // 设置一个空URL避免重复加载，并标记为失败
    imageUrls.value.set(resource.id, '');
    failedImages.value.add(resource.id);
  }
};

// 处理图片加载错误
const handleImageError = (resource) => {
  failedImages.value.add(resource.id);
  ElMessage({
    message: `图片 ${resource.name} 加载失败，请尝试下载查看`,
    type: 'error'
  });
};

// 重新加载图片
const retryLoadImage = (resource) => {
  console.log('重新加载图片:', resource.name);
  // 清除失败状态
  failedImages.value.delete(resource.id);
  // 清除缓存的URL
  imageUrls.value.delete(resource.id);
  // 重新加载
  loadImage(resource);
};

// 文件预览功能
const previewResource = async (resource) => {
  try {
    ElMessage({
      message: `正在加载 ${resource.name}...`,
      type: 'info',
      duration: 1000
    });

    // 重置预览状态
    closePreview();
    pdfPreviewError.value = false;
    textContent.value = '';
    jsonContent.value = '';

    // 获取文件数据
    const response = await downloadQuestionResource(resource.id);
    
    // 获取文件类型
    const contentType = response.headers['content-type'] || getFileType(resource.name);
    previewFileType.value = contentType;
    currentPreviewResource.value = resource;

    // 处理不同类型的文件
    if (contentType === 'text/plain') {
      // 文本文件直接读取内容
      textContent.value = await response.data.text();
    } else if (contentType === 'application/json') {
      // JSON文件格式化显示
      const jsonData = await response.data.text();
      jsonContent.value = JSON.stringify(JSON.parse(jsonData), null, 2);
    }

    // 创建文件URL用于预览
    const blob = new Blob([response.data], { type: contentType });
    previewFileUrl.value = window.URL.createObjectURL(blob);

    ElMessage({
      message: `${resource.name} 预览加载成功`,
      type: 'success'
    });
  } catch (error) {
    console.error('预览资源失败:', error);
    ElMessage({
      message: `预览 ${resource.name} 失败`,
      type: 'error'
    });
    closePreview();
  }
};

// 关闭预览
const closePreview = () => {
  if (previewFileUrl.value) {
    window.URL.revokeObjectURL(previewFileUrl.value);
  }
  previewFileUrl.value = '';
  previewFileType.value = '';
  currentPreviewResource.value = null;
  pdfPreviewError.value = false;
  textContent.value = '';
  jsonContent.value = '';
};

// 处理PDF预览错误
const handlePdfPreviewError = () => {
  pdfPreviewError.value = true;
};

// 处理PDF预览加载成功
const handlePdfPreviewLoad = () => {
  pdfPreviewError.value = false;
};

// 根据文件名获取文件类型
const getFileType = (fileName) => {
  const extension = fileName.split('.').pop().toLowerCase();
  const typeMap = {
    'txt': 'text/plain',
    'pdf': 'application/pdf',
    'ppt': 'application/vnd.ms-powerpoint',
    'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'json': 'application/json',
    'png': 'image/png',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'gif': 'image/gif'
  };
  return typeMap[extension] || 'application/octet-stream';
};

// 下载资源
const downloadResource = async (resource) => {
  try {
    ElMessage({
      message: `正在下载 ${resource.name}...`,
      type: 'info',
      duration: 1000
    });
    
    const response = await downloadQuestionResource(resource.id);
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', resource.name);
    document.body.appendChild(link);
    link.click();
    
    // 清理
    document.body.removeChild(link);
    window.URL.revokeObjectURL(url);
    
    ElMessage({
      message: `${resource.name} 下载成功`,
      type: 'success'
    });
  } catch (error) {
    console.error('下载资源失败:', error);
    ElMessage({
      message: `下载 ${resource.name} 失败`,
      type: 'error'
    });
  }
};

// 加载所有图片
const loadAllImages = () => {
  imageResources.value.forEach(resource => {
    if (!imageUrls.value.has(resource.id)) {
      loadImage(resource);
    }
  });
};

// 切换资源组展开/收起状态
const toggleGroup = (groupName) => {
  expandedGroups.value[groupName] = !expandedGroups.value[groupName];
};

// 组件挂载时加载所有图片
onMounted(() => {
  console.log('组件挂载，开始加载图片资源...');
  loadAllImages();
});

// 监听图片资源变化
watch(imageResources, (newResources) => {
  console.log('图片资源发生变化，重新加载...', newResources);
  loadAllImages();
}, { immediate: true });
</script>

<style scoped>
.question-resources {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
  max-height: 600px;
  overflow-y: auto;
}

/* 隐藏滚动条样式 */
.question-resources::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}

.question-resources::-webkit-scrollbar-track {
  background: transparent;
}

.question-resources::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 0px;
}

.question-resources::-webkit-scrollbar-thumb:hover {
  background: transparent;
}

.resources-header {
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e9ecef;
}

.resources-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.resources-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.resource-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-group-title {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  transition: all 0.2s ease;
}

.resource-group-title:hover {
  background: #f5f7fa;
  border-color: #c0c4cc;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-left: 20px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fff;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  transition: all 0.2s ease;
}

.resource-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.resource-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.resource-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.resource-size {
  font-size: 12px;
  color: #909399;
}

.resource-description {
  font-size: 12px;
  color: #606266;
  margin-top: 4px;
}

/* 图片资源组样式 */
.image-resources-group {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

/* 图片直接展示样式 */
.image-preview-direct {
  margin-top: 8px;
  text-align: center;
}

.direct-image {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 图片加载状态样式 */
.image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
  min-height: 100px;
}

.loading-icon {
  font-size: 24px;
  color: #409eff;
  animation: rotating 2s linear infinite;
  margin-bottom: 8px;
}

.loading-text {
  font-size: 12px;
  color: #909399;
}

@keyframes rotating {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

/* 图片加载失败状态样式 */
.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: #fef0f0;
  border-radius: 4px;
  border: 1px dashed #fbc4c4;
  min-height: 100px;
}

.error-icon {
  font-size: 24px;
  color: #f56c6c;
  margin-bottom: 8px;
}

.error-text {
  font-size: 12px;
  color: #f56c6c;
  margin-bottom: 8px;
}

.no-resources {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}

/* 文件预览样式 */
.file-preview-container {
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  max-height: 400px;
  overflow: auto;
}

/* 隐藏文件预览容器滚动条 */
.file-preview-container::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}

.file-preview-container::-webkit-scrollbar-track {
  background: transparent;
}

.file-preview-container::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 0px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
}

.preview-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.preview-image {
  max-width: 100%;
  max-height: 350px;
  display: block;
  margin: 0 auto;
}

.pdf-preview-container {
  width: 100%;
  height: 350px;
  position: relative;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.pdf-preview-error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 200px;
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.pdf-preview-error p {
  margin-bottom: 12px;
  color: #606266;
  text-align: center;
}

.text-preview-container,
.json-preview-container {
  max-height: 350px;
  overflow: auto;
}

/* 隐藏文本预览容器滚动条 */
.text-preview-container::-webkit-scrollbar,
.json-preview-container::-webkit-scrollbar {
  width: 0px;
  height: 0px;
}

.text-preview-container::-webkit-scrollbar-track,
.json-preview-container::-webkit-scrollbar-track {
  background: transparent;
}

.text-preview-container::-webkit-scrollbar-thumb,
.json-preview-container::-webkit-scrollbar-thumb {
  background: transparent;
  border-radius: 0px;
}

.preview-text,
.preview-json {
  margin: 0;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 13px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-all;
}

.preview-json {
  color: #e6a23c;
}

.ppt-preview-container,
.other-preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  background: #f8f9fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.ppt-preview-info,
.other-preview-info {
  text-align: center;
}

.ppt-preview-info i,
.other-preview-info i {
  font-size: 48px;
  color: #909399;
  margin-bottom: 12px;
}

.ppt-preview-info p,
.other-preview-info p {
  margin: 4px 0;
  color: #606266;
}

.resource-actions {
  display: flex;
  gap: 8px;
}
</style>