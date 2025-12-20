import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

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
