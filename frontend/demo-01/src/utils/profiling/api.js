// src/utils/profiling/api.js
import axios from "axios";

// 后端网关地址（按实际修改，例如你的 gateway 在 8080 端口）
const BASE_URL = "http://localhost:8080/api/profiling";

// 统一 axios 实例
const api = axios.create({
  baseURL: BASE_URL,
  timeout: 5000,
});

// 用户基本资料
export const getUserProfile = () => api.get("/user/profile");

// 学习统计
export const getStudyStats = () => api.get("/user/stats");

// 学习趋势
export const getLearningTrend = () => api.get("/user/trend");

// 成绩概览
export const getScoreOverview = () => api.get("/user/scores");

// 技能进度
export const getSkillProgress = () => api.get("/user/skills");

// 每周活跃度
export const getWeeklyActivity = () => api.get("/user/activity");

// 成就列表
export const getAchievements = () => api.get("/user/achievements");

// 学习目标
export const getLearningGoals = () => api.get("/user/goals");

// 学习效率
export const getStudyEfficiency = () => api.get("/user/efficiency");

// 课程提醒
export const getCourseReminders = () => api.get("/user/courses");

// 学习资料推荐
export const getResourceRecommendations = () => api.get("/user/resources");

// 教师反馈
export const getTeacherFeedback = () => api.get("/user/feedback");
