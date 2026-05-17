import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const usePracticeStore = defineStore('practice', () => {
  // State
  const practices = ref([
  ])

  const currentPractice = ref(null)
  const selectedPractice = ref(null)
  const submissionHistory = ref({
  })
  const loading = ref(false)

  // Getters
  const getPracticesByCourseSectionId = computed(() => {
    return (courseSectionId) => practices.value.filter(practice => practice.courseSectionId === courseSectionId)
  })

  const practiceCount = computed(() => practices.value.length)

  const getPracticeById = computed(() => {
    return (practiceId) => practices.value.find(practice => practice.id === practiceId)
  })

  const getPracticeStats = computed(() => {
    return (courseSectionId) => {
      const coursePractices = practices.value.filter(p => p.courseSectionId === courseSectionId)
      const completed = coursePractices.filter(p => p.status === '已提交' || p.status === '部分正确').length
      return {
        total: coursePractices.length,
        completed,
        pending: coursePractices.length - completed
      }
    }
  })

  // Actions
  const setCurrentPractice = (practice) => {
    currentPractice.value = practice
  }

  const setSelectedPractice = (index) => {
    selectedPractice.value = index
  }

  const clearSelectedPractice = () => {
    selectedPractice.value = null
  }

  const addPractice = (practice) => {
    const newPractice = {
      ...practice,
      id: Date.now().toString(),
      status: '未开始',
      score: null,
      createTime: new Date().toISOString()
    }
    practices.value.unshift(newPractice)
    return newPractice
  }

  const updatePractice = (practiceId, updates) => {
    const index = practices.value.findIndex(p => p.id === practiceId)
    if (index !== -1) {
      practices.value[index] = { ...practices.value[index], ...updates }
      if (currentPractice.value?.id === practiceId) {
        currentPractice.value = practices.value[index]
      }
      return practices.value[index]
    }
    return null
  }

  const updatePracticeStatus = (practiceId, status, score = null) => {
    const practice = practices.value.find(p => p.id === practiceId)
    if (practice) {
      practice.status = status
      if (score !== null) {
        practice.score = score
      }
      practice.updateTime = new Date().toISOString()
    }
  }

  const deletePractice = (practiceId) => {
    const index = practices.value.findIndex(p => p.id === practiceId)
    if (index !== -1) {
      practices.value.splice(index, 1)
      if (currentPractice.value?.id === practiceId) {
        currentPractice.value = null
      }
      // 删除相关的提交历史
      delete submissionHistory.value[practiceId]
      return true
    }
    return false
  }

  const addSubmissionHistory = (practiceId, submission) => {
    if (!submissionHistory.value[practiceId]) {
      submissionHistory.value[practiceId] = []
    }
    submissionHistory.value[practiceId].unshift(submission)

    // 限制历史记录数量，最多保留10条
    if (submissionHistory.value[practiceId].length > 10) {
      submissionHistory.value[practiceId] = submissionHistory.value[practiceId].slice(0, 10)
    }
  }

  const getSubmissionHistoryById = (practiceId) => {
    return submissionHistory.value[practiceId] || []
  }

  const getCodeTemplate = (practiceId, language) => {
    const templates = {
      cpp: `#include <iostream>
using namespace std;

int main() {
    // 在此编写您的代码
    cout << "Hello, World!" << endl;
    return 0;
}`,
      python: `# 在此编写您的代码
def main():
    print("Hello, World!")

if __name__ == "__main__":
    main()`,
      java: `import java.util.*;

public class Main {
    public static void main(String[] args) {
        // 在此编写您的代码
        System.out.println("Hello, World!");
    }
}`
    }
    return templates[language] || templates.python
  }

  const submitPractice = async (practiceId, code, language) => {
    loading.value = true
    try {
      // 模拟提交过程
      await new Promise(resolve => setTimeout(resolve, 1500))

      // 创建提交记录
      const submission = {
        submitTime: new Date().toLocaleString(),
        status: '评判中',
        score: null,
        code: code,
        language: language
      }

      // 添加提交历史
      addSubmissionHistory(practiceId, submission)

      // 更新练习状态
      updatePracticeStatus(practiceId, '已提交')

      return submission
    } catch (error) {
      console.error('提交练习失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const importPractices = (practicesList) => {
    practicesList.forEach(practice => {
      addPractice(practice)
    })
  }

  const clearAll = () => {
    practices.value = []
    currentPractice.value = null
    selectedPractice.value = null
    submissionHistory.value = {}
  }

  const resetPractices = (newPractices) => {
    practices.value = newPractices
  }

  return {
    // State
    practices,
    currentPractice,
    selectedPractice,
    submissionHistory,
    loading,

    // Getters
    getPracticesByCourseSectionId,
    practiceCount,
    getPracticeById,
    getPracticeStats,

    // Actions
    setCurrentPractice,
    setSelectedPractice,
    clearSelectedPractice,
    addPractice,
    updatePractice,
    updatePracticeStatus,
    deletePractice,
    addSubmissionHistory,
    getSubmissionHistory: getSubmissionHistoryById,
    getCodeTemplate,
    submitPractice,
    importPractices,
    clearAll,
    resetPractices
  }
}, {
  persist: {
    key: 'practice-store',
    storage: localStorage,
    paths: ['practices', 'submissionHistory']
  }
})