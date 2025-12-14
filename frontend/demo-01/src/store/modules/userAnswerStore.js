import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

/**
 * 用户答案状态管理
 * 专门用于管理用户输入的答案数据
 */
export const useUserAnswerStore = defineStore('userAnswer', () => {
  // State
  const userAnswers = ref([]); // 用户答案列表
  const loading = ref(false); // 加载状态
  const error = ref(null); // 错误信息
  const lastUpdated = ref(null); // 上次更新时间

  // Getters
  const getUserAnswers = computed(() => userAnswers.value);

  const getUserAnswerByQuestionId = computed(() => {
    return (questionId) => {
      const answer = userAnswers.value.find(item => item.questionId === questionId);
      return answer ? answer.answer : [];
    };
  });

  const getAnsweredQuestionIds = computed(() => {
    return userAnswers.value
      .filter(item => item.answer && item.answer.length > 0)
      .map(item => item.questionId);
  });

  const getAnsweredCount = computed(() => {
    return userAnswers.value.filter(item => item.answer && item.answer.length > 0).length;
  });

  const getUserAnswersMap = computed(() => {
    const map = {};
    userAnswers.value.forEach(item => {
      map[item.questionId] = item.answer;
    });
    return map;
  });

  // Actions
  /**
   * 初始化用户答案
   * @param {Array} questions - 问题列表
   */
  const initializeUserAnswers = (questions) => {
    if (!questions || !Array.isArray(questions)) return;
    
    const initializedAnswers = questions.map(question => ({
      questionId: question.id,
      answer: [], // 初始为空数组
      answeredAt: null,
      updatedAt: new Date().toISOString()
    }));
    
    userAnswers.value = initializedAnswers;
    lastUpdated.value = new Date().toISOString();
  };

  /**
   * 更新单个问题的用户答案
   * @param {number} questionId - 问题ID
   * @param {Array} answer - 答案内容（通常是选项数组）
   */
  const updateUserAnswerByQuestionId = (questionId, answer) => {
    try {
      const existingIndex = userAnswers.value.findIndex(item => item.questionId === questionId);
      const currentTime = new Date().toISOString();
      
      if (existingIndex >= 0) {
        // 更新现有答案
        userAnswers.value[existingIndex] = {
          ...userAnswers.value[existingIndex],
          answer,
          updatedAt: currentTime,
          answeredAt: userAnswers.value[existingIndex].answeredAt || currentTime
        };
      } else {
        // 添加新答案
        userAnswers.value.push({
          questionId,
          answer,
          answeredAt: currentTime,
          updatedAt: currentTime
        });
      }
      
      lastUpdated.value = currentTime;
      error.value = null;
    } catch (err) {
      console.error('更新用户答案失败:', err);
      error.value = err.message;
    }
  };

  /**
   * 批量更新用户答案
   * @param {Object} answerMap - 问题ID到答案的映射对象
   */
  const updateUserAnswers = (answerMap) => {
    if (!answerMap || typeof answerMap !== 'object') return;
    
    const currentTime = new Date().toISOString();
    
    Object.entries(answerMap).forEach(([questionId, answer]) => {
      const numQuestionId = parseInt(questionId);
      if (!isNaN(numQuestionId)) {
        const existingIndex = userAnswers.value.findIndex(item => item.questionId === numQuestionId);
        
        if (existingIndex >= 0) {
          userAnswers.value[existingIndex] = {
            ...userAnswers.value[existingIndex],
            answer,
            updatedAt: currentTime,
            answeredAt: userAnswers.value[existingIndex].answeredAt || currentTime
          };
        } else {
          userAnswers.value.push({
            questionId: numQuestionId,
            answer,
            answeredAt: currentTime,
            updatedAt: currentTime
          });
        }
      }
    });
    
    lastUpdated.value = currentTime;
  };

  /**
   * 清除指定问题的用户答案
   * @param {number} questionId - 问题ID
   */
  const clearUserAnswerByQuestionId = (questionId) => {
    const existingIndex = userAnswers.value.findIndex(item => item.questionId === questionId);
    if (existingIndex >= 0) {
      userAnswers.value[existingIndex] = {
        ...userAnswers.value[existingIndex],
        answer: [],
        answeredAt: null,
        updatedAt: new Date().toISOString()
      };
      lastUpdated.value = new Date().toISOString();
    }
  };

  /**
   * 清除所有用户答案
   */
  const clearAllUserAnswers = () => {
    userAnswers.value = [];
    lastUpdated.value = new Date().toISOString();
  };

  /**
   * 检查问题是否已回答
   * @param {number} questionId - 问题ID
   * @returns {boolean} 是否已回答
   */
  const isQuestionAnswered = (questionId) => {
    const answer = getUserAnswerByQuestionId.value(questionId);
    return answer && answer.length > 0;
  };

  /**
   * 设置加载状态
   * @param {boolean} status - 加载状态
   */
  const setLoading = (status) => {
    loading.value = status;
  };

  /**
   * 设置错误信息
   * @param {string|null} errorMsg - 错误信息
   */
  const setError = (errorMsg) => {
    error.value = errorMsg;
  };

  return {
    // State
    userAnswers,
    loading,
    error,
    lastUpdated,

    // Getters
    getUserAnswers,
    getUserAnswerByQuestionId,
    getAnsweredQuestionIds,
    getAnsweredCount,
    getUserAnswersMap,

    // Actions
    initializeUserAnswers,
    updateUserAnswerByQuestionId,
    updateUserAnswers,
    clearUserAnswerByQuestionId,
    clearAllUserAnswers,
    isQuestionAnswered,
    setLoading,
    setError
  };
}, {
  // 持久化存储
  persist: {
    key: 'userAnswerStore',
    storage: localStorage,
    paths: ['userAnswers', 'lastUpdated']
  }
});