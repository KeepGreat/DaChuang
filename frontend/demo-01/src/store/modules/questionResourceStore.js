import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * 问题资源状态管理
 * 专门用于存储问题相关的资源数据
 */
export const useQuestionResourceStore = defineStore('questionResource', () => {
  // State
  const resources = ref([]); // 存储问题资源数据

  // Getters
  /**
   * 获取所有资源
   */
  const getAllResources = computed(() => resources.value);

  /**
   * 根据ID获取资源
   */
  const getResourceById = computed(() => {
    return (resourceId) => resources.value.find(resource => resource.id === resourceId);
  });

  /**
   * 根据问题ID获取资源列表
   */
  const getResourcesByQuestionId = computed(() => {
    return (questionId) => resources.value.filter(resource => resource.questionId === questionId);
  });

  /**
   * 根据资源类型获取资源列表
   * @param {number} type - 资源类型（0:测试用例,1:用例答案,2:问题描述资料）
   */
  const getResourcesByType = computed(() => {
    return (type) => resources.value.filter(resource => resource.type === type);
  });

  /**
   * 根据问题ID和资源类型获取资源列表
   */
  const getResourcesByQuestionIdAndType = computed(() => {
    return (questionId, type) => resources.value.filter(resource => 
      resource.questionId === questionId && resource.type === type
    );
  });

  // Actions
  /**
   * 设置所有资源数据
   * @param {Array} newResources - 新的资源列表
   */
  const setResources = (newResources) => {
    resources.value = newResources;
  };

  /**
   * 添加单个资源
   * @param {Object} resource - 资源对象
   */
  const addResource = (resource) => {
    resources.value.push(resource);
  };

  /**
   * 批量添加资源
   * @param {Array} newResources - 资源列表
   */
  const addResources = (newResources) => {
    if (Array.isArray(newResources)) {
      resources.value.push(...newResources);
    }
  };

  /**
   * 更新资源
   * @param {number} resourceId - 资源ID
   * @param {Object} updatedData - 更新的数据
   */
  const updateResource = (resourceId, updatedData) => {
    const index = resources.value.findIndex(resource => resource.id === resourceId);
    if (index !== -1) {
      resources.value[index] = { ...resources.value[index], ...updatedData };
    }
  };

  /**
   * 删除资源
   * @param {number} resourceId - 资源ID
   */
  const deleteResource = (resourceId) => {
    resources.value = resources.value.filter(resource => resource.id !== resourceId);
  };

  /**
   * 删除指定问题的所有资源
   * @param {number} questionId - 问题ID
   */
  const deleteResourcesByQuestionId = (questionId) => {
    resources.value = resources.value.filter(resource => resource.questionId !== questionId);
  };

  /**
   * 清空所有资源
   */
  const clearAllResources = () => {
    resources.value = [];
  };

  return {
    // State
    resources,

    // Getters
    getAllResources,
    getResourceById,
    getResourcesByQuestionId,
    getResourcesByType,
    getResourcesByQuestionIdAndType,

    // Actions
    setResources,
    addResource,
    addResources,
    updateResource,
    deleteResource,
    deleteResourcesByQuestionId,
    clearAllResources
  };
});
