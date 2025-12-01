import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

// 导入所有store模块
import { useCounterStore } from "./modules/testCountStore";
import { testClassifm } from "./modules/testClassifm";
import { useCourseStore } from "./modules/courseStore";
import { useAssignmentStore } from "./modules/assignmentStore";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

export {
  pinia,
  // 原有的store
  useCounterStore,
  testClassifm,
  // 新增的store
  useCourseStore,
  useAssignmentStore
}