import request from "@/utils/request";
// src/api/modules/teaching/TeachingAPI.js

export default {
  // 创建会话
  createSession() {
    return request({
      url: "/api/teaching/smartcompanion/session/create",
      method: "post",
      data: {
        session_id: null,
        clear_history: true,
        max_turns: null
      }
    });
  },

  // 发送消息获取回复
  chat(data) {
    return request({
      url: "/api/teaching/smartcompanion/chat",
      method: "post",
      data,
    });
  },

  // 获取对话历史（可选，用于加载历史）
  getHistory(sessionId) {
    return request({
      url: `/api/teaching/smartcompanion/history/${sessionId}`,
      method: "get",
    });
  },

  // 清空历史（可选）
  clearHistory(sessionId) {
    return request({
      url: `/api/teaching/smartcompanion/history/${sessionId}/clear`,
      method: "delete",
    });
  },
};
