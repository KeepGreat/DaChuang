
//调用代码在home.vue中注释的部分
import { ref, computed } from "vue";
import { defineStore } from "pinia";

const useCounterStore = defineStore(
  "counter",
  () => {
    const count = ref(100);
    const doubleCount = computed(() => count.value * 2);
    function increment() {
      count.value++;
    }

    return { count, doubleCount, increment };
  },
  {
    persist:true
  }
);

export { useCounterStore };