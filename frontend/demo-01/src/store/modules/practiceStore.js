import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const usePracticeStore = defineStore('practice', () => {
  // State
  const practices = ref([
    // Python编程基础课程的练习
    {
      id: '1',
      courseId: '1',
      title: '练习1：Python基础语法',
      description: '练习Python变量、数据类型和基本运算符',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>定义不同类型的变量（整数、浮点数、字符串、布尔值）</li>
          <li>使用算术运算符进行计算</li>
          <li>使用比较运算符和逻辑运算符</li>
          <li>练习字符串的基本操作</li>
          <li>代码需要包含适当的注释</li>
        </ul>`,
      deadline: '2024-12-15 23:59:59',
      status: '未提交',
      score: null,
      totalScore: 100,
      difficulty: 1
    },
    {
      id: '2',
      courseId: '1',
      title: '练习2：条件语句与循环',
      description: '使用if-else语句和循环结构解决实际问题',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>编写一个判断闰年的程序</li>
          <li>使用for循环计算1-100的累加和</li>
          <li>使用while循环实现猜数字游戏</li>
        </ul>`,
      deadline: '2024-12-20 23:59:59',
      status: '已提交',
      score: 85,
      totalScore: 100,
      difficulty: 2
    },
    {
      id: '3',
      courseId: '1',
      title: '练习3：函数与模块',
      description: '学习函数的定义和调用，模块的使用',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>定义一个计算阶乘的函数</li>
          <li>实现斐波那契数列函数</li>
          <li>创建自己的模块并导入使用</li>
        </ul>`,
      deadline: '2024-12-25 23:59:59',
      status: '未开始',
      score: null,
      totalScore: 100,
      difficulty: 2
    },

    // 数据结构与算法课程的练习
    {
      id: '4',
      courseId: '2',
      title: '练习1：数组操作',
      description: '实现数组的常用操作算法',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>实现数组反转算法</li>
          <li>实现数组去重算法</li>
          <li>实现快速排序算法</li>
        </ul>`,
      deadline: '2024-12-18 23:59:59',
      status: '未提交',
      score: null,
      totalScore: 100,
      difficulty: 3
    },
    {
      id: '5',
      courseId: '2',
      title: '练习2：链表实现',
      description: '手动实现链表数据结构及相关操作',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>定义链表节点类</li>
          <li>实现链表的插入、删除、查找操作</li>
          <li>实现链表的反转</li>
        </ul>`,
      deadline: '2024-12-28 23:59:59',
      status: '部分正确',
      score: 60,
      totalScore: 100,
      difficulty: 4
    },

    // Web前端开发课程的练习
    {
      id: '6',
      courseId: '3',
      title: '练习1：HTML页面构建',
      description: '使用HTML5语义化标签构建网页结构',
      requirement: `<p>请创建一个个人主页，包含以下元素：</p>
        <ul>
          <li>使用语义化HTML5标签</li>
          <li>包含导航菜单、文章列表、侧边栏等结构</li>
          <li>添加适当的meta标签和title</li>
        </ul>`,
      deadline: '2024-12-22 23:59:59',
      status: '已提交',
      score: 90,
      totalScore: 100,
      difficulty: 1
    },
    {
      id: '7',
      courseId: '3',
      title: '练习2：CSS样式设计',
      description: '使用CSS3美化网页，实现响应式设计',
      requirement: `<p>请完成以下样式任务：</p>
        <ul>
          <li>使用Flexbox和Grid布局</li>
          <li>实现响应式设计（移动端适配）</li>
          <li>添加过渡和动画效果</li>
        </ul>`,
      deadline: '2024-12-30 23:59:59',
      status: '未提交',
      score: null,
      totalScore: 100,
      difficulty: 2
    },

    // Java企业级开发课程的练习
    {
      id: '8',
      courseId: '4',
      title: '练习1：面向对象编程',
      description: '使用Java实现面向对象编程的核心概念',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>设计并实现一个学生类（Student）</li>
          <li>实现封装、继承和多态</li>
          <li>使用抽象类和接口</li>
        </ul>`,
      deadline: '2024-12-25 23:59:59',
      status: '未开始',
      score: null,
      totalScore: 100,
      difficulty: 3
    },
    {
      id: '9',
      courseId: '4',
      title: '练习2：Spring Boot入门',
      description: '使用Spring Boot框架创建RESTful API',
      requirement: `<p>请完成以下任务：</p>
        <ul>
          <li>创建Spring Boot项目</li>
          <li>实现用户管理的CRUD接口</li>
          <li>使用JPA进行数据库操作</li>
        </ul>`,
      deadline: '2025-01-05 23:59:59',
      status: '未开始',
      score: null,
      totalScore: 100,
      difficulty: 4
    }
  ])

  const currentPractice = ref(null)
  const selectedPractice = ref(null)
  const submissionHistory = ref({
    '2': [
      {
        submitTime: '2024-12-14 15:30:00',
        status: '已提交',
        score: 85
      }
    ],
    '5': [
      {
        submitTime: '2024-12-16 20:15:00',
        status: '部分正确',
        score: 60
      }
    ],
    '6': [
      {
        submitTime: '2024-12-20 10:30:00',
        status: '已提交',
        score: 90
      }
    ]
  })
  const loading = ref(false)

  // Getters
  const getPracticesByCourseId = computed(() => {
    return (courseId) => practices.value.filter(practice => practice.courseId === courseId)
  })

  const practiceCount = computed(() => practices.value.length)

  const getPracticeById = computed(() => {
    return (practiceId) => practices.value.find(practice => practice.id === practiceId)
  })

  const getPracticeStats = computed(() => {
    return (courseId) => {
      const coursePractices = practices.value.filter(p => p.courseId === courseId)
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
// 更新practiceStore中的练习列表
// 直接重置整个练习列表，而不是合并
practiceStore.resetPractices(fetchedPractices);
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
    getPracticesByCourseId,
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