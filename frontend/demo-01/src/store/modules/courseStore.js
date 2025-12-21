import { getCourseSectionsPage, getAllCourseSections } from "@/api";
import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useUserStore } from "./user";

export const useCourseStore = defineStore("course", () => {
  // State
  // 原始课程数据（永远不会被修改）
  const originalCourses = ref([
    {
      id: 1,
      name: "Python编程基础",
      description: "从零开始学习Python编程语言，掌握基础语法和编程思维",
      image: null,
      status: 1,
      createTime: "2024-01-01",
      updateTime: "2024-01-15",
    },
    {
      id: 2,
      name: "数据结构与算法",
      description: "学习常用的数据结构和算法，提高编程能力和解决问题的效率",
      image: null,
      status: 1,
      createTime: "2024-01-05",
      updateTime: "2024-01-20",
    },
    {
      id: 3,
      name: "Web前端开发",
      description: "掌握HTML、CSS、JavaScript等前端技术，构建现代化的Web应用",
      image: null,
      status: 1,
      createTime: "2024-01-10",
      updateTime: "2024-01-25",
    },
    {
      id: 4,
      name: "Java企业级开发",
      description: "学习Java编程语言及企业级开发框架，成为Java开发工程师",
      image: null,
      status: 1,
      createTime: "2024-01-15",
      updateTime: "2024-02-01",
    },
    {
      id: 5,
      name: "机器学习入门",
      description: "了解机器学习的基本概念和常用算法，入门人工智能领域",
      image: null,
      status: 0,
      createTime: "2024-02-01",
      updateTime: "2024-02-15",
    },
    {
      id: 6,
      name: "数据库设计与优化",
      description: "学习关系型数据库原理，掌握SQL语言和数据库优化技巧",
      image: null,
      status: 1,
      createTime: "2024-02-05",
      updateTime: "2024-02-20",
    },
    {
      id: 7,
      name: "移动应用开发",
      description: "使用React Native或Flutter开发跨平台移动应用",
      image: null,
      status: 0,
      createTime: "2024-02-10",
      updateTime: "2024-02-25",
    },
    {
      id: 8,
      name: "云计算与DevOps",
      description: "学习云计算平台知识和DevOps实践，提升部署运维能力",
      image: null,
      status: 1,
      createTime: "2024-02-15",
      updateTime: "2024-03-01",
    },
    {
      id: 9,
      name: "网络安全基础",
      description: "了解网络安全的基本概念和防护措施，保护系统安全",
      image: null,
      status: 1,
      createTime: "2024-02-20",
      updateTime: "2024-03-05",
    },
    {
      id: 10,
      name: "区块链技术",
      description: "探索区块链原理和应用，了解分布式账本技术",
      image: null,
      status: 0,
      createTime: "2024-03-01",
      updateTime: "2024-03-15",
    },
    {
      id: 11,
      name: "人工智能进阶",
      description: "深入学习深度学习、神经网络等AI核心技术",
      image: null,
      status: 0,
      createTime: "2024-03-05",
      updateTime: "2024-03-20",
    },
    {
      id: 12,
      name: "微服务架构",
      description: "学习微服务设计理念和实现技术，构建可扩展的系统架构",
      image: null,
      status: 1,
      createTime: "2024-03-10",
      updateTime: "2024-03-25",
    },
    {
      id: 13,
      name: "React Native开发",
      description: "使用React Native开发跨平台移动应用",
      image: null,
      status: 1,
      createTime: "2024-03-15",
      updateTime: "2024-03-30",
    },
    {
      id: 14,
      name: "Vue.js进阶教程",
      description: "深入学习Vue.js框架，掌握组件开发和状态管理",
      image: null,
      status: 1,
      createTime: "2024-03-20",
      updateTime: "2024-04-05",
    },
    {
      id: 15,
      name: "Node.js后端开发",
      description: "使用Node.js构建高性能的后端服务",
      image: null,
      status: 1,
      createTime: "2024-03-25",
      updateTime: "2024-04-10",
    },
    {
      id: 16,
      name: "TypeScript完全指南",
      description: "学习TypeScript的类型系统和高级特性",
      image: null,
      status: 1,
      createTime: "2024-04-01",
      updateTime: "2024-04-15",
    },
    {
      id: 17,
      name: "Docker容器技术",
      description: "掌握Docker容器的使用和管理",
      image: null,
      status: 1,
      createTime: "2024-04-05",
      updateTime: "2024-04-20",
    },
    {
      id: 18,
      name: "Kubernetes编排",
      description: "学习Kubernetes容器编排技术",
      image: null,
      status: 1,
      createTime: "2024-04-10",
      updateTime: "2024-04-25",
    },
    {
      id: 19,
      name: "GraphQL API设计",
      description: "设计和实现GraphQL API接口",
      image: null,
      status: 1,
      createTime: "2024-04-15",
      updateTime: "2024-04-30",
    },
    {
      id: 20,
      name: "Redis缓存技术",
      description: "学习Redis缓存的使用和优化",
      image: null,
      status: 1,
      createTime: "2024-04-20",
      updateTime: "2024-05-05",
    },
    {
      id: 21,
      name: "MongoDB数据库",
      description: "掌握NoSQL数据库MongoDB的使用",
      image: null,
      status: 1,
      createTime: "2024-04-25",
      updateTime: "2024-05-10",
    },
    {
      id: 22,
      name: "Vue3组合式API",
      description: "深入学习Vue3的组合式API",
      image: null,
      status: 1,
      createTime: "2024-05-01",
      updateTime: "2024-05-15",
    },
    {
      id: 23,
      name: "Spring Boot框架",
      description: "使用Spring Boot快速构建Java应用",
      image: null,
      status: 1,
      createTime: "2024-05-05",
      updateTime: "2024-05-20",
    },
    {
      id: 24,
      name: "PostgreSQL数据库",
      description: "学习PostgreSQL关系型数据库",
      image: null,
      status: 1,
      createTime: "2024-05-10",
      updateTime: "2024-05-25",
    },
    {
      id: 25,
      name: "Nginx服务器配置",
      description: "掌握Nginx服务器的配置和优化",
      image: null,
      status: 1,
      createTime: "2024-05-15",
      updateTime: "2024-05-30",
    },
  ]);

  // 显示的课程数据（用于搜索过滤）
  const courses = ref([...originalCourses.value]);

  const currentCourse = ref(null);
  const pagination = ref({
    current: 1,
    pageSize: 24, // 每页显示24条，接近25条的测试数据
    total: 0,
  });
  const loading = ref(false);
  const searchName = ref("");
  const isSearching = ref(false);

  // 模拟延迟
  const delay = (ms = 500) => new Promise((resolve) => setTimeout(resolve, ms));

  // Actions
  // 获取课程数据（分页）
  const fetchCoursePage = async (page = 1, pageSize = 12) => {
    console.log("11111", originalCourses.value);
    loading.value = true;
    await delay(500);
    try {
      // 获取用户store实例
      const userStore = useUserStore();

      // 获取当前登录的学生ID，如果没有则使用默认值
      const studentId = userStore.userId || "student001"; // 测试用的默认学生ID

      // 调用获取所有课程的接口
      const response = await getAllCourseSections(studentId);

      if (response.data) {
        // 将API返回的数据保存到originalCourses
        originalCourses.value = response.data;
      }

      // 计算当前页的数据
      const startIndex = (page - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      courses.value = originalCourses.value.slice(startIndex, endIndex);

      // 更新分页信息
      pagination.value.total = originalCourses.value.length;
      pagination.value.current = page;
      pagination.value.pageSize = pageSize;

      return response.data;
    } catch (error) {
      console.error("获取课程列表失败，使用测试数据:", error);
      // API调用失败时，使用本地测试数据
      const startIndex = (page - 1) * pageSize;
      const endIndex = startIndex + pageSize;
      courses.value = originalCourses.value.slice(startIndex, endIndex);

      pagination.value.total = originalCourses.value.length;
      pagination.value.current = page;
      pagination.value.pageSize = pageSize;

      // 返回测试数据格式
      return {
        code: 200,
        message: "使用测试数据",
        data: originalCourses.value,
      };
    } finally {
      loading.value = false;
      isSearching.value = false;
    }
  };

  const searchCourses = async (name) => {
    loading.value = true;

    try {
      if (!name || name.trim() === "") {
        // 如果搜索框为空，显示所有课程
        courses.value = [...originalCourses.value];
        console.log(courses.value);
        pagination.value.total = originalCourses.value.length;
        pagination.value.current = 1;
        isSearching.value = false;
        searchName.value = "";
        return;
      }

      const searchTerm = name.toLowerCase().trim();
      isSearching.value = true;

      // 搜索逻辑
      courses.value = originalCourses.value.filter(
        (course) =>
          course.name.toLowerCase().includes(searchTerm) ||
          course.description.toLowerCase().includes(searchTerm)
      );

      pagination.value.total = courses.value.length;
      pagination.value.current = 1;
    } catch (error) {
      console.error("搜索课程失败:", error);
      // 搜索失败时显示所有课程
      courses.value = [...originalCourses.value];
      pagination.value.total = originalCourses.value.length;
    } finally {
      loading.value = false;
    }
  };

  const setCurrentCourse = (course) => {
    currentCourse.value = course;
  };

  const updateCourse = (courseId, updates) => {
    const index = originalCourses.value.findIndex(
      (course) => course.id === courseId
    );
    if (index !== -1) {
      originalCourses.value[index] = {
        ...originalCourses.value[index],
        ...updates,
        updateTime: new Date().toISOString(),
      };
      // 更新显示的课程列表
      courses.value = [...originalCourses.value];
      if (currentCourse.value?.id === courseId) {
        currentCourse.value = originalCourses.value[index];
      }
      return originalCourses.value[index];
    }
    return null;
  };

  const deleteCourse = (courseId) => {
    const index = originalCourses.value.findIndex(
      (course) => course.id === courseId
    );
    if (index !== -1) {
      originalCourses.value.splice(index, 1);
      courses.value = [...originalCourses.value];
      if (currentCourse.value?.id === courseId) {
        currentCourse.value = null;
      }
      return true;
    }
    return false;
  };

  const resetSearch = () => {
    searchName.value = "";
    isSearching.value = false;
    courses.value = [...originalCourses.value];
    pagination.value.total = originalCourses.value.length;
  };

  const clearCourses = () => {
    originalCourses.value = [];
    courses.value = [];
    currentCourse.value = null;
    pagination.value = {
      current: 1,
      pageSize: 24,
      total: 0,
    };
  };

  return {
    // State
    courses,
    originalCourses,
    currentCourse,
    pagination,
    loading,
    searchName,
    isSearching,

    // Actions
    fetchCoursePage,
    searchCourses,
    setCurrentCourse,
    updateCourse,
    deleteCourse,
    resetSearch,
    clearCourses,
  };
});
