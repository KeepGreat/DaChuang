import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useQuestionsStore = defineStore(
  "questions",
  () => {
    // State
    const questions = ref([]);

    const questionTypes = ref([
      { type: 0, name: "判断题" },
      { type: 1, name: "选择题" },
      { type: 2, name: "简答题" },
      { type: 3, name: "编程题" },
    ]);

    // 已回答的问题数量
    const answeredCount = computed(() => {
      return questions.value.filter((q) => q.status !== null).length;
    });

    // 总问题数量
    const totalQuestions = computed(() => {
      return questions.value.length;
    });

    // 侧边栏题型统计数据
    const sidebarQuestionTypes = computed(() => {
      return generateSidebarQuestionTypes();
    });

    // 生成侧边栏题型统计数据的函数，支持传入用户答案数据
    const generateSidebarQuestionTypes = (userAnswersMap = null) => {
      // 初始化空数组，不再包含"全部题型"
      const typesWithStats = [];

      // 为每种题型生成统计数据
      questionTypes.value.forEach((type) => {
        if (type.type !== -1) {
          // 排除"全部题型"
          const typeQuestions = questions.value.filter((q) => q.type === type.type);
          let typeAnswered = 0;

          // 计算已回答的题目数量
          typeQuestions.forEach((question) => {
            const isAnswered = isQuestionAnswered(question.id, userAnswersMap);
            if (isAnswered) {
              typeAnswered++;
            }
          });

          typesWithStats.push({
            id: type.type,
            name: type.name,
            total: typeQuestions.length,
            answered: typeAnswered,
          });
        }
      });

      return typesWithStats;
    };

    // 判断题目是否已回答的辅助函数
    const isQuestionAnswered = (questionId) => {
      // 只检查 questionsStore 中的 status 字段（已提交到后端的答案），不再检查userAnswersMap，因为那只是本地填写的答案，未必已提交
      const question = questions.value.find((q) => q.id === questionId);
      if (question && question.status !== null) {
        return true;
      }

      return false;
    };

    // 辅助函数：将API返回的Question转换为store所需的结构
    const transformQuestion = (apiQuestion) => {
      // 根据问题类型设置默认选项
      let defaultOptions = [];
      if (apiQuestion.type === 0) {
        // 判断题
        defaultOptions = [
          { label: "A", value: "true", text: "正确" },
          { label: "B", value: "false", text: "错误" },
        ];
      } else if (apiQuestion.type === 1) {
        // 选择题
        // 设置ABCD四种默认选项
        defaultOptions = [
          { label: "A", value: "A", text: "选项A" },
          { label: "B", value: "B", text: "选项B" },
          { label: "C", value: "C", text: "选项C" },
          { label: "D", value: "D", text: "选项D" },
        ];
      }

      // 转换为store所需的结构
      return {
        id: apiQuestion.id,
        name: apiQuestion.name,
        type: apiQuestion.type,
        content: apiQuestion.content,
        hasResource: apiQuestion.hasResource,
        options: defaultOptions, // 选择题/判断题的选项
        answer: [], // 正确答案，实际应用中应从API获取
        status: null, // 答题状态，初始为null
      };
    };

    // Actions
    const updateQuestionStatus = (questionId, status) => {
      const question = questions.value.find((q) => q.id === questionId);
      if (question) {
        question.status = status;
      }
    };

    const setQuestions = (newQuestions) => {
      // 如果传入的问题不是store所需的结构，则进行转换
      const transformedQuestions = newQuestions.map(transformQuestion);
      questions.value = transformedQuestions;
    };

    // 导出transformQuestion供外部使用
    const transformApiQuestions = (apiQuestions) => {
      return apiQuestions.map(transformQuestion);
    };

    return {
      questions,
      questionTypes,
      sidebarQuestionTypes,
      answeredCount,
      totalQuestions,
      updateQuestionStatus,
      setQuestions,
      transformApiQuestions,
      generateSidebarQuestionTypes,
      isQuestionAnswered,
    };
  },
  {
    persist: {
      key: "questions-store",
      storage: localStorage,
      paths: ["questions"],
    },
  }
);
