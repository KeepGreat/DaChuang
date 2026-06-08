# 基于Agent的智能编程教育系统 —— 前端项目报告

---

## 一、项目总体介绍

### 1.1 项目概述

**慧编未来**（Smart Programming Education Platform）是一个基于 Agent 的智能编程教育系统，旨在为编程学习者提供智能化、个性化的学习体验。系统以 Vue 3 为核心前端框架，结合 AI 智能助教、知识图谱、代码沙箱等前沿技术，构建了一个集课程学习、编程练习、智能答疑、学习分析于一体的综合性编程教育平台。

平台面向三类用户角色：
- **学生（Student）**：参与课程学习、完成编程练习、与 AI 学伴互动、查看个人学习画像
- **教师（Teacher）**：管理课程与章节、发布练习与考试、配置 AI 提示词、编辑知识图谱
- **管理员（Admin）**：用户管理与权限配置

### 1.2 核心功能

| 功能模块 | 描述 |
|---------|------|
| **课程学习系统** | 课程章节浏览、课程系列学习、视频/PDF 资料播放 |
| **智能 AI 助教** | 基于流式响应的 AI 问答，支持代码分享与 RAG 检索增强生成 |
| **AI 智能学伴** | 引导式对话学习，通过会话管理实现个性化教学交互 |
| **编程练习系统** | 在线编程题目练习，支持 C++/Python/Java 代码沙箱与自动判题 |
| **知识图谱** | 可视化知识节点与关系网络，支持节点导入/导出与资料关联 |
| **学习画像** | 学习进度追踪、学习时长统计、成就系统与课程证书 |
| **教师工作站** | 课程管理、练习管理、提示词配置、用户管理的统一入口 |

### 1.3 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| 前端框架 | Vue 3 (Composition API) | ^3.5.10 |
| 构建工具 | Vite | ^5.4.8 |
| 路由管理 | Vue Router (Hash 模式) | ^4.5.1 |
| 状态管理 | Pinia（持久化插件） | ^3.0.4 |
| UI 组件库 | Element Plus | ^2.11.2 |
| CSS 框架 | Tailwind CSS + SCSS | ^3.4.18 / ^1.97.0 |
| HTTP 客户端 | Axios | ^1.12.2 |
| 数据可视化 | ECharts | ^6.0.0 |
| 代码高亮 | Highlight.js | ^11.11.1 |
| Markdown 渲染 | Marked | ^16.3.0 |
| 视频播放 | Video.js | ^8.23.4 |
| PDF 查看 | vue3-pdf-app | ^1.0.3 |
| Mock 数据 | Mock.js + vite-plugin-mock | ^1.1.0 |
| 代码检查 | ESLint + eslint-plugin-vue | ^9.39.1 |

---

## 二、项目架构介绍

### 2.1 整体架构图

```mermaid
graph TB
    subgraph 用户层["👤 用户层"]
        Student["学生"]
        Teacher["教师"]
        Admin["管理员"]
    end

    subgraph 前端应用["🖥️ 前端应用层 (Vue 3 + Vite)"]
        direction TB
        
        subgraph 视图层["Views 视图层"]
            HomePage["首页<br/>MainLanding"]
            TeachingView["教学视图<br/>Course/Practice/Exam"]
            PracticeView["练习视图<br/>Practice"]
            AICompanion["AI学伴<br/>AICompanion"]
            ProfileView["学习画像<br/>Profile"]
            TeacherView["教师工作站<br/>WorkStation"]
        end

        subgraph 组件层["Components 组件层"]
            LayoutComp["布局组件<br/>Layout / TeacherLayout"]
            TeachingComp["教学组件<br/>AIAssistant / CodeSandbox<br/>MaterialPlayer / KnowledgeGraph"]
            PracticeComp["练习组件<br/>QuestionDisplay / ProgrammingQuestion<br/>PracticeNavbar"]
            ProfileComp["画像组件<br/>UserHeader / LearningTime<br/>Achievements"]
            AdminComp["管理组件<br/>CourseEditor / PracticeEditor<br/>QuestionEditor / PromptEditor"]
        end

        subgraph 核心层["核心逻辑层"]
            Router["路由管理<br/>Vue Router<br/>路由守卫 + 权限控制"]
            Store["状态管理<br/>Pinia<br/>持久化存储"]
            API["API 层<br/>Axios + 拦截器<br/>JWT 认证"]
            Hooks["自定义 Hooks<br/>表单验证"]
        end

        subgraph 工具层["工具与样式层"]
            Utils["工具函数<br/>错误处理 / 类型定义"]
            Styles["样式系统<br/>SCSS + Tailwind CSS"]
            Mock["Mock 服务<br/>Mock.js"]
        end
    end

    subgraph 后端服务["🔗 后端 API 服务"]
        AuthAPI["认证服务<br/>JWT 鉴权"]
        TeachingAPI["教学服务<br/>课程/章节/资料"]
        AIAPI["AI 服务<br/>智能问答/流式响应"]
        PracticeAPI["练习服务<br/>题目/作答/判题"]
        KGAPI["知识图谱服务<br/>节点/边/索引"]
        ProfileAPI["画像服务<br/>用户信息/评价"]
    end

    Student --> HomePage
    Teacher --> TeacherView
    Admin --> TeacherView
    
    视图层 --> 组件层
    组件层 --> 核心层
    核心层 --> 工具层
    核心层 --> 后端服务
    Mock -.->|开发阶段| 核心层
```

### 2.2 路由架构

```mermaid
graph LR
    subgraph 路由入口["Router Entry"]
        HashHistory["Hash History"]
        RouteGuard["路由守卫<br/>beforeEach"]
    end

    subgraph 布局路由["布局路由"]
        MainLayout["Layout.vue<br/>（学生端）"]
        TeacherLayout["TeacherLayout.vue<br/>（教师/管理员端）"]
    end

    subgraph 学生端["📚 学生端页面"]
        Landing["/ → 首页"]
        Profile["/profile → 学习画像"]
        LearningTime["/LearningTime → 学习时长"]
        CourseSection["/coursesection → 课程章节"]
        TeachCourse["/teaching/course/:id → 课程学习"]
        CourseList["/learn → 课程列表"]
        PracticeList["/practice → 练习列表"]
        ExamList["/exam → 考试列表"]
        KnowledgeGraph["/knowledge → 知识图谱"]
    end

    subgraph 独立路由["独立页面"]
        Practice["/practice/:csId/:pId → 练习"]
        AICompanionPage["/ai-companion → AI学伴"]
    end

    subgraph 教师端["🎓 教师/管理员端"]
        WorkStation["/teacher → 工作站"]
        CourseMgr["/teacher/cs → 课程管理"]
        PracticeMgr["/teacher/pd → 练习管理"]
        PromptEditor["/teacher/pme → 提示词编辑"]
        UserMgr["/admin/user → 用户管理"]
    end

    subgraph 认证["🔐 认证页面"]
        Login["/login → 登录"]
        Register["/register → 注册"]
    end

    HashHistory --> RouteGuard
    RouteGuard --> MainLayout
    RouteGuard --> TeacherLayout
    RouteGuard --> Practice
    RouteGuard --> AICompanionPage
    RouteGuard --> Login
    RouteGuard --> Register

    MainLayout --> Landing
    MainLayout --> Profile
    MainLayout --> CourseSection
    MainLayout --> TeachCourse
    TeachCourse --> CourseList
    TeachCourse --> PracticeList
    TeachCourse --> ExamList
    TeachCourse --> KnowledgeGraph

    TeacherLayout --> WorkStation
    TeacherLayout --> CourseMgr
    TeacherLayout --> PracticeMgr
    TeacherLayout --> PromptEditor
    TeacherLayout --> UserMgr
```

### 2.3 状态管理架构（Pinia Store）

```mermaid
graph TB
    subgraph Pinia["Pinia Store 模块"]
        UserStore["userStore<br/>用户认证状态<br/>token / role / userId"]
        CourseStore["courseStore<br/>课程数据管理"]
        PracticeStores["练习相关 Store"]
        TeachingStore["teachingStore<br/>教学状态"]
        ExamStore["examStore<br/>考试状态"]
        ResourceStore["resourceStore<br/>资源管理"]
    end

    subgraph PracticeStores_detail["练习相关 Store 详情"]
        PracticeStore["practiceStore<br/>练习数据"]
        QuestionsStore["questionsStore<br/>题目数据"]
        AnswerStore["answerStore<br/>答案管理"]
        UserAnswerStore["userAnswerStore<br/>用户作答"]
        QuestionResourceStore["questionResourceStore<br/>题目资源"]
    end

    subgraph 持久化["持久化插件"]
        PersistedState["pinia-plugin-persistedstate"]
    end

    UserStore --> PersistedState
    PracticeStores --> PracticeStores_detail
    PracticeStores_detail --> PersistedState
```

### 2.4 API 请求流程

```mermaid
sequenceDiagram
    participant View as Vue 组件
    participant Store as Pinia Store
    participant API as API 模块
    participant Request as Axios 实例
    participant Interceptor as 拦截器
    participant Backend as 后端服务

    View->>Store: 调用 Store Action
    Store->>API: 调用 API 函数
    API->>Request: 发起 HTTP 请求
    Request->>Interceptor: 请求拦截器<br/>注入 JWT Token
    Interceptor-->>Request: 添加认证头
    Request->>Backend: 发送请求
    Backend-->>Request: 返回响应
    Request->>Interceptor: 响应拦截器<br/>错误处理 / 数据转换
    Interceptor-->>Request: 处理后的响应
    Request-->>API: 响应数据
    API-->>Store: 返回结果
    Store-->>View: 更新视图状态
```

### 2.5 AI 智能交互流程

```mermaid
sequenceDiagram
    participant Student as 学生
    participant AIAssistant as AI 助教组件
    participant CodeSandbox as 代码沙箱
    participant AIAPI as AI API 模块
    participant Backend as 后端 AI 服务
    participant LLM as 大语言模型

    Student->>AIAssistant: 输入问题描述
    Student->>CodeSandbox: 编写/粘贴代码
    
    Student->>AIAssistant: 点击发送
    AIAssistant->>AIAPI: teachStream(teachingInput)
    Note over AIAPI: 组装 TeachingInput<br/>question + codeSandboxInput<br/>+ relativeMaterialIds
    
    AIAPI->>Backend: POST /api/teaching/ai/teach<br/>(Fetch + ReadableStream)
    Backend->>LLM: 调用 LLM 生成回答<br/>（结合 RAG 检索相关资料）
    LLM-->>Backend: 流式返回
    Backend-->>AIAPI: SSE 流式响应
    AIAPI-->>AIAssistant: onChunk 逐块回调
    AIAssistant-->>Student: 实时渲染 Markdown 回答

    Note over AIAssistant,Student: AI 学伴模式 (SmartCompanion)
    Student->>Backend: 创建会话 (createSession)
    Backend-->>Student: sessionId
    Student->>Backend: 发送消息 (chat)
    Backend->>LLM: 引导式对话
    LLM-->>Backend: 回复
    Backend-->>Student: 对话回复
```

### 2.6 目录结构概览

```
demo-01/
├── index.html                     # 入口 HTML
├── package.json                   # 依赖配置
├── vite.config.js                 # Vite 构建配置
├── tailwind.config.js             # Tailwind CSS 配置
├── postcss.config.js              # PostCSS 配置
├── eslint.config.js               # ESLint 配置
├── public/                        # 静态资源
└── src/
    ├── main.js                    # 应用入口（挂载 Vue、Pinia、Router、ElementPlus）
    ├── App.vue                    # 根组件
    ├── BlankTemplate.vue          # 空白模板
    ├── router/
    │   └── router.js              # 路由配置 + 路由守卫
    ├── store/
    │   ├── index.js               # Pinia 实例 + 统一导出
    │   └── modules/               # 各业务 Store 模块
    ├── api/
    │   ├── index.js               # API 统一导出
    │   ├── request.js             # Axios 实例 + 拦截器
    │   └── modules/               # 按业务分类的 API 模块
    ├── hooks/
    │   ├── index.js               # Hooks 统一导出
    │   └── common/                # 通用 Hooks
    ├── utils/
    │   ├── error.js               # 业务错误类
    │   └── types.js               # 类型定义 (JSDoc)
    ├── mock/
    │   ├── index.js               # Mock 统一导出
    │   ├── auth/                  # 认证 Mock
    │   └── practice/              # 练习 Mock
    ├── styles/
    │   ├── index.scss             # 全局样式入口
    │   └── modules/               # SCSS 模块（变量、重置）
    ├── view/                      # 页面视图
    │   ├── homepage/              # 首页
    │   ├── auth/                  # 登录/注册
    │   ├── teaching/              # 教学相关页面
    │   ├── practice/              # 练习页面
    │   ├── ai/                    # AI 学伴页面
    │   ├── teacher/               # 教师工作站
    │   └── test/                  # 测试页面
    ├── components/                # 可复用组件
    │   ├── Layout.vue             # 学生端布局
    │   ├── TeacherLayout.vue      # 教师端布局
    │   ├── homepage/              # 首页组件
    │   ├── teaching/              # 教学组件（AI助手、代码沙箱等）
    │   ├── practice/              # 练习组件
    │   ├── profiling/             # 画像组件
    │   └── admin/                 # 管理组件
    └── assets/                    # 静态资源
        ├── homepage/              # 首页图片
        ├── profile/               # 画像图片
        └── teaching/              # 教学图片
```

### 2.7 关键技术特点

1. **基于 Agent 的智能交互**：系统通过 AI 助教（AIAssistant）和 AI 学伴（SmartCompanion）两种模式，实现智能化的编程教学交互。AI 助教支持流式响应（SSE），可结合课程资料进行 RAG 检索增强生成；AI 学伴则通过会话管理实现引导式对话学习。

2. **多角色权限控制**：基于 JWT 的身份认证，结合路由守卫实现学生、教师、管理员三级权限管控，不同角色拥有不同的默认路由和功能入口。

3. **模块化架构设计**：采用 Views → Components → Store → API 的分层架构，各层职责清晰，组件可复用性强。API 层按业务模块（teaching、practice、auth、profiling）组织，便于维护和扩展。

4. **代码沙箱与在线判题**：集成 Highlight.js 实现的代码编辑器，支持 C++、Python、Java 三种语言的高亮显示，结合后端自动判题服务，实现即时的编程练习反馈。

5. **知识图谱可视化**：支持知识节点与边的 CRUD 操作，通过 ECharts 实现知识图谱的可视化展示，支持 Excel 批量导入导出，将知识点与教学资料进行关联索引。

6. **开发效率保障**：集成 Mock.js + vite-plugin-mock 实现前端独立开发，ESLint 保障代码质量，Pinia 持久化插件简化状态管理，Tailwind CSS + SCSS 提升样式开发效率。
