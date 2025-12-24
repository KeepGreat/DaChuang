<template>
  <div class="practice-view">
    <!-- 导航栏组件 -->
    <PracticeNavbar
      :practice-title="practiceTitle"
      :deadline="deadline"
      :user-info="userInfo"
      v-model:single-question-mode="singleQuestionMode"
      @time-up="handleTimeUp"
    />

    <div class="practice-content">
      <!-- 侧边栏组件 -->
      <div style="background: #f5f7fa">
        <PracticeSiderbar
          :question-types="computedSidebarQuestionTypes"
          :active-type-id="activeType"
          @type-change="handleTypeChange"
        />

        <!-- 进度信息区域 -->
        <div class="progress-info-sidebar">
          <div class="progress-bar">
            <el-progress
              :percentage="progressPercentage"
              :stroke-width="8"
              :color="progressColor"
              :show-text="false"
            />
          </div>
          <div class="progress-stats">
            <span>已完成：{{ answeredCount }}/{{ totalQuestions }}</span>
            <span>完成率：{{ completionRate }}%</span>
          </div>
        </div>
      </div>

      <div class="main-content">
        <!-- 题目展示容器 -->
        <div class="question-container">
          <QuestionDisplay
            :question="currentQuestion"
            :question-number="currentQuestionIndex + 1"
            :show-correctness="showCorrectness"
            :single-question-mode="singleQuestionMode"
            :same-type-questions="filteredQuestions"
            @set-show-correctness="toggleShowCorrectness"
            @answer-submitted="handleAnswerSubmitted"
            @previous="handlePreviousQuestion"
            @next="handleNextQuestion"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
	createUserAnswer,
	getAnswersByQuestionIds,
	getQuestionResources,
	getUserAnswers,
	getUserId,
	judgeAnswersAuto,
	updateUserAnswerById,
} from "@/api";
import PracticeNavbar from "@/components/practice/PracticeNavbar.vue";
import PracticeSiderbar from "@/components/practice/PracticeSiderbar.vue";
import QuestionDisplay from "@/components/practice/QuestionDisplay.vue";
import {
	useAnswerStore,
	usePracticeStore,
	useQuestionResourceStore,
	useQuestionsStore,
	useUserAnswerStore,
	useUserStore,
} from "@/store";
import { ElMessage, ElMessageBox } from "element-plus";
import { computed, onMounted, onUnmounted, ref, watch } from "vue";
import { onBeforeRouteLeave, useRoute } from "vue-router";

// 使用store
const questionsStore = useQuestionsStore();
const answerStore = useAnswerStore(); // 管理标准答案
const userAnswerStore = useUserAnswerStore(); // 管理用户答案
const practiceStore = usePracticeStore(); // 管理练习数据
const questionResourceStore = useQuestionResourceStore(); // 管理问题资源
const userStore = useUserStore(); // 管理用户状态
const route = useRoute();

// -------------------
// 基础数据定义
// -------------------

// 从token获取用户ID
const fetchUserIdFromToken = async () => {
  try {
    if (!userStore.token) {
      console.error("用户未登录，无法获取token");
      return null;
    }

    const response = await getUserId({ token: userStore.token });

    if (response && response.code === 200 && response.data) {
      const fetchedUserId = response.data;
      console.log("成功获取用户ID:", fetchedUserId);
      // 存储用户ID
      userId.value = fetchedUserId;
      // 更新用户信息
      userInfo.value.id = fetchedUserId;
      return fetchedUserId;
    } else {
      console.error("获取用户ID失败:", response?.message || "未知错误");
      return null;
    }
  } catch (error) {
    console.error("获取用户ID异常:", error);
    return null;
  }
};

// 路由参数获取当前练习ID
const currentPracticeId = computed(() => {
  return route.params.practiceId || null;
});

// 练习基本信息 - 从practiceStore中根据practiceId动态获取
const practiceTitle = computed(() => {
  // 如果有practiceId，根据ID查找对应的练习
  if (currentPracticeId.value) {
    const practice = practiceStore.getPracticeById(currentPracticeId.value);
    if (practice) {
      return practice.title;
    }
  }
  console.warn(`未找到ID为${currentPracticeId.value}的练习，使用第一个练习作为标题`);

  // 如果没有找到对应的练习，使用第一个练习
  if (practiceStore.practices && practiceStore.practices.length > 0) {
    return practiceStore.practices[0].title;
  }

  // 如果没有练习数据，返回默认标题
  return "JavaScript基础练习";
});

// 用户ID，从token获取
const userId = ref(null);
const userInfo = ref({ name: "张三", avatar: "" });

// 单题作答模式，默认为false
const singleQuestionMode = ref(false);

// 倒计时相关 - 从 practiceStore 根据practiceId动态获取 deadline
const deadline = computed(() => {
  // 如果有practiceId，根据ID查找对应的练习
  if (currentPracticeId.value) {
    const practice = practiceStore.getPracticeById(currentPracticeId.value);
    if (practice && practice.deadline) {
      // 将 deadline 字符串转换为 Date 对象
      return new Date(practice.deadline);
    }
  }

  // 如果没有找到对应的练习，使用第一个练习作为fallback
  if (practiceStore.practices && practiceStore.practices.length > 0) {
    const assignmentDeadline = practiceStore.practices[0].deadline;
    // 将 deadline 字符串转换为 Date 对象
    return assignmentDeadline ? new Date(assignmentDeadline) : null;
  }

  // 如果没有练习数据，返回null表示长期有效
  return null;
});

// 当前激活的题型，初始值设为第一个具体题型（判断题）
const activeType = ref(0);

// 从userAnswerStore获取用户答案（计算属性）
const userAnswers = computed(() => {
  return userAnswerStore.getUserAnswersMap;
});

// 当前问题索引
const currentQuestionIndex = ref(0);

// 是否显示正确答案
const showCorrectness = ref(false);

// -------------------
// 计算属性
// -------------------

// 根据题型过滤问题
const filteredQuestions = computed(() => {
  return questionsStore.questions.filter((q) => q.type === activeType.value);
});

// 当前显示的问题
const currentQuestion = computed(() => {
  if (filteredQuestions.value.length === 0) {
    return null;
  }
  return filteredQuestions.value[currentQuestionIndex.value];
});

// 已回答的问题数量（与题型导航逻辑保持一致）
const answeredCount = computed(() => {
  return questionsStore.questions.filter((q) =>
    questionsStore.isQuestionAnswered(q.id, userAnswers.value)
  ).length;
});

// 正确的问题数量
const correctCount = computed(() => {
  return questionsStore.questions.filter((q) => q.status === "correct").length;
});

// 总问题数量
const totalQuestions = computed(() => {
  return questionsStore.totalQuestions;
});

// 当前过滤后的问题数量
const filteredTotalQuestions = computed(() => {
  return filteredQuestions.value.length;
});

// 进度百分比
const progressPercentage = computed(() => {
  if (totalQuestions.value === 0) return 0;
  return Math.round((answeredCount.value / totalQuestions.value) * 100);
});

// 完成率（与进度百分比相同）
const completionRate = computed(() => {
  return progressPercentage.value;
});

// 进度条颜色 - 使用侧边栏相同的渐变色
const progressColor = computed(() => {
  return "linear-gradient(45deg, #2563eb, #1d4ed8)"; // 与PracticeSiderbar.vue第147行相同的渐变色
});

// 侧边栏题型统计数据（包含用户答案信息）
const computedSidebarQuestionTypes = computed(() => {
  return questionsStore.generateSidebarQuestionTypes(userAnswers.value);
});

// -------------------
// 方法定义
// -------------------

// 初始化用户答案
const initUserAnswers = () => {
  // 使用userAnswerStore的初始化方法
  userAnswerStore.initializeUserAnswers(questionsStore.questions);
};

// 从API获取用户答案数据
const fetchUserAnswersFromApi = async () => {
  try {
    // 设置加载状态
    userAnswerStore.setLoading(true);

    // 从questionsStore获取所有问题ID
    const questionIds = questionsStore.questions.map((q) => q.id);
    if (questionIds.length === 0) {
      userAnswerStore.setLoading(false);
      return;
    }

    // 使用存储的用户ID
    if (!userId.value) {
      console.error("用户ID未初始化，无法获取用户答案");
      userAnswerStore.setLoading(false);
      return;
    }

    // 调用API获取用户答案数据
    const response = await getUserAnswers({ userId: userId.value });

    if (response && response.code === 200 && response.data) {
      // 处理API返回的用户答案数据
      const apiUserAnswers = response.data;

      // 转换API数据格式为store所需格式
      const userAnswerMap = {};
      const answeredQuestionIds = []; // 记录已回答的问题ID

      apiUserAnswers.forEach((answer) => {
        let parsedAnswer;
        try {
          if (answer.content && typeof answer.content === "string") {
            // 根据题目类型决定答案格式
            // questionType: 0-判断题, 1-选择题, 2-简答题, 3-编程题
            if (answer.questionType === 0 || answer.questionType === 1) {
              // 判断题和选择题：转换为数组
              parsedAnswer = answer.content.split(",").filter((item) => item.trim());
            } else if (answer.questionType === 2 || answer.questionType === 3) {
              // 简答题和编程题：保持字符串格式
              parsedAnswer = answer.content;
            } else {
              console.error("未知类型", answer.questionType);
              // 未知类型，默认为空数组
              parsedAnswer = [];
            }
          } else {
            parsedAnswer = [];
          }
        } catch (e) {
          console.error(`解析问题 ${answer.questionId} 的答案失败:`, e);
          parsedAnswer = [];
        }

        userAnswerMap[answer.questionId] = parsedAnswer;

        // 如果答案不为空，记录为已回答
        if (
          parsedAnswer &&
          (Array.isArray(parsedAnswer)
            ? parsedAnswer.length > 0
            : parsedAnswer.toString().trim() !== "")
        ) {
          answeredQuestionIds.push(answer.questionId);
        }
      });

      // 使用userAnswerStore的updateUserAnswers方法批量更新用户答案
      userAnswerStore.updateUserAnswers(userAnswerMap);

      // 将从API获取的已回答题目同步到questionsStore的status字段
      answeredQuestionIds.forEach((questionId) => {
        const question = questionsStore.questions.find((q) => q.id === questionId);
        if (question && question.status === null) {
          question.status = "answered"; // 设置为已回答状态
        }
      });

      console.log("成功从API获取用户答案数据:", apiUserAnswers.length, "条");
      console.log("已同步", answeredQuestionIds.length, "个已回答题目到questionsStore");
    } else {
      const errorMsg = response?.message || "未知错误";
      console.error("获取用户答案数据失败:", errorMsg);
      userAnswerStore.setError(errorMsg);
      userAnswerStore.setLoading(false); // 确保在错误情况下也设置加载状态为false
    }
  } catch (error) {
    console.error("获取用户答案数据异常:", error);
    userAnswerStore.setError(error.message);
    userAnswerStore.setLoading(false); // 确保在异常情况下也设置加载状态为false
    // 出错时不影响页面使用，使用本地默认值
  }
};

// 从API获取标准答案数据
const fetchStandardAnswersFromApi = async () => {
  try {
    // 设置加载状态
    answerStore.setLoading(true);

    // 从questionsStore获取所有问题ID
    const questionIds = questionsStore.questions.map((q) => q.id);
    if (questionIds.length === 0) {
      answerStore.setLoading(false);
      return;
    }

    // 调用API获取答案数据
    const response = await getAnswersByQuestionIds(questionIds);

    if (response && response.code === 200 && response.data) {
      // 处理API返回的标准答案数据
      const apiAnswers = response.data;

      // 使用answerStore的setAnswers方法设置标准答案
      answerStore.setAnswers(apiAnswers);

      console.log("成功从API获取标准答案数据:", apiAnswers.length, "条");
    } else {
      const errorMsg = response?.message || "未知错误";
      console.error("获取标准答案数据失败:", errorMsg);
      answerStore.setError(errorMsg);
      answerStore.setLoading(false); // 确保在错误情况下也设置加载状态为false
    }
  } catch (error) {
    console.error("获取标准答案数据异常:", error);
    answerStore.setError(error.message);
    answerStore.setLoading(false); // 确保在异常情况下也设置加载状态为false
    // 出错时不影响页面使用，使用本地默认值
  }
};

// 从API获取问题资源数据
const fetchQuestionResourcesFromApi = async () => {
  try {
    // 获取所有hasResource为true的问题ID
    const questionsWithResources = questionsStore.questions.filter(
      (q) => q.hasResource === true
    );

    if (questionsWithResources.length === 0) {
      console.log("没有需要获取资源的问题");
      return;
    }

    console.log(`开始获取 ${questionsWithResources.length} 个问题的资源`);

    // 为每个问题获取资源
    for (const question of questionsWithResources) {
      try {
        const response = await getQuestionResources({ questionId: question.id });

        if (response && response.code === 200 && response.data) {
          // 将获取到的资源添加到store中
          questionResourceStore.addResources(response.data);
          console.log(`成功获取问题 ${question.id} 的资源: ${response.data.length} 条`);
        } else {
          console.error(
            `获取问题 ${question.id} 的资源失败:`,
            response?.message || "未知错误"
          );
        }
      } catch (error) {
        console.error(`获取问题 ${question.id} 的资源异常:`, error);
      }
    }

    console.log(
      "问题资源获取完成，总计资源数量:",
      questionResourceStore.getAllResources.length
    );
  } catch (error) {
    console.error("获取问题资源数据异常:", error);
  }
};

// 更新用户答案
const updateUserAnswer = (questionId, answer) => {
  // 确保只更新用户答案，不影响标准答案
  console.log(`更新问题 ${questionId} 的用户答案:`, answer);
  userAnswerStore.updateUserAnswerByQuestionId(questionId, answer);

  // 验证标准答案没有被意外修改
  const standardAnswer = answerStore.getAnswersByQuestionId(questionId);
  if (standardAnswer) {
    console.log(`问题 ${questionId} 的标准答案保持不变:`, standardAnswer);
  }
};

// 监听用户答案变化，重置对应题目的状态
watch(
  () => userAnswerStore.getUserAnswersMap,
  (newUserAnswersMap, oldUserAnswersMap) => {
    // 检查哪些题目的答案发生了变化
    for (const questionId in newUserAnswersMap) {
      const newAnswer = newUserAnswersMap[questionId];
      const oldAnswer = oldUserAnswersMap[questionId];

      // 只有当答案确实发生了变化时才重置状态
      if (JSON.stringify(newAnswer) !== JSON.stringify(oldAnswer)) {
        const question = questionsStore.questions.find((q) => q.id === questionId);
        if (question && question.status === "answered") {
          // 用户修改了已提交的答案，重置状态为null表示未提交
          question.status = null;
        }
      }
    }
  },
  { deep: true }
);
// 提交用户答案到后端
const submitUserAnswerToBackend = async (questionId, answer) => {
  try {
    // 获取问题信息
    const question = questionsStore.questions.find((q) => q.id === questionId);
    if (!question) {
      console.error(`未找到问题ID为 ${questionId} 的问题`);
      return;
    }

    // 使用存储的用户ID
    if (!userId.value) {
      console.error("用户ID未初始化，无法提交答案");
      return;
    }

    // 准备答案数据
    const answerData = {
      content: Array.isArray(answer) ? answer.join(",") : String(answer),
      userId: userId.value,
      questionId: questionId,
      questionType: question.type,
    };

    // 检查是否已存在该问题的答案
    const existingAnswers = await getUserAnswers({
      questionId: questionId,
      userId: answerData.userId,
    });

    if (existingAnswers && existingAnswers.data && existingAnswers.data.length > 0) {
      // 更新现有答案
      const existingAnswerId = existingAnswers.data[0].id;
      await updateUserAnswerById({
        id: existingAnswerId,
        content: answerData.content,
      });
      console.log(`成功更新问题 ${questionId} 的答案到后端`);
    } else {
      // 创建新答案
      await createUserAnswer(answerData);
      console.log(`成功提交问题 ${questionId} 的答案到后端`);
    }
  } catch (error) {
    console.error(`提交问题 ${questionId} 的答案到后端失败:`, error);
  }
};

// 切换显示正确答案
const toggleShowCorrectness = async () => {
  showCorrectness.value = !showCorrectness.value;

  // 当显示答案时，调用判题接口进行判题
  if (showCorrectness.value) {
    await judgeAllAnswers();
  }
};

// 判题方法：调用judgeAnswersAuto接口对所有已回答的题目进行判题
const judgeAllAnswers = async () => {
  try {
    // 获取所有已回答的题目（判断题和选择题）
    const answeredQuestions = questionsStore.questions.filter((q) => {
      // 只处理已回答的判断题(0)和选择题(1)
      return (q.type === 0 || q.type === 1) && userAnswerStore.isQuestionAnswered(q.id);
    });

    if (answeredQuestions.length === 0) {
      console.log("没有需要判题的已回答题目");
      return;
    }

    // 准备判题数据
    const judgeData = answeredQuestions.map((question) => {
      const userAnswer = userAnswerStore.getUserAnswerByQuestionId(question.id);
      return {
        questionId: question.id,
        questionType: question.type,
        content: Array.isArray(userAnswer) ? userAnswer.join(",") : String(userAnswer),
        userId: userId.value,
      };
    });

    console.log("开始判题，题目数量:", judgeData.length);

    // 调用判题接口
    const res = await judgeAnswersAuto(judgeData);
    const judgedAnswers = res.data; // 用变量获取res.data

    // 更新题目状态
    judgedAnswers.forEach((judgedAnswer) => {
      const question = questionsStore.questions.find(
        (q) => q.id === judgedAnswer.questionId
      );
      if (question) {
        // 根据分数判断正确性
        question.status = judgedAnswer.score > 0 ? "correct" : "incorrect";
        console.log(
          `题目 ${judgedAnswer.questionId} 判题完成，状态: ${question.status}, 分数: ${judgedAnswer.score}`
        );
      }
    });
    console.log("判题完成，已更新题目状态");
  } catch (error) {
    console.error("判题过程异常:", error);
  }
};

// 处理时间结束
const handleTimeUp = () => {
  showCorrectness.value = true; // 时间结束时显示正确答案
};

// 处理题型切换
const handleTypeChange = (typeId) => {
  activeType.value = typeId;
  currentQuestionIndex.value = 0; // 切换题型时，重置到第一个问题
};

// 处理答案提交
const handleAnswerSubmitted = async (result) => {
  const question = questionsStore.questions.find((q) => q.id === result.questionId);
  if (question) {
    // 保存用户答案到userAnswerStore
    updateUserAnswer(result.questionId, result.answer);

    // 只有在答案不为空时才提交到后端，并标记为已回答
    if (!result.isEmpty) {
      await submitUserAnswerToBackend(result.questionId, result.answer);
      // 提交成功后才标记为已回答
      question.status = "answered";
    } else {
      // 答案为空时，保持状态为null（未回答）
      question.status = null;
    }
  } else {
    console.error("未找到对应问题:", result.questionId);
  }
};

// 处理上一题
const handlePreviousQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    // 不是当前题型的第一题，直接切换到上一题
    currentQuestionIndex.value--;
  } else {
    // 当前是当前题型的第一题，切换到上一类题型的最后一题
    const sidebarTypes = questionsStore.sidebarQuestionTypes;
    console.log("sidebarTypes:", sidebarTypes);
    if (!sidebarTypes || !Array.isArray(sidebarTypes) || sidebarTypes.length === 0) {
      console.warn("题型数据未准备好，无法切换题型");
      return;
    }

    const currentTypeIndex = sidebarTypes.findIndex(
      (type) => type.id === activeType.value
    );
    if (currentTypeIndex > 0) {
      const prevType = sidebarTypes[currentTypeIndex - 1];
      activeType.value = prevType.id;
      // 设置为上一类题型的最后一题
      const questions = questionsStore.questions?.value || [];
      if (!Array.isArray(questions)) {
        console.warn("题目数据格式错误，无法切换到上一题型");
        return;
      }
      const prevTypeQuestions =
        prevType.id === "all"
          ? questions
          : questions.filter((q) => q.type === prevType.id);
      currentQuestionIndex.value =
        prevTypeQuestions.length > 0 ? prevTypeQuestions.length - 1 : 0;
    }
  }
};

// 处理下一题
const handleNextQuestion = () => {
  if (currentQuestionIndex.value < filteredQuestions.value.length - 1) {
    // 不是当前题型的最后一题，直接切换到下一题
    currentQuestionIndex.value++;
  } else {
    // 当前是当前题型的最后一题，切换到下一类题型的第一题
    const sidebarTypes = questionsStore.sidebarQuestionTypes;
    console.log("sidebarTypes:", sidebarTypes);
    if (!sidebarTypes || !Array.isArray(sidebarTypes) || sidebarTypes.length === 0) {
      console.warn("题型数据未准备好，无法切换题型");
      return;
    }

    const currentTypeIndex = sidebarTypes.findIndex(
      (type) => type.id === activeType.value
    );
    if (currentTypeIndex < sidebarTypes.length - 1) {
      const nextType = sidebarTypes[currentTypeIndex + 1];
      activeType.value = nextType.id;
      currentQuestionIndex.value = 0; // 设置为下一类题型的第一题
    }
  }
};

// -------------------
// 生命周期钩子
// -------------------

// 验证数据存储是否正确分离
const validateDataSeparation = () => {
  console.log("\n=== 数据存储分离验证 ===");
  console.log("用户答案存储位置: userAnswerStore");
  console.log("用户答案数量:", userAnswerStore.getUserAnswers.length);
  console.log("标准答案存储位置: answerStore");
  console.log("标准答案数量:", answerStore.getAnswers.length);
  console.log("问题资源存储位置: questionResourceStore");
  console.log("问题资源数量:", questionResourceStore.getAllResources.length);
  console.log("数据存储分离验证完成\n");
};

// 组件挂载时初始化
onMounted(async () => {
  // 首先确保用户已登录并获取用户ID
  const fetchedUserId = await fetchUserIdFromToken();
  if (!fetchedUserId) {
    console.error("用户未登录或无法获取用户ID，部分功能可能受限");
  }

  initUserAnswers(); // 初始化用户答案
  await fetchUserAnswersFromApi(); // 从API获取用户答案数据
  await fetchStandardAnswersFromApi(); // 从API获取标准答案数据
  await fetchQuestionResourcesFromApi(); // 从API获取问题资源数据

  // 添加延迟确保数据加载完成后进行验证
  setTimeout(() => {
    validateDataSeparation();
  }, 100);
});

// 检查是否有未提交的答案
const hasUnsubmittedAnswers = computed(() => {
  const userAnswersMap = userAnswers.value;

  // 检查是否有用户填写但未提交的答案
  let unsubmittedCount = 0;
  const unsubmittedDetails = [];
  const allDetails = [];

  // 遍历所有题目，检查用户是否有填写但未提交的答案
  questionsStore.questions.forEach((question) => {
    const userAnswer = userAnswersMap[question.id];

    // 记录每个题目的详细信息
    const detail = {
      questionId: question.id,
      userAnswer: userAnswer,
      userAnswerLength: userAnswer ? userAnswer.length : 0,
      questionStatus: question.status,
      isArray: Array.isArray(userAnswer),
      isEmpty:
        !userAnswer || (Array.isArray(userAnswer) ? userAnswer.length === 0 : false),
      isUnsubmitted: false,
    };

    // 如果用户有答案但题目状态为null（未提交），则认为是未提交的答案
    if (userAnswer && userAnswer.length > 0 && question.status === null) {
      unsubmittedCount++;
      detail.isUnsubmitted = true;
      unsubmittedDetails.push({
        questionId: question.id,
        userAnswer: userAnswer,
        questionStatus: question.status,
        reason: "用户有答案但状态为null",
      });
    }

    allDetails.push(detail);
  });

  console.log(`未提交答案数量: ${unsubmittedCount}`);
  console.log(`未提交答案详情:`, unsubmittedDetails);

  return unsubmittedCount > 0;
});

// 提交所有未提交的答案
const submitAllUnsubmittedAnswers = async () => {
  try {
    const userAnswersMap = userAnswers.value;
    let submitCount = 0;

    for (const question of questionsStore.questions) {
      const userAnswer = userAnswersMap[question.id];

      // 如果用户有答案但题目状态为null（未提交），则提交
      if (userAnswer && userAnswer.length > 0 && question.status === null) {
        await submitUserAnswerToBackend(question.id, userAnswer);
        // 提交成功后标记为已回答
        question.status = "answered";
        submitCount++;
      }
    }

    if (submitCount > 0) {
      ElMessage.success(`已自动提交 ${submitCount} 个未提交的答案`);
    }

    return submitCount;
  } catch (error) {
    console.error("自动提交未提交答案失败:", error);
    ElMessage.error("自动提交答案失败，请手动保存");
    return 0;
  }
};

// 标记是否正在处理路由导航
let isNavigating = ref(false);

// 路由离开守卫
onBeforeRouteLeave(async (to, from, next) => {
  console.log("准备离开练习页面，检查未提交答案...");

  // 标记正在处理路由导航
  isNavigating.value = true;

  // 如果没有未提交的答案，直接允许离开
  if (!hasUnsubmittedAnswers.value) {
    console.log("没有未提交的答案，允许离开");
    isNavigating.value = false; // 重置导航状态
    next();
    return;
  }

  // 有未提交答案，显示确认对话框
  try {
    await ElMessageBox.confirm(
      "您有未提交的答案，离开页面将自动保存您的答案。是否确认离开？",
      "离开确认",
      {
        confirmButtonText: "确认离开",
        cancelButtonText: "取消",
        type: "warning",
        beforeClose: async (action, instance, done) => {
          if (action === "confirm") {
            // 用户确认离开，先提交未提交的答案
            instance.confirmButtonLoading = true;
            instance.confirmButtonText = "提交中...";

            try {
              await submitAllUnsubmittedAnswers();
              done();
            } catch (error) {
              // 如果提交失败，仍然允许用户离开，但提示用户
              ElMessage.warning("部分答案提交失败，建议您稍后检查并重新提交");
              done();
            } finally {
              instance.confirmButtonLoading = false;
            }
          } else {
            done();
          }
        },
      }
    );

    // 用户确认离开
    console.log("用户确认离开练习页面");
    next();
  } catch (error) {
    // 用户取消离开（点击取消或ESC键）
    console.log("用户取消离开练习页面");
    // 阻止路由跳转
    next(false);
  } finally {
    // 无论结果如何，都要重置导航状态
    isNavigating.value = false;
  }
});

// 监听浏览器刷新/关闭事件
const handleBeforeUnload = (event) => {
  // 如果正在处理路由导航，则不显示原生弹窗
  if (isNavigating.value) {
    return;
  }

  if (hasUnsubmittedAnswers.value) {
    event.preventDefault();
    event.returnValue = "您有未提交的答案，确定要离开吗？";
    return event.returnValue;
  }
};

// 组件卸载时清理
onUnmounted(() => {
  // 不再需要清理计时器，因为计时器已经移到 PracticeNavbar 组件中
  // 移除浏览器刷新/关闭事件监听
  window.removeEventListener("beforeunload", handleBeforeUnload);
});

// 添加浏览器刷新/关闭事件监听
window.addEventListener("beforeunload", handleBeforeUnload);
</script>

<style scoped>
/* 练习页面主容器 */
.practice-view {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

/* 练习内容区域 */
.practice-content {
  display: flex;
  flex: 1;
  overflow: hidden;
}

/* 主内容区域 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 20px;
  background: #f5f7fa;
}

/* 题目容器 */
.question-container {
  flex: 1;
  overflow-y: auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

/* 进度信息容器 */
.progress-info {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

/* 侧边栏下方的进度信息容器 */
.progress-info-sidebar {
  background: #fff;
  padding: 16px;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin: 0 0 20px 0;
  width: 240px;
  box-sizing: border-box;
}

/* 进度条容器 */
.progress-bar {
  margin-bottom: 12px;
}

/* 进度统计信息 */
.progress-stats {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .practice-content {
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .main-content {
    padding: 12px;
  }

  .progress-stats {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
