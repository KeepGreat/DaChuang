import "@/styles/index.scss";
import ElementPlus from "element-plus";
import "element-plus/dist/index.css";
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/router";
import { pinia } from "./store";

const app = createApp(App);
app.use(pinia);
app.use(router);
app.use(ElementPlus);
app.mount("#app");
