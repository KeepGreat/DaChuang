import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useTaskStore = defineStore('task', () => {
  // State
  const tasks = ref([
    {
      id: '1',
      courseId: '1',
      title: '第一章：Python环境搭建与基础语法',
      description: '完成Python环境搭建，���习变量、数据类型和基本运算符',
      type: 'task', // task: 普通任务, notification: 通知
      priority: 'high', // high, medium, low
      status: 'pending', // pending: 待完成, completed: 已完成, expired: 已过期
      deadline: '2024-12-10 23:59:59',
      publishTime: '2024-12-01 08:00:00',
      publisher: '张老师',
      attachments: [
        {
          name: 'Python安装指南.pdf',
          url: '/files/python-install.pdf',
          size: '2.3MB'
        }
      ],
      tags: ['基础', '必做'],
      completionRate: 0
    },
    {
      id: '2',
      courseId: '1',
      title: '课程通知：Python期中考试安排',
      description: '期中考试将于12月15日举行，考试范围为第1-3章内容',
      type: 'notification',
      priority: 'high',
      status: 'pending',
      deadline: '',
      publishTime: '2024-12-02 10:00:00',
      publisher: '教务处',
      attachments: [],
      tags: ['通知', '考试'],
      completionRate: null
    },
    {
      id: '3',
      courseId: '1',
      title: '第二章练习：条件语句与循环',
      description: '完成教材第二章的所有练习题，并提交实验报告',
      type: 'task',
      priority: 'medium',
      status: 'completed',
      deadline: '2024-12-08 23:59:59',
      publishTime: '2024-12-03 09:00:00',
      publisher: '李老师',
      attachments: [
        {
          name: '第二章练习题.docx',
          url: '/files/chapter2-exercise.docx',
          size: '156KB'
        }
      ],
      tags: ['练习', '实验'],
      completionRate: 100
    },
    {
      id: '4',
      courseId: '2',
      title: '数据结构作业：实现二叉树',
      description: '编写一个完整的二叉树实现，包括插入、删除、遍历等操作',
      type: 'task',
      priority: 'high',
      status: 'pending',
      deadline: '2024-12-12 23:59:59',
      publishTime: '2024-12-01 14:00:00',
      publisher: '王教授',
      attachments: [
        {
          name: '二叉树实现要求.pdf',
          url: '/files/binary-tree-requirements.pdf',
          size: '890KB'
        },
        {
          name: '测试用例.txt',
          url: '/files/test-cases.txt',
          size: '12KB'
        }
      ],
      tags: ['数据结构', '编程'],
      completionRate: 30
    },
    {
      id: '5',
      courseId: '2',
      title: '讲座通知：算法竞赛经验分享',
      description: '邀请ACM金牌获得者分享算法竞赛经验，欢迎参加',
      type: 'notification',
      priority: 'low',
      status: 'pending',
      deadline: '',
      publishTime: '2024-12-02 16:00:00',
      publisher: '计算机学院',
      attachments: [],
      tags: ['讲座', '活动'],
      completionRate: null
    },
    {
      id: '6',
      courseId: '3',
      title: 'Web项目：个人博客网站',
      description: '使用HTML、CSS、JavaScript创建一个个人博客网站',
      type: 'task',
      priority: 'medium',
      status: 'pending',
      deadline: '2024-12-20 23:59:59',
      publishTime: '2024-12-03 11:00:00',
      publisher: '陈老师',
      attachments: [
        {
          name: '项目要求文档.pdf',
          url: '/files/blog-requirements.pdf',
          size: '3.2MB'
        },
        {
          name: '参考模板.zip',
          url: '/files/blog-template.zip',
          size: '1.5MB'
        }
      ],
      tags: ['项目', '前端'],
      completionRate: 0
    }
  ])

  const currentTask = ref(null)
  const loading = ref(false)

  // Getters
  const getTasksByCourseId = computed(() => {
    return (courseId) => tasks.value.filter(task => task.courseId === courseId)
  })

  const taskCount = computed(() => tasks.value.length)

  const getTaskById = computed(() => {
    return (taskId) => tasks.value.find(task => task.id === taskId)
  })

  // 根据状态筛选任务
  const getTasksByStatus = computed(() => {
    return (status) => {
      if (status === 'all') return tasks.value
      return tasks.value.filter(task => task.status === status)
    }
  })

  // 根据类型筛选任务
  const getTasksByType = computed(() => {
    return (type) => {
      if (type === 'all') return tasks.value
      return tasks.value.filter(task => task.type === type)
    }
  })

  // 获取任务统计
  const getTaskStats = computed(() => (courseId) => {
    const courseTasks = tasks.value.filter(task => task.courseId === courseId)
    return {
      total: courseTasks.length,
      pending: courseTasks.filter(t => t.status === 'pending').length,
      completed: courseTasks.filter(t => t.status === 'completed').length,
      expired: courseTasks.filter(t => t.status === 'expired').length
    }
  })

  // Actions
  const setCurrentTask = (task) => {
    currentTask.value = task
  }

  const addTask = (task) => {
    const newTask = {
      ...task,
      id: Date.now().toString(),
      createTime: new Date().toISOString()
    }
    tasks.value.unshift(newTask)
    return newTask
  }

  const updateTask = (taskId, updates) => {
    const index = tasks.value.findIndex(t => t.id === taskId)
    if (index !== -1) {
      tasks.value[index] = { ...tasks.value[index], ...updates }
      if (currentTask.value?.id === taskId) {
        currentTask.value = tasks.value[index]
      }
      return tasks.value[index]
    }
    return null
  }

  const updateTaskStatus = (taskId, status) => {
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.status = status
      if (status === 'completed') {
        task.completionRate = 100
        task.completeTime = new Date().toISOString()
      }
    }
  }

  const updateTaskCompletionRate = (taskId, rate) => {
    const task = tasks.value.find(t => t.id === taskId)
    if (task) {
      task.completionRate = Math.max(0, Math.min(100, rate))
      if (task.completionRate === 100) {
        task.status = 'completed'
        task.completeTime = new Date().toISOString()
      }
    }
  }

  const deleteTask = (taskId) => {
    const index = tasks.value.findIndex(t => t.id === taskId)
    if (index !== -1) {
      tasks.value.splice(index, 1)
      if (currentTask.value?.id === taskId) {
        currentTask.value = null
      }
      return true
    }
    return false
  }

  const searchTasks = (searchTerm, courseId = null) => {
    const term = searchTerm.toLowerCase().trim()
    let filtered = courseId
      ? tasks.value.filter(t => t.courseId === courseId)
      : tasks.value

    if (!term) return filtered

    return filtered.filter(task =>
      task.title.toLowerCase().includes(term) ||
      task.description.toLowerCase().includes(term) ||
      task.publisher.toLowerCase().includes(term) ||
      task.tags.some(tag => tag.toLowerCase().includes(term))
    )
  }

  // 标记任务为已过期
  const checkExpiredTasks = () => {
    const now = new Date()
    tasks.value.forEach(task => {
      if (task.deadline && task.status === 'pending') {
        const deadline = new Date(task.deadline)
        if (deadline < now) {
          task.status = 'expired'
        }
      }
    })
  }

  // 清空所有任务
  const clearAll = () => {
    tasks.value = []
    currentTask.value = null
  }

  return {
    // State
    tasks,
    currentTask,
    loading,

    // Getters
    getTasksByCourseId,
    taskCount,
    getTaskById,
    getTasksByStatus,
    getTasksByType,
    getTaskStats,

    // Actions
    setCurrentTask,
    addTask,
    updateTask,
    updateTaskStatus,
    updateTaskCompletionRate,
    deleteTask,
    searchTasks,
    checkExpiredTasks,
    clearAll
  }
}, {
  persist: {
    key: 'task-store',
    storage: localStorage,
    paths: ['tasks']
  }
})