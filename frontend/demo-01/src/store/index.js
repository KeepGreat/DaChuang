import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import { testClassifm } from "./modules/testClassifm";
import { useCounterStore } from "./modules/testCountStore";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// 统一导出store
export * from "./modules/user";

export { pinia, testClassifm, useCounterStore, userStore };


