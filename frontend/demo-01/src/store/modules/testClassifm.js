import {defineStore} from 'pinia'
import { ref } from 'vue'

const testClassifm = defineStore('classifm',()=>{
  const class_data = ref([
  {
    "id": 1,
    "name": "Python 入门系列",
    "description": "从零开始学习 Python 编程语言，掌握基础语法、数据类型与流程控制。"
  },
  {
    "id": 2,
    "name": "JavaScript 核心编程",
    "description": "深入理解 JavaScript 基础与高级特性，包括闭包、原型链、异步编程等。"
  },
  {
    "id": 3,
    "name": "Web 前端开发实战",
    "description": "从 HTML/CSS 到 Vue/React，打造现代响应式网页与交互式用户界面。"
  },
  {
    "id": 4,
    "name": "数据结构与算法精讲",
    "description": "系统学习数组、链表、栈、队列、树、图等结构及经典算法实现。"
  },
  {
    "id": 5,
    "name": "Java 企业级应用开发",
    "description": "掌握 Spring Boot、MyBatis 等主流框架，构建高性能后端服务。"
  },
  {
    "id": 6,
    "name": "数据库原理与 SQL 实战",
    "description": "从 MySQL 基础到复杂查询优化，理解事务、索引与数据库设计范式。"
  },
  {
    "id": 7,
    "name": "人工智能与机器学习入门",
    "description": "使用 Python 和 scikit-learn 探索监督学习、分类、回归与模型评估。"
  },
  {
    "id": 8,
    "name": "Linux 与 Shell 编程",
    "description": "掌握常用命令、脚本编写、权限管理及服务器环境配置技巧。"
  },
  {
    "id": 9,
    "name": "Git 与团队协作开发",
    "description": "从版本控制基础到分支管理、代码审查与 CI/CD 流程实践。"
  },
  {
    "id": 10,
    "name": "计算机网络与安全基础",
    "description": "理解 HTTP/HTTPS、TCP/IP、DNS 等协议，初步掌握网络安全防护知识。"
  }
])
return {class_data}
})

export {testClassifm}