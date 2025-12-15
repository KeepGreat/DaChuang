<template>
  <div class="question-resources">
    <div class="resources-header">
      <h4 class="resources-title">
        <i class="el-icon-folder-opened"></i>
        题目资源
      </h4>
    </div>
    
    <div class="resources-content">
      <!-- 测试用例资源 -->
      <div v-if="testCaseResources.length > 0" class="resource-group">
        <h5 class="resource-group-title">
          <i class="el-icon-document-copy"></i>
          测试用例
        </h5>
        <div class="resource-list">
          <div v-for="resource in testCaseResources" :key="resource.id" class="resource-item">
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
            </div>
            <el-button size="small" type="primary" @click="downloadResource(resource)">
              下载
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 用例答案资源 -->
      <div v-if="answerResources.length > 0" class="resource-group">
        <h5 class="resource-group-title">
          <i class="el-icon-check"></i>
          参考答案
        </h5>
        <div class="resource-list">
          <div v-for="resource in answerResources" :key="resource.id" class="resource-item">
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
            </div>
            <el-button size="small" type="primary" @click="downloadResource(resource)">
              下载
            </el-button>
          </div>
        </div>
      </div>
      
      <!-- 问题描述资料 -->
      <div v-if="descriptionResources.length > 0" class="resource-group">
        <h5 class="resource-group-title">
          <i class="el-icon-reading"></i>
          问题描述资料
        </h5>
        <div class="resource-list">
          <div v-for="resource in descriptionResources" :key="resource.id" class="resource-item">
            <div class="resource-info">
              <span class="resource-name">{{ resource.name }}</span>
              <span class="resource-size">{{ formatFileSize(resource.size) }}</span>
              <span v-if="resource.description" class="resource-description">{{ resource.description }}</span>
            </div>
            <el-button size="small" type="primary" @click="downloadResource(resource)">
              下载
            </el-button>
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
import { computed } from 'vue';
import { downloadQuestionResource } from '@/api';
import { ElMessage } from 'element-plus';
import { useQuestionResourceStore } from '@/store';

// 初始化store
const questionResourceStore = useQuestionResourceStore();

// Props定义
const props = defineProps({
  questionId: {
    type: [String, Number],
    required: true
  }
});

// 从store获取资源
const getResourcesByQuestionId = computed(() => questionResourceStore.getResourcesByQuestionId);
const getResourcesByType = computed(() => questionResourceStore.getResourcesByType);

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
</script>

<style scoped>
.question-resources {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e9ecef;
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

.no-resources {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px 0;
}
</style>