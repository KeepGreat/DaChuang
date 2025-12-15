import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

// 导入所有store模块
import { useCounterStore } from "./modules/testCountStore";
import { testClassifm } from "./modules/testClassifm";
import { useCourseStore } from "./modules/courseStore";
import { usePracticeStore } from "./modules/practiceStore";
import { useTaskStore } from "./modules/taskStore";
import { useDiscussionStore } from "./modules/discussionStore";
import { useResourceStore } from "./modules/resourceStore";
import { useExamStore } from "./modules/examStore";
import { useQuestionsStore } from "./modules/questionsStore";
import { useAnswerStore } from "./modules/answerStore";
import { useUserAnswerStore } from "./modules/userAnswerStore";
import { useQuestionResourceStore } from "./modules/questionResourceStore";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 统一导出store
export * from "./modules/user";

export {
  pinia,
  // 原有的store
  useCounterStore,
  testClassifm,
  // 新增的store
  useCourseStore,
  usePracticeStore,
  useTaskStore,
  useDiscussionStore,
  useResourceStore,
  useExamStore,
  useQuestionsStore,
  useAnswerStore,
  useUserAnswerStore,
  useQuestionResourceStore
}
