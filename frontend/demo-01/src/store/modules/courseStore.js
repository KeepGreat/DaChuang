import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { getCourseSectionsPage } from "@/api";

export const useCourseStore = defineStore(
  "course",
  () => {
    // State
    // 原始课程数据（永远不会被修改）
    const originalCourses = ref([
      {
        id: "1",
        name: "Python编程基础",
        description: "从零开始学习Python编程语言，掌握基础语法和编程思维",
        image: null,
        status: 1,
        createTime: "2024-01-01",
        updateTime: "2024-01-15",
      },
      {
        id: "2",
        name: "数据结构与算法",
        description: "学习常用的数据结构和算法，提高编程能力和解决问题的效率",
        image: null,
        status: 1,
        createTime: "2024-01-05",
        updateTime: "2024-01-20",
      },
      {
        id: "3",
        name: "Web前端开发",
        description: "掌握HTML、CSS、JavaScript等前端技术，构建现代化的Web应用",
        image: null,
        status: 1,
        createTime: "2024-01-10",
        updateTime: "2024-01-25",
      },
      {
        id: "4",
        name: "Java企业级开发",
        description: "学习Java编程语言及企业级开发框架，成为Java开发工程师",
        image: null,
        status: 1,
        createTime: "2024-01-15",
        updateTime: "2024-02-01",
      },
      {
        id: "5",
        name: "机器学习入门",
        description: "了解机器学习的基本概念和常用算法，入门人工智能领域",
        image: null,
        status: 0,
        createTime: "2024-02-01",
        updateTime: "2024-02-15",
      },
      {
        id: "6",
        name: "数据库设计与优化",
        description: "学习关系型数据库原理，掌握SQL语言和数据库优化技巧",
        image: null,
        status: 1,
        createTime: "2024-02-05",
        updateTime: "2024-02-20",
      },
      {
        id: "7",
        name: "移动应用开发",
        description: "使用React Native或Flutter开发跨平台移动应用",
        image: null,
        status: 0,
        createTime: "2024-02-10",
        updateTime: "2024-02-25",
      },
      {
        id: "8",
        name: "云计算与DevOps",
        description: "学习云计算平台知识和DevOps实践，提升部署运维能力",
        image: null,
        status: 1,
        createTime: "2024-02-15",
        updateTime: "2024-03-01",
      },
      {
        id: "9",
        name: "网络安全基础",
        description: "了解网络安全的基本概念和防护措施，保护系统安全",
        image: null,
        status: 1,
        createTime: "2024-02-20",
        updateTime: "2024-03-05",
      },
      {
        id: "10",
        name: "区块链技术",
        description: "探索区块链原理和应用，了解分布式账本技术",
        image: null,
        status: 0,
        createTime: "2024-03-01",
        updateTime: "2024-03-15",
      },
      {
        id: "11",
        name: "人工智能进阶",
        description: "深入学习深度学习、神经网络等AI核心技术",
        image: null,
        status: 0,
        createTime: "2024-03-05",
        updateTime: "2024-03-20",
      },
      {
        id: "12",
        name: "微服务架构",
        description: "学习微服务设计理念和实现技术，构建可扩展的系统架构",
        image: null,
        status: 1,
        createTime: "2024-03-10",
        updateTime: "2024-03-25",
      },
    ]);

    // 显示的课程数据（用于搜索过滤）
    const courses = ref([...originalCourses.value]);

    const currentCourse = ref(null);
    const pagination = ref({
      current: 1,
      pageSize: 12,
      total: 0,
    });
    const loading = ref(false);
    const searchName = ref("");
    const isSearching = ref(false);

    // Getters
    const courseCount = computed(() => courses.value.length);

    const getCourseById = computed(() => {
      return (courseId) =>
        courses.value.find((course) => course.id === courseId);
    });

    const publishedCourses = computed(() => {
      return courses.value.filter((course) => course.status === 1);
    });

    const draftCourses = computed(() => {
      return courses.value.filter((course) => course.status === 0);
    });

    // Actions
    const fetchCoursePage = async (page = 1, pageSize = 12) => {
      loading.value = true;
      try {
        const response = await getCourseSectionsPage(
          page,
          pageSize,
          null,
          null
        );
        if (response.data?.records) {
          originalCourses.value = response.data.records;
          courses.value = [...response.data.records];
        }
        pagination.value.total =
          response.data?.total || originalCourses.value.length;
        pagination.value.current = page;
        pagination.value.pageSize = pageSize;
        return response.data;
      } catch (error) {
        console.error("获取课程列表失败:", error);
        // 使用本地数据
        pagination.value.total = originalCourses.value.length;
        throw error;
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

        isSearching.value = true;
        searchName.value = name.trim();

        // 前端本地搜索
        const searchTerm = name.toLowerCase().trim();
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

    const addCourse = (course) => {
      const newCourse = {
        ...course,
        id: Date.now().toString(),
        createTime: new Date().toISOString(),
        updateTime: new Date().toISOString(),
      };
      originalCourses.value.unshift(newCourse);
      courses.value = [...originalCourses.value];
      pagination.value.total++;
      return newCourse;
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
        pagination.value.total--;
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
        pageSize: 12,
        total: 0,
      };
    };

    return {
      // State
      originalCourses,
      courses,
      currentCourse,
      pagination,
      loading,
      searchName,
      isSearching,

      // Getters
      courseCount,
      getCourseById,
      publishedCourses,
      draftCourses,

      // Actions
      fetchCoursePage,
      searchCourses,
      setCurrentCourse,
      addCourse,
      updateCourse,
      deleteCourse,
      resetSearch,
      clearCourses,
    };
  },
  {
    persist: {
      key: "course-store",
      storage: localStorage,
      paths: ["originalCourses", "currentCourse"],
    },
  }
);
