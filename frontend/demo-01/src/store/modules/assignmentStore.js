import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAssignmentStore = defineStore('assignment', () => {
  // State
  const assignments = ref([
    // Python编程基础课程的作业
    {
      id: '1',
      courseId: '1',
      title: '作业1：Python基础语法',
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
      title: '作业2：条件语句与循环',
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
      title: '作业3：函数与模块',
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

    // 数据结构与算法课程的作业
    {
      id: '4',
      courseId: '2',
      title: '作业1：数组操作',
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
      title: '作业2：链表实现',
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

    // Web前端开发课程的作业
    {
      id: '6',
      courseId: '3',
      title: '作业1：HTML页面构建',
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
      title: '作业2：CSS样式设计',
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

    // Java企业级开发课程的作业
    {
      id: '8',
      courseId: '4',
      title: '作业1：面向对象编程',
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
      title: '作业2：Spring Boot入门',
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

  const currentAssignment = ref(null)
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
  const getAssignmentsByCourseId = computed(() => {
    return (courseId) => assignments.value.filter(assignment => assignment.courseId === courseId)
  })

  const assignmentCount = computed(() => assignments.value.length)

  const getAssignmentById = computed(() => {
    return (assignmentId) => assignments.value.find(assignment => assignment.id === assignmentId)
  })

  const getAssignmentStats = computed(() => {
    return (courseId) => {
      const courseAssignments = assignments.value.filter(a => a.courseId === courseId)
      const completed = courseAssignments.filter(a => a.status === '已提交' || a.status === '部分正确').length
      return {
        total: courseAssignments.length,
        completed,
        pending: courseAssignments.length - completed
      }
    }
  })

  // Actions
  const setCurrentAssignment = (assignment) => {
    currentAssignment.value = assignment
  }

  const addAssignment = (assignment) => {
    const newAssignment = {
      ...assignment,
      id: Date.now().toString(),
      status: '未开始',
      score: null,
      createTime: new Date().toISOString()
    }
    assignments.value.unshift(newAssignment)
    return newAssignment
  }

  const updateAssignment = (assignmentId, updates) => {
    const index = assignments.value.findIndex(a => a.id === assignmentId)
    if (index !== -1) {
      assignments.value[index] = { ...assignments.value[index], ...updates }
      if (currentAssignment.value?.id === assignmentId) {
        currentAssignment.value = assignments.value[index]
      }
      return assignments.value[index]
    }
    return null
  }

  const updateAssignmentStatus = (assignmentId, status, score = null) => {
    const assignment = assignments.value.find(a => a.id === assignmentId)
    if (assignment) {
      assignment.status = status
      if (score !== null) {
        assignment.score = score
      }
      assignment.updateTime = new Date().toISOString()
    }
  }

  const deleteAssignment = (assignmentId) => {
    const index = assignments.value.findIndex(a => a.id === assignmentId)
    if (index !== -1) {
      assignments.value.splice(index, 1)
      if (currentAssignment.value?.id === assignmentId) {
        currentAssignment.value = null
      }
      // 删除相关的提交历史
      delete submissionHistory.value[assignmentId]
      return true
    }
    return false
  }

  const addSubmissionHistory = (assignmentId, submission) => {
    if (!submissionHistory.value[assignmentId]) {
      submissionHistory.value[assignmentId] = []
    }
    submissionHistory.value[assignmentId].unshift(submission)

    // 限制历史记录数量，最多保留10条
    if (submissionHistory.value[assignmentId].length > 10) {
      submissionHistory.value[assignmentId] = submissionHistory.value[assignmentId].slice(0, 10)
    }
  }

  const getSubmissionHistoryById = (assignmentId) => {
    return submissionHistory.value[assignmentId] || []
  }

  const getCodeTemplate = (assignmentId, language) => {
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

  const submitAssignment = async (assignmentId, code, language) => {
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
      addSubmissionHistory(assignmentId, submission)

      // 更新作业状态
      updateAssignmentStatus(assignmentId, '已提交')

      return submission
    } catch (error) {
      console.error('提交作业失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const importAssignments = (assignmentsList) => {
    assignmentsList.forEach(assignment => {
      addAssignment(assignment)
    })
  }

  const clearAll = () => {
    assignments.value = []
    currentAssignment.value = null
    submissionHistory.value = {}
  }

  return {
    // State
    assignments,
    currentAssignment,
    submissionHistory,
    loading,

    // Getters
    getAssignmentsByCourseId,
    assignmentCount,
    getAssignmentById,
    getAssignmentStats,

    // Actions
    setCurrentAssignment,
    addAssignment,
    updateAssignment,
    updateAssignmentStatus,
    deleteAssignment,
    addSubmissionHistory,
    getSubmissionHistory: getSubmissionHistoryById,
    getCodeTemplate,
    submitAssignment,
    importAssignments,
    clearAll
  }
}, {
  persist: {
    key: 'assignment-store',
    storage: localStorage,
    paths: ['assignments', 'submissionHistory']
  }
})