import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useExamStore = defineStore('exam', () => {
  // State
  const exams = ref([
    {
      id: '1',
      courseId: '1',
      title: 'Python基础语法测试',
      description: '测试Python基础语法知识，包括变量、数据类型、运算符等',
      type: 'quiz', // quiz: 测验, exam: 考试
      status: 'published', // draft: 草稿, published: 已发布, ended: 已结束
      startTime: '2024-12-10 14:00:00',
      endTime: '2024-12-10 16:00:00',
      duration: 120, // 分钟
      totalScore: 100,
      passScore: 60,
      attempts: 3, // 允许尝试次数
      timeLimit: true,
      randomOrder: true,
      showAnswers: true, // 结束后显示答案
      publisher: '张老师',
      publishTime: '2024-12-01 09:00:00',
      questions: [
        {
          id: '1-1',
          type: 'single', // single: 单选, multiple: 多选, essay: 简答, fill: 填空
          question: 'Python中哪个关键字用于定义函数？',
          options: ['function', 'def', 'define', 'func'],
          correctAnswer: ['def'],
          score: 10
        },
        {
          id: '1-2',
          type: 'multiple',
          question: '以下哪些是Python的基本数据类型？',
          options: ['int', 'str', 'list', 'class'],
          correctAnswer: ['int', 'str', 'list'],
          score: 15
        },
        {
          id: '1-3',
          type: 'essay',
          question: '请简述Python中列表（List）和元组（Tuple）的区别。',
          correctAnswer: '列表是可变的，元组是不可变的',
          score: 25
        }
      ]
    },
    {
      id: '2',
      courseId: '1',
      title: 'Python期中考试',
      description: '涵盖Python基础语法、流程控制、函数等内容',
      type: 'exam',
      status: 'published',
      startTime: '2024-12-15 09:00:00',
      endTime: '2024-12-15 11:30:00',
      duration: 150,
      totalScore: 100,
      passScore: 70,
      attempts: 1,
      timeLimit: true,
      randomOrder: true,
      showAnswers: true,
      publisher: '张老师',
      publishTime: '2024-12-01 10:00:00',
      questions: []
    },
    {
      id: '3',
      courseId: '1',
      title: 'Python期末考试',
      description: '全面测试Python课程所学内容',
      type: 'exam',
      status: 'draft',
      startTime: '2025-01-15 09:00:00',
      endTime: '2025-01-15 12:00:00',
      duration: 180,
      totalScore: 100,
      passScore: 60,
      attempts: 1,
      timeLimit: true,
      randomOrder: false,
      showAnswers: false,
      publisher: '张老师',
      publishTime: '2024-12-05 14:00:00',
      questions: []
    },
    {
      id: '4',
      courseId: '2',
      title: '数据结构期中测试',
      description: '测试数据结构基础知识',
      type: 'quiz',
      status: 'published',
      startTime: '2024-12-12 19:00:00',
      endTime: '2024-12-12 21:00:00',
      duration: 120,
      totalScore: 100,
      passScore: 75,
      attempts: 2,
      timeLimit: true,
      randomOrder: true,
      showAnswers: true,
      publisher: '王教授',
      publishTime: '2024-12-02 09:00:00',
      questions: []
    },
    {
      id: '5',
      courseId: '2',
      title: '算法在线测试',
      description: '测试常见算法的理解和应用',
      type: 'quiz',
      status: 'ended',
      startTime: '2024-11-20 14:00:00',
      endTime: '2024-11-20 15:30:00',
      duration: 90,
      totalScore: 100,
      passScore: 80,
      attempts: 3,
      timeLimit: true,
      randomOrder: true,
      showAnswers: true,
      publisher: '李教授',
      publishTime: '2024-11-15 10:00:00',
      questions: []
    }
  ])

  const currentExam = ref(null)
  const examResults = ref({
    '1': [
      {
        id: 'result-1-1',
        userId: 'student1',
        userName: '小明',
        examId: '1',
        score: 85,
        passStatus: 'passed', // passed: 通过, failed: 未通过
        submitTime: '2024-12-10 15:45:00',
        duration: 95, // 实际用时（分钟）
        answers: {
          '1-1': ['def'],
          '1-2': ['int', 'str'],
          '1-3': '列表是可变的，元组是不可变的'
        }
      }
    ]
  })
  const loading = ref(false)

  // Getters
  const getExamsByCourseId = computed(() => {
    return (courseId) => {
      const list = exams.value.filter(exam => exam.courseId === courseId)
      return list.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
    }
  })

  const examCount = computed(() => exams.value.length)

  const getExamById = computed(() => {
    return (examId) => exams.value.find(exam => exam.id === examId)
  })

  // 根据状态筛选考试
  const getExamsByStatus = computed(() => {
    return (status) => {
      if (status === 'all') return exams.value
      return exams.value.filter(exam => exam.status === status)
    }
  })

  // 获取考试统计（学生视角）
  const getExamStats = computed(() => (courseId) => {
    const courseExams = exams.value.filter(e => e.courseId === courseId)
    const now = new Date()

    // 计算待参加和已完成的考试
    const pending = courseExams.filter(exam => {
      const startTime = new Date(exam.startTime)
      const endTime = new Date(exam.endTime)
      const examResults = getUserExamResults.value(exam.id, 'student1')
      return exam.status === 'published' &&
             now >= startTime &&
             now <= endTime &&
             examResults.length < exam.attempts
    }).length

    const completed = courseExams.filter(exam => {
      const examResults = getUserExamResults.value(exam.id, 'student1')
      return examResults.length > 0
    }).length

    return {
      total: courseExams.length,
      published: courseExams.filter(e => e.status === 'published').length,
      ended: courseExams.filter(e => e.status === 'ended').length,
      pending: pending,
      completed: completed
    }
  })

  // 获取用户考试结果
  const getUserExamResults = computed(() => {
    return (examId, userId = 'student1') => {
      return examResults.value[examId]?.filter(r => r.userId === userId) || []
    }
  })

  // 判断考试是否可用
  const isExamAvailable = computed(() => {
    return (examId, userId = 'student1') => {
      const exam = getExamById.value(examId)
      if (!exam || exam.status !== 'published') return false

      const now = new Date()
      const startTime = new Date(exam.startTime)
      const endTime = new Date(exam.endTime)

      if (now < startTime) return false // 未开始
      if (now > endTime) return false // 已结束

      const userResults = getUserExamResults.value(examId, userId)
      if (userResults.length >= exam.attempts) return false // 超过次数

      return true
    }
  })

  // Actions
  const setCurrentExam = (exam) => {
    currentExam.value = exam
  }

  const createExam = (exam) => {
    const newExam = {
      ...exam,
      id: Date.now().toString(),
      status: 'draft',
      publishTime: new Date().toISOString(),
      questions: exam.questions || []
    }
    exams.value.unshift(newExam)
    return newExam
  }

  const updateExam = (examId, updates) => {
    const index = exams.value.findIndex(e => e.id === examId)
    if (index !== -1) {
      exams.value[index] = { ...exams.value[index], ...updates }
      if (currentExam.value?.id === examId) {
        currentExam.value = exams.value[index]
      }
      return exams.value[index]
    }
    return null
  }

  const deleteExam = (examId) => {
    const index = exams.value.findIndex(e => e.id === examId)
    if (index !== -1) {
      exams.value.splice(index, 1)
      delete examResults.value[examId]
      if (currentExam.value?.id === examId) {
        currentExam.value = null
      }
      return true
    }
    return false
  }

  // 发布考试
  const publishExam = (examId) => {
    return updateExam(examId, { status: 'published' })
  }

  // 结束考试
  const endExam = (examId) => {
    return updateExam(examId, { status: 'ended' })
  }

  // 提交考试答案
  const submitExam = (examId, answers, userId = 'student1') => {
    const exam = getExamById.value(examId)
    if (!exam) return null

    let score = 0
    const correctAnswers = {}

    // 计算得分
    exam.questions.forEach(question => {
      correctAnswers[question.id] = question.correctAnswer

      if (question.type === 'single' || question.type === 'multiple') {
        const userAnswer = answers[question.id] || []
        const isCorrect = JSON.stringify(userAnswer.sort()) ===
                         JSON.stringify(question.correctAnswer.sort())
        if (isCorrect) score += question.score
      } else if (question.type === 'essay') {
        // 简答题需要手动批改，这里给部分分数
        score += question.score * 0.5
      } else if (question.type === 'fill') {
        // 填空题简单匹配
        if (answers[question.id] === question.correctAnswer[0]) {
          score += question.score
        }
      }
    })

    const result = {
      id: `result-${examId}-${Date.now()}`,
      userId: userId,
      userName: '学生', // 应该从用户信息获取
      examId: examId,
      score: Math.round(score),
      passStatus: score >= exam.passScore ? 'passed' : 'failed',
      submitTime: new Date().toISOString(),
      duration: null, // 需要记录实际用时
      answers: answers
    }

    if (!examResults.value[examId]) {
      examResults.value[examId] = []
    }
    examResults.value[examId].push(result)

    return result
  }

  // 搜索考试
  const searchExams = (searchTerm, courseId = null) => {
    const term = searchTerm.toLowerCase().trim()
    let filtered = courseId
      ? exams.value.filter(e => e.courseId === courseId)
      : exams.value

    if (!term) return filtered

    return filtered.filter(exam =>
      exam.title.toLowerCase().includes(term) ||
      exam.description.toLowerCase().includes(term) ||
      exam.publisher.toLowerCase().includes(term)
    )
  }

  // 获取考试排行榜
  const getExamRanking = computed(() => {
    return (examId) => {
      const results = examResults.value[examId] || []
      return results
        .sort((a, b) => b.score - a.score)
        .map((r, index) => ({
          ...r,
          rank: index + 1
        }))
    }
  })

  // 批改阅卷
  const batchGrade = (examId) => {
    const exam = getExamById.value(examId)
    if (!exam) return

    const results = examResults.value[examId] || []
    results.forEach(result => {
      // 这里应该调用批改接口，模拟自动批改
      const essayQuestions = exam.questions.filter(q => q.type === 'essay')
      if (essayQuestions.length > 0 && result.answers) {
        // 简单模拟批改逻辑
        let additionalScore = 0
        essayQuestions.forEach(question => {
          const userAnswer = result.answers[question.id]
          if (userAnswer && userAnswer.length > 20) {
            additionalScore += question.score * 0.3
          }
        })
        result.score = Math.min(result.score + additionalScore, exam.totalScore)
        result.passStatus = result.score >= exam.passScore ? 'passed' : 'failed'
        result.isGraded = true
        result.gradeTime = new Date().toISOString()
      }
    })
  }

  // 清空所有考试
  const clearAll = () => {
    exams.value = []
    currentExam.value = null
    examResults.value = {}
  }

  return {
    // State
    exams,
    currentExam,
    examResults,
    loading,

    // Getters
    getExamsByCourseId,
    examCount,
    getExamById,
    getExamsByStatus,
    getExamStats,
    getUserExamResults,
    isExamAvailable,
    getExamRanking,

    // Actions
    setCurrentExam,
    createExam,
    updateExam,
    deleteExam,
    publishExam,
    endExam,
    submitExam,
    searchExams,
    batchGrade,
    clearAll
  }
}, {
  persist: {
    key: 'exam-store',
    storage: localStorage,
    paths: ['exams', 'examResults']
  }
})