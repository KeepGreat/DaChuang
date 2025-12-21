import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 标准答案状态管理
 * 专门用于管理系统标准答案数据
 */
export const useAnswerStore = defineStore('answer', () => {
  // State
  const answers = ref([]) // 存储标准答案数据
  const loading = ref(false) // 加载状态
  const error = ref(null) // 错误信息

  // Getters
  const getAnswers = computed(() => answers.value)

  const getAnswerById = computed(() => {
    return (answerId) => answers.value.find(answer => answer.id === answerId)
  })

  const getAnswersByQuestionId = computed(() => {
    return (questionId) => answers.value.filter(answer => answer.questionId === questionId)
  })

  const getAnswersByQuestionIds = computed(() => {
    return (questionIds) => {
      if (!Array.isArray(questionIds)) return []
      return answers.value.filter(answer => questionIds.includes(answer.questionId))
    }
  })

  const getStandardAnswersMap = computed(() => {
    // 获取问题ID到标准答案的映射
    const map = {}
    answers.value.forEach(answer => {
      map[answer.questionId] = answer.content
    })
    return map
  })

  // Actions
  /**
   * 设置标准答案数据
   * @param {Array} newAnswers - 新的标准答案列表
   */
  const setAnswers = (newAnswers) => {
    answers.value = newAnswers
    loading.value = false
    error.value = null
  }

  /**
   * 清空标准答案数据
   */
  const clearAnswers = () => {
    answers.value = []
  }

  /**
   * 设置加载状态
   * @param {boolean} isLoading - 是否加载中
   */
  const setLoading = (isLoading) => {
    loading.value = isLoading
  }

  /**
   * 设置错误信息
   * @param {string|null} err - 错误信息
   */
  const setError = (err) => {
    error.value = err
  }

  return {
    // State
    answers,
    loading,
    error,

    // Getters
    getAnswers,
    getAnswerById,
    getAnswersByQuestionId,
    getAnswersByQuestionIds,
    getStandardAnswersMap,

    // Actions
    setAnswers,
    clearAnswers,
    setLoading,
    setError
  }
})
