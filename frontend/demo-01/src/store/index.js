import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

// 导入所有store模块
import { useAssignmentStore } from "./modules/assignmentStore";
import { useCourseStore } from "./modules/courseStore";
import { useDiscussionStore } from "./modules/discussionStore";
import { useExamStore } from "./modules/examStore";
import { useQuestionsStore } from "./modules/questionsStore";
import { useResourceStore } from "./modules/resourceStore";
import { useTaskStore } from "./modules/taskStore";
import { useTeachingStore } from "./modules/teachingStore";
import { testClassifm } from "./modules/testClassifm";
import { useCounterStore } from "./modules/testCountStore";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 统一导出所有store模块
export * from "./modules/auth/user";
export * from "./modules/courseStore";
export * from "./modules/discussionStore";
export * from "./modules/examStore";
export * from "./modules/practice/answerStore";
export * from "./modules/practice/practiceStore";
export * from "./modules/practice/questionResourceStore";
export * from "./modules/practice/questionsStore";
export * from "./modules/practice/userAnswerStore";
export * from "./modules/resourceStore";
export * from "./modules/taskStore";
export * from "./modules/testClassifm";
export * from "./modules/testCountStore";

export { pinia };

	export {
		testClassifm,
		useAssignmentStore,
		// 原有的store
		useCounterStore,
		// 新增的store
		useCourseStore,
		useDiscussionStore,
		useExamStore,
		useQuestionsStore,
		useResourceStore,
		useTaskStore,
		useTeachingStore
	};

