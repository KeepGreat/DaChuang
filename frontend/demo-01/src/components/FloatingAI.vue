<template>
  <div>
    <!-- AI图标（可拖动）-->
    <div
      v-if="!showChat"
      ref="iconRef"
      class="ai-floating-icon"
      :style="{ left: iconPos.x + 'px', top: iconPos.y + 'px' }"
      @mousedown="startDragIcon"
      @mouseup="iconMouseUp"
    >
      🤖
    </div>

    <!-- AI 窗口 (可拖动) -->
    <transition name="popup">
      <div
        v-if="showChat"
        ref="chatRef"
        class="ai-chat-window"
        :style="{ left: chatPos.x + 'px', top: chatPos.y + 'px' }"
      >
        <div class="chat-header" @mousedown="startDragChat">
          <span>AI 助手</span>
          <button @click="closeChat">×</button>
        </div>

        <!-- 可滚动的消息主体 -->
        <div class="chat-body" ref="bodyRef">
          <div
            v-for="msg in messages"
            :key="msg.id"
            :class="['msg-item', msg.role]"
          >
            <div class="bubble">{{ msg.text }}</div>
          </div>
        </div>

        <!-- 固定底部的输入栏 -->
        <div class="chat-input">
          <input v-model="input" placeholder="输入你的问题..." @keyup.enter="send"/>
          <button @click="send">发送</button>
        </div>
      </div>
    </transition>
  </div>
</template>


<script setup>
import { ref, nextTick } from "vue";

//=============== 状态 ===============
const showChat = ref(false);
const input = ref("");
const messages = ref([]);
const bodyRef = ref(null);

//=============== 位置逻辑 ===============
const iconPos = ref({ x: window.innerWidth - 80, y: window.innerHeight - 100 });
const chatPos = ref({ x: iconPos.value.x, y: iconPos.value.y });

const iconRef = ref(null);
const chatRef = ref(null);

let draggingIcon = false;
let draggingChat = false;
let moved = false;
let offsetX = 0;
let offsetY = 0;


//============== 限制窗口和图标不能超界 ===============
function clampToScreen(pos, w, h) {
  const pad = 10;
  let x = Math.min(Math.max(pos.x, pad), window.innerWidth - w - pad);
  let y = Math.min(Math.max(pos.y, pad), window.innerHeight - h - pad);
  return { x, y };
}


//============== 图标拖动 ===============
function startDragIcon(e) {
  draggingIcon = true;
  moved = false;
  offsetX = e.clientX - iconPos.value.x;
  offsetY = e.clientY - iconPos.value.y;
  document.addEventListener("mousemove", dragIcon);
  document.addEventListener("mouseup", stopDragIcon);
}

function dragIcon(e) {
  if (!draggingIcon) return;
  moved = true;
  iconPos.value = { x: e.clientX - offsetX, y: e.clientY - offsetY };
}

function stopDragIcon() {
  draggingIcon = false;
  iconPos.value = clampToScreen(iconPos.value, 50, 50);

  document.removeEventListener("mousemove", dragIcon);
  document.removeEventListener("mouseup", stopDragIcon);
}

function iconMouseUp() {
  if (!moved) openChat();
}


//============== 打开窗口 ===============
function openChat() {
  const iconCenter = {
    x: iconPos.value.x + 25,
    y: iconPos.value.y + 25,
  };

  chatPos.value = clampToScreen({
    x: iconCenter.x - 160,
    y: iconCenter.y - 210,
  }, 320, 420);

  showChat.value = true;
}


//============== 关闭窗口 ===============
function closeChat() {
  showChat.value = false;

  const newIconPos = {
    x: chatPos.value.x + 160 - 25,
    y: chatPos.value.y + 210 - 25,
  };

  iconPos.value = clampToScreen(newIconPos, 50, 50);
}


//============== 窗口拖动 ===============
function startDragChat(e) {
  if (e.target.tagName === "INPUT" || e.target.tagName === "BUTTON") return;

  draggingChat = true;
  offsetX = e.clientX - chatPos.value.x;
  offsetY = e.clientY - chatPos.value.y;

  document.addEventListener("mousemove", dragChat);
  document.addEventListener("mouseup", stopDragChat);
}

function dragChat(e) {
  if (!draggingChat) return;
  chatPos.value = clampToScreen({
    x: e.clientX - offsetX,
    y: e.clientY - offsetY
  }, 320, 420);

  // 图标实时跟随窗口中心
  iconPos.value = {
    x: chatPos.value.x + 160 - 25,
    y: chatPos.value.y + 210 - 25,
  };
}

function stopDragChat() {
  draggingChat = false;
  document.removeEventListener("mousemove", dragChat);
  document.removeEventListener("mouseup", stopDragChat);
}


//============== 打字动画 ===============
async function typeWriter(msg, full, speed = 18) {
  for (let ch of full.slice(msg.text.length)) {
    msg.text += ch;
    await nextTick();
    scrollBottom();
    await new Promise(r => setTimeout(r, speed));
  }
}


//============== 发送消息 ===============
async function send() {
  if (!input.value.trim()) return;

  messages.value.push({ id: Date.now(), role: "user", text: input.value });
  const question = input.value;
  input.value = "";

  const aiMsg = { id: "ai-" + Date.now(), role: "ai", text: "" };
  messages.value.push(aiMsg);

  const res = await fetch("/api/ai/answer", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ question }),
  });

  const reader = res.body.getReader();
  const decoder = new TextDecoder();

  let buffer = "";

  while (true) {
    const { value, done } = await reader.read();
    if (done) break;

    buffer += decoder.decode(value);
    await typeWriter(aiMsg, buffer);
  }

  scrollBottom();
}


//============== 滚动到底部 ===============
function scrollBottom() {
  nextTick(() => {
    if (bodyRef.value) {
      bodyRef.value.scrollTop = bodyRef.value.scrollHeight;
    }
  });
}
</script>


<style scoped>
/************** 图标 **************/
.ai-floating-icon {
  width: 50px;
  height: 50px;
  position: fixed;

  /* 🌈 从圆心向外渐变 + 渐变透明 */
  background: radial-gradient(circle at center,
    rgba(255, 114, 185, 0.95) 20%,
    rgba(255, 194, 230, 0.55) 55%,
    rgba(255, 205, 235, 0.0) 80%
  );

  border-radius: 50%;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.35);

  display: flex;
  align-items: center;
  justify-content: center;

  font-size: 28px;
  color: white;
  cursor: pointer;

  z-index: 9999;
  transition: transform .25s, background .25s;
}

.ai-floating-icon:active {
  transform: scale(1.05);
  background: radial-gradient(circle at center,
    rgba(255, 114, 185, 1) 0%,
    rgba(255, 160, 215, 0.75) 45%,
    rgba(255, 205, 235, 0.15) 100%
  );
}


/************** 窗口 **************/
.ai-chat-window {
  width: 320px;
  height: 420px;
  position: fixed;
  border-radius: 14px;
  overflow: hidden;
  z-index: 99999;

  /* 毛玻璃窗口 */
  background: rgba(255,255,255,0.35);
  backdrop-filter: blur(14px);
  border: 1px solid rgba(255,255,255,0.3);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);

  display: flex;
  flex-direction: column;
}

/************** Header **************/
.chat-header {
  background: #ff91c8;
  padding: 8px 12px;
  color: white;
  display: flex;
  justify-content: space-between;
  cursor: grab;
}
.chat-header button {
  background: transparent;
  border: none;
  color: white;
  font-size: 18px;
}

/************** 滚动聊天区 **************/
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
}

/* 隐藏滚动条 */
.chat-body::-webkit-scrollbar {
  width: 6px;
}
.chat-body::-webkit-scrollbar-thumb {
  background: #ff9ad8;
  border-radius: 3px;
}

/************** 输入栏固定底部 **************/
.chat-input {
  padding: 10px;
  display: flex;
  gap: 6px;
  background: rgba(239, 239, 239, 0.4);
  backdrop-filter: blur(6px);
}
.chat-input input {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ddd;
}

.chat-input input:focus {
  outline: none;           /* 去掉默认蓝色外框 */
  border-color: #ff8cc5;   /* 改为粉色边框 */
  box-shadow: 0 0 4px rgba(255, 114, 185, 0.6); /* 可选光晕 */
}

.chat-input button {
  background:#ff91c8;
  color:white;
  border:none;
  border-radius:6px;
  padding:8px 10px;
}



.chat-input button:hover {
  background: #ff6fb5;
}

.chat-input button:active {
  background: #ff4aa0;
}




/************** 气泡动画 & 样式 **************/
.msg-item {
  width: 100%;
  display: flex;
  margin: 6px 0;
  opacity: 0;
  transform: translateY(10px) scale(0.85);
  animation: bubbleIn 0.25s forwards;
}

.msg-item.user { justify-content: flex-end; }
.msg-item.ai   { justify-content: flex-start; }

.bubble {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 14px;
  font-size: 15px;
  line-height: 1.4;
  word-break: break-word;
}

/* 用户 */
.msg-item.user .bubble {
  background: #ff72b9;
  color: white;
  border-bottom-right-radius: 4px;
}

/* AI */
.msg-item.ai .bubble {
  background: rgba(255,255,255,0.45);
  color: #fff6fb;
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.25);
  border-bottom-left-radius: 4px;
}

@keyframes bubbleIn {
  from { opacity: 0; transform: translateY(15px) scale(0.7); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/************** 窗口缩放动画 **************/
.popup-enter-from { transform: scale(0.2); opacity: 0; }
.popup-enter-to   { transform: scale(1); opacity: 1; }
.popup-enter-active { transition: all .28s cubic-bezier(.25,.8,.25,1); }

.popup-leave-from { opacity: 1; transform: scale(1); }
.popup-leave-to   { opacity: 0; transform: scale(0.2); }
.popup-leave-active { transition: all .22s ease; }
</style>
