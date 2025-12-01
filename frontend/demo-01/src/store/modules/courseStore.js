import { defineStore } from 'pinia'
import { getCourseSections, getCourseSectionsPage } from '@/api'

export const useCourseStore = defineStore('course', {
  state: () => ({
    // 课程列表
    courses: [
      {
        id: '1',
        name: 'Python编程基础',
        description: '从零开始学习Python编程语言，掌握基础语法和编程思维',
        image: null,
        status: 1,
        createTime: '2024-01-01',
        updateTime: '2024-01-15'
      },
      {
        id: '2',
        name: '数据结构与算法',
        description: '学习常用的数据结构和算法，提高编程能力和解决问题的效率',
        image: null,
        status: 1,
        createTime: '2024-01-05',
        updateTime: '2024-01-20'
      },
      {
        id: '3',
        name: 'Web前端开发',
        description: '掌握HTML、CSS、JavaScript等前端技术，构建现代化的Web应用',
        image: null,
        status: 1,
        createTime: '2024-01-10',
        updateTime: '2024-01-25'
      },
      {
        id: '4',
        name: 'Java企业级开发',
        description: '学习Java编程语言及企业级开发框架，成为Java开发工程师',
        image: null,
        status: 1,
        createTime: '2024-01-15',
        updateTime: '2024-02-01'
      },
      {
        id: '5',
        name: '机器学习入门',
        description: '了解机器学习的基本概念和常用算法，入门人工智能领域',
        image: null,
        status: 0,
        createTime: '2024-02-01',
        updateTime: '2024-02-15'
      },
      {
        id: '6',
        name: '数据库设计与优化',
        description: '学习关系型数据库原理，掌握SQL语言和数据库优化技巧',
        image: null,
        status: 1,
        createTime: '2024-02-05',
        updateTime: '2024-02-20'
      },
      {
        id: '7',
        name: '移动应用开发',
        description: '使用React Native或Flutter开发跨平台移动应用',
        image: null,
        status: 0,
        createTime: '2024-02-10',
        updateTime: '2024-02-25'
      },
      {
        id: '8',
        name: '云计算与DevOps',
        description: '学习云计算平台知识和DevOps实践，提升部署运维能力',
        image: null,
        status: 1,
        createTime: '2024-02-15',
        updateTime: '2024-03-01'
      },
      {
        id: '9',
        name: '网络安全基础',
        description: '了解网络安全的基本概念和防护措施，保护系统安全',
        image: null,
        status: 1,
        createTime: '2024-02-20',
        updateTime: '2024-03-05'
      },
      {
        id: '10',
        name: '区块链技术',
        description: '探索区块链原理和应用，了解分布式账本技术',
        image: null,
        status: 0,
        createTime: '2024-03-01',
        updateTime: '2024-03-15'
      },
      {
        id: '11',
        name: '人工智能进阶',
        description: '深入学习深度学习、神经网络等AI核心技术',
        image: null,
        status: 0,
        createTime: '2024-03-05',
        updateTime: '2024-03-20'
      },
      {
        id: '12',
        name: '微服务架构',
        description: '学习微服务设计理念和实现技术，构建可扩展的系统架构',
        image: null,
        status: 1,
        createTime: '2024-03-10',
        updateTime: '2024-03-25'
      }
    ],
    // 当前选中的课程
    currentCourse: null,
    // 分页信息
    pagination: {
      current: 1,
      pageSize: 12,
      total: 0
    },
    // 加载状态
    loading: false,
    // 搜索相关
    searchName: '',
    isSearching: false
  }),

  getters: {
    // 获取课程总数
    courseCount: (state) => state.courses.length,

    // 根据ID获取课程
    getCourseById: (state) => {
      return (courseId) => state.courses.find(course => course.id === courseId)
    },

    // 获取已发布的课程
    publishedCourses: (state) => {
      return state.courses.filter(course => course.status === 1)
    },

    // 获取草稿状态的课程
    draftCourses: (state) => {
      return state.courses.filter(course => course.status === 0)
    }
  },

  actions: {
    // 获取分页课程列表
    async fetchCoursePage(page = 1, pageSize = 12) {
      this.loading = true
      try {
        const response = await getCourseSectionsPage(page, pageSize, null, null)
        this.courses = response.data.records || this.courses
        this.pagination.total = response.data.total || this.courses.length
        this.pagination.current = page
        this.pagination.pageSize = pageSize
        return response.data
      } catch (error) {
        console.error('获取课程列表失败:', error)
        // 使用本地数据
        this.pagination.total = this.courses.length
        throw error
      } finally {
        this.loading = false
        this.isSearching = false
      }
    },

    // 搜索课程
    async searchCourses(name) {
      this.loading = true
      this.isSearching = true

      try {
        if (!name) {
          // 如果搜索框为空，恢复分页列表
          await this.fetchCoursePage(this.pagination.current, this.pagination.pageSize)
          return
        }

        const response = await getCourseSections(null, name)
        this.courses = Array.isArray(response.data) ? response.data : this.courses
        this.searchName = name
        this.pagination.total = this.courses.length
        return response.data
      } catch (error) {
        console.error('搜索课程失败:', error)
        // 本地搜索
        this.courses = this.courses.filter(course =>
          course.name.toLowerCase().includes(name.toLowerCase()) ||
          course.description.toLowerCase().includes(name.toLowerCase())
        )
        this.pagination.total = this.courses.length
        throw error
      } finally {
        this.loading = false
      }
    },

    // 设置当前课程
    setCurrentCourse(course) {
      this.currentCourse = course
    },

    // 添加课程
    addCourse(course) {
      const newCourse = {
        ...course,
        id: Date.now().toString(),
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString()
      }
      this.courses.unshift(newCourse)
      this.pagination.total++
      return newCourse
    },

    // 更新课程
    updateCourse(courseId, updates) {
      const index = this.courses.findIndex(course => course.id === courseId)
      if (index !== -1) {
        this.courses[index] = {
          ...this.courses[index],
          ...updates,
          updateTime: new Date().toISOString()
        }
        if (this.currentCourse?.id === courseId) {
          this.currentCourse = this.courses[index]
        }
        return this.courses[index]
      }
      return null
    },

    // 删除课程
    deleteCourse(courseId) {
      const index = this.courses.findIndex(course => course.id === courseId)
      if (index !== -1) {
        this.courses.splice(index, 1)
        this.pagination.total--
        if (this.currentCourse?.id === courseId) {
          this.currentCourse = null
        }
        return true
      }
      return false
    },

    // 重置搜索状态
    resetSearch() {
      this.searchName = ''
      this.isSearching = false
    },

    // 清空课程列表
    clearCourses() {
      this.courses = []
      this.currentCourse = null
      this.pagination = {
        current: 1,
        pageSize: 12,
        total: 0
      }
    }
  },

  persist: {
    key: 'course-store',
    storage: localStorage,
    paths: ['courses', 'currentCourse']
  }
})