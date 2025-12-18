import { defineStore } from "pinia";
import { ref } from "vue";
import { getCourses } from "@/api/modules/teaching/CourseAPI";
// import { testpdf } from "@/assets/test_pdf.pdf";

// 模拟延迟函数
const delay = (ms = 500) => new Promise((resolve) => setTimeout(resolve, ms));

// 课程数据仓库
export const useTeachingStore = defineStore("teaching", () => {
  // 状态
  const courseSeries = ref([]);
  const courseContent = ref({});
  const loading = ref(false);
  const error = ref(null);

  // 获取所有课程系列
  const fetchCourseSeries = async () => {
    // loading.value = true;
    // error.value = null;
    // try {
    //   // 模拟API延迟
    //   await delay(500);
    //   // 模拟数据
    //   const mockData = [
    //     {
    //       id: 1,
    //       title: "Python 基础入门",
    //       description: "从零开始学习 Python 编程语言的基础知识",
    //       videoCount: 5,
    //       pdfCount: 3,
    //       totalCount: 8,
    //       completedCount: 3,
    //       progress: 38,
    //     },
    //     {
    //       id: 2,
    //       title: "Python 进阶教程",
    //       description: "深入学习 Python 的高级特性和编程技巧",
    //       videoCount: 8,
    //       pdfCount: 5,
    //       totalCount: 13,
    //       completedCount: 0,
    //       progress: 0,
    //     },
    //     {
    //       id: 3,
    //       title: "Python Web 开发",
    //       description: "使用 Python 进行 Web 应用开发的完整指南",
    //       videoCount: 12,
    //       pdfCount: 8,
    //       totalCount: 20,
    //       completedCount: 5,
    //       progress: 25,
    //     },
    //     {
    //       id: 4,
    //       title: "Python 数据分析",
    //       description: "掌握使用 Python 进行数据分析的核心技能",
    //       videoCount: 10,
    //       pdfCount: 6,
    //       totalCount: 16,
    //       completedCount: 0,
    //       progress: 0,
    //     },
    //   ];
    //   courseSeries.value = mockData;
    //   return {
    //     code: 200,
    //     message: "获取成功",
    //     data: mockData,
    //   };
    // } catch (err) {
    //   error.value = err.message;
    //   return {
    //     code: 500,
    //     message: err.message || "获取失败",
    //     data: null,
    //   };
    // } finally {
    //   loading.value = false;
    // }
  };

  // 根据ID获取课程系列
  const getCourseSeriesById = (id) => {
    return courseSeries.value.find((series) => series.id === parseInt(id));
  };

  // 获取课程系列内容
  const fetchCourseContent = async (seriesId) => {
    loading.value = true;
    error.value = null;

    try {
      // 模拟API延迟
      await delay(1000);

      // 如果已经缓存了数据，直接返回
      if (courseContent.value[seriesId]) {
        return {
          code: 200,
          message: "获取成功",
          data: courseContent.value[seriesId],
        };
      }

      // 模拟内容数据
      const mockContentData = {
        1: [
          {
            id: "1-1",
            title: "Python 基础语法视频教程",
            type: "video",
            url: "https://www.w3schools.com/html/mov_bbb.mp4",
            duration: "15:30",
          },
          {
            id: "1-2",
            title: "Python 安装指南文档",
            type: "pdf",
            url: "/src/assets/test_pdf.pdf",
            size: "2.5MB",
          },
          {
            id: "1-3",
            title: "数据类型详解视频",
            type: "video",
            url: "https://www.w3schools.com/html/movie.mp4",
            duration: "20:15",
          },
          {
            id: "1-4",
            title: "Python 学习路线图",
            type: "pdf",
            url: "/src/assets/test_pdf.pdf",
            size: "1.8MB",
          },
          {
            id: "1-5",
            title: "W3C 技术视频",
            type: "video",
            url: "https://www.w3.org/2009/04/video/webmitd1_2mb.webm",
            duration: "25:45",
          },
        ],
        2: [
          {
            id: "2-1",
            title: "Python 面向对象编程",
            type: "video",
            url: "https://www.w3schools.com/html/mov_bbb.mp4",
            duration: "30:20",
          },
          {
            id: "2-2",
            title: "Python 装饰器详解",
            type: "pdf",
            url: "/src/assets/test_pdf.pdf",
            size: "3.2MB",
          },
          {
            id: "2-3",
            title: "Python 生成器与迭代器",
            type: "video",
            url: "https://www.w3schools.com/html/movie.mp4",
            duration: "18:40",
          },
        ],
        3: [
          {
            id: "3-1",
            title: "Flask 框架入门",
            type: "video",
            url: "https://www.w3schools.com/html/mov_bbb.mp4",
            duration: "45:30",
          },
          {
            id: "3-2",
            title: "Django 实战项目",
            type: "pdf",
            url: "/src/assets/test_pdf.pdf",
            size: "5.5MB",
          },
        ],
      };

      let content = mockContentData[seriesId];

      // 如果没有预设内容，生成随机内容
      if (!content) {
        content = [];
        const contentCount = Math.floor(Math.random() * 5) + 3; // 3-7个内容

        for (let i = 1; i <= contentCount; i++) {
          content.push({
            id: `${seriesId}-${i}`,
            title: `课程内容 ${i}`,
            type: Math.random() > 0.5 ? "video" : "pdf",
            url:
              Math.random() > 0.5
                ? "https://www.w3schools.com/html/mov_bbb.mp4"
                : "/src/assets/test_pdf.pdf",
            duration:
              Math.random() > 0.5
                ? `${Math.floor(Math.random() * 60)}:${String(
                    Math.floor(Math.random() * 60)
                  ).padStart(2, "0")}`
                : undefined,
            size:
              Math.random() > 0.5
                ? `${(Math.random() * 10 + 1).toFixed(1)}MB`
                : undefined,
          });
        }
      }

      // 缓存内容
      courseContent.value[seriesId] = content;

      return {
        code: 200,
        message: "获取成功",
        data: content,
      };
    } catch (err) {
      error.value = err.message;
      return {
        code: 500,
        message: err.message || "获取失败",
        data: null,
      };
    } finally {
      loading.value = false;
    }
  };

  // 获取缓存的内容
  const getContentBySeriesId = (seriesId) => {
    return courseContent.value[seriesId] || [];
  };

  // 清除错误
  const clearError = () => {
    error.value = null;
  };

  // 获取课程数据
  const fetchCourses = async (courseId) => {
    loading.value = true;
    error.value = null;
    await delay(500); // 模拟延迟

    try {
      // 调用 getCourses API
      const response = await getCourses(courseId);

      if (response && response.data) {
        return response;
      }

      throw new Error("API返回数据格式错误");
    } catch (err) {
      console.error("获取课程数据失败:", err);
      error.value = err.message;

      // 返回模拟数据
      const mockData = [
        {
          id: 1,
          title: "Python 基础入门",
          description: "从零开始学习 Python 编程语言的基础知识",
        },
        {
          id: 2,
          title: "Python 进阶教程",
          description: "深入学习 Python 的高级特性和编程技巧",
        },
        {
          id: 3,
          title: "Python Web 开发",
          description: "使用 Python 进行 Web 应用开发的完整指南",
        },
        {
          id: 4,
          title: "Python 数据分析",
          description: "掌握使用 Python 进行数据分析的核心技能",
        },
      ];

      return {
        code: 200,
        message: "使用模拟数据",
        data: mockData,
      };
    } finally {
      loading.value = false;
    }
  };

  // 重置状态
  const resetState = () => {
    courseSeries.value = [];
    courseContent.value = {};
    loading.value = false;
    error.value = null;
  };

  return {
    // 状态
    courseSeries,
    courseContent,
    loading,
    error,
    // 方法
    fetchCourseSeries,
    getCourseSeriesById,
    fetchCourseContent,
    getContentBySeriesId,
    fetchCourses,
    clearError,
    resetState,
  };
});
