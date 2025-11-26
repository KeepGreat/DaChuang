import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import { useCounterStore } from "./modules/testCountStore";
import { testClassifm } from "./modules/testClassifm";
import { userStore } from "./modules/user";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

export {
  pinia,
  useCounterStore,
  testClassifm,
  userStore
}