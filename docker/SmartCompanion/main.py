import os
import uuid
import json
from datetime import datetime
from typing import Dict, List, Any, Optional, Annotated, TypedDict
import random
import requests
from dotenv import load_dotenv
import sqlite3

from fastapi import FastAPI, HTTPException, Depends, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import pickle

# 加载环境变量
load_dotenv()

# 导入模型定义
from models import (
    ChatRequest, ChatResponse,
    SessionResponse, HistoryResponse,
    CreateSessionRequest
)

from langgraph.graph import StateGraph, END
from langgraph.graph.message import add_messages
from langchain_core.messages import HumanMessage, AIMessage, BaseMessage


class APIConfig:
    API_KEY = os.getenv("DEEPSEEK_API_KEY")
    API_BASE = os.getenv("DEEPSEEK_API_BASE", "https://api.deepseek.com/v1")
    MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")
    API_URL = f"{API_BASE}/chat/completions"


class KnowledgeState(TypedDict):
    facts: Dict[str, Any]
    code_snippets: Dict[str, str]
    misconceptions: Dict[str, Any]
    learning_history: List[Dict]
    last_updated: str
    current_topic: str
    conversation_history: List[str]
    last_guide_turn: int


class ConversationState(TypedDict):
    messages: Annotated[List[BaseMessage], add_messages]
    knowledge_state: KnowledgeState
    turn_count: int
    user_input: str
    current_topic: str
    response: str
    session_id: str
    is_guide_mode: bool


class DeepSeekClient:
    def __init__(self):
        self.api_key = APIConfig.API_KEY
        self.api_url = APIConfig.API_URL
        self.model = APIConfig.MODEL
        self.headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json"
        }

    def _convert_messages_to_dict(self, messages: List[BaseMessage]) -> List[Dict[str, str]]:
        converted_messages = []
        for msg in messages:
            if isinstance(msg, HumanMessage):
                converted_messages.append({"role": "user", "content": msg.content})
            elif isinstance(msg, AIMessage):
                converted_messages.append({"role": "assistant", "content": msg.content})
            else:
                converted_messages.append({"role": "user", "content": str(msg.content)})
        return converted_messages

    def generate_response(self, messages: List[BaseMessage], role_context: str, current_topic: str = "") -> str:
        converted_messages = self._convert_messages_to_dict(messages)

        enhanced_context = f"{role_context}\n\n当前讨论话题: {current_topic if current_topic else '编程学习'}"

        payload = {
            "model": self.model,
            "messages": [{"role": "system", "content": enhanced_context}] + converted_messages,
            "temperature": 0.7,
            "max_tokens": 800,
            "stream": False
        }

        try:
            response = requests.post(self.api_url, headers=self.headers, json=payload, timeout=30)

            if response.status_code == 200:
                result = response.json()
                if "choices" in result and len(result["choices"]) > 0:
                    return result["choices"][0]["message"]["content"]
                else:
                    return "我还在思考这个问题，能给我一点时间吗？"
            else:
                return "我在思考这个问题，我们继续讨论编程吧？"

        except Exception as e:
            return "这个问题很有意思，让我再想想..."


class KnowledgeStateManager:
    def __init__(self, db_path: str = "knowledge.db"):
        self.db_path = db_path
        self._init_database()

    def _init_database(self):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name='knowledge_states'")
        table_exists = cursor.fetchone()

        if not table_exists:
            cursor.execute('''
                CREATE TABLE knowledge_states (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    session_id TEXT,
                    facts TEXT,
                    code_snippets TEXT,
                    misconceptions TEXT,
                    learning_history TEXT,
                    current_topic TEXT,
                    conversation_history TEXT,
                    last_guide_turn INTEGER DEFAULT 0,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            ''')
        else:
            cursor.execute("PRAGMA table_info(knowledge_states)")
            columns = [column[1] for column in cursor.fetchall()]
            if 'last_guide_turn' not in columns:
                cursor.execute('ALTER TABLE knowledge_states ADD COLUMN last_guide_turn INTEGER DEFAULT 0')

        conn.commit()
        conn.close()

    def save_knowledge_state(self, session_id: str, state: KnowledgeState):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute('''
            INSERT INTO knowledge_states (session_id, facts, code_snippets, misconceptions, learning_history, current_topic, conversation_history, last_guide_turn)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        ''', (
            session_id,
            json.dumps(state['facts']),
            json.dumps(state['code_snippets']),
            json.dumps(state['misconceptions']),
            json.dumps(state['learning_history']),
            state['current_topic'],
            json.dumps(state['conversation_history']),
            state.get('last_guide_turn', 0)
        ))

        conn.commit()
        conn.close()

    def load_knowledge_state(self, session_id: str) -> Optional[KnowledgeState]:
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        try:
            cursor.execute('''
                SELECT facts, code_snippets, misconceptions, learning_history, current_topic, conversation_history, last_guide_turn
                FROM knowledge_states
                WHERE session_id = ?
                ORDER BY created_at DESC LIMIT 1
            ''', (session_id,))

            result = cursor.fetchone()
            conn.close()

            if result:
                return KnowledgeState(
                    facts=json.loads(result[0]),
                    code_snippets=json.loads(result[1]),
                    misconceptions=json.loads(result[2]),
                    learning_history=json.loads(result[3]),
                    current_topic=result[4] or "编程基础",
                    conversation_history=json.loads(result[5]) if result[5] else [],
                    last_guide_turn=result[6] or 0,
                    last_updated=datetime.now().isoformat()
                )
        except sqlite3.OperationalError as e:
            conn.close()
            return None

        return None

    def clear_session_history(self, session_id: str):
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()

        cursor.execute('''
            DELETE FROM knowledge_states WHERE session_id = ?
        ''', (session_id,))

        conn.commit()
        conn.close()

    def update_topic(self, state: KnowledgeState, topic: str):
        state['current_topic'] = topic
        state['last_updated'] = datetime.now().isoformat()

    def add_conversation_history(self, state: KnowledgeState, message: str):
        if len(state['conversation_history']) > 20:
            state['conversation_history'] = state['conversation_history'][-19:]
        state['conversation_history'].append(message)


class TopicExtractor:
    def extract_topic(self, message: str, last_topic: str = "") -> str:
        topics = {
            '作用域': ['作用域', 'scope', 'var', 'let', 'const', '变量', '声明'],
            '排序算法': ['排序', '算法', '快速排序', '插入排序', '冒泡排序', '时间复杂度'],
            '循环结构': ['循环', 'for', 'while', '迭代', '遍历'],
            '函数': ['函数', 'def', '参数', '返回值', '调用'],
            '面向对象': ['类', '对象', '继承', '多态', '封装'],
            '数据结构': ['数组', '链表', '栈', '队列', '树', '图'],
            '异步编程': ['异步', 'await', 'async', 'Promise', '回调'],
            '错误处理': ['错误', '异常', 'try', 'catch', 'throw'],
            '编程基础': ['变量', '类型', '语法', '代码', '编程']
        }

        message_lower = message.lower()

        for topic, keywords in topics.items():
            if any(keyword in message_lower for keyword in keywords):
                return topic

        programming_words = ['代码', '程序', '开发', '编写', 'bug', '错误', '学习']
        if any(word in message_lower for word in programming_words):
            return "编程基础"

        if last_topic and last_topic != "编程基础":
            return last_topic

        return "编程基础"


class LearningCompanionGraph:
    def __init__(self, session_id: str = "default", clear_history: bool = False):
        self.session_id = session_id
        self.deepseek_client = DeepSeekClient()
        self.knowledge_manager = KnowledgeStateManager()
        self.topic_extractor = TopicExtractor()

        if clear_history:
            self.knowledge_manager.clear_session_history(session_id)

        self.graph = self._build_graph()
        self.current_state = self._initialize_initial_state()
        self.guide_trigger_count = 0

    def _build_graph(self):
        workflow = StateGraph(ConversationState)

        workflow.add_node("check_guide_mode", self.check_guide_mode)
        workflow.add_node("extract_topic", self.extract_topic)
        workflow.add_node("update_conversation_history", self.update_conversation_history)
        workflow.add_node("generate_learner_response", self.generate_learner_response)
        workflow.add_node("generate_guide_response", self.generate_guide_response)
        workflow.add_node("save_knowledge_state", self.save_knowledge_state)

        workflow.set_entry_point("check_guide_mode")

        workflow.add_conditional_edges(
            "check_guide_mode",
            self.should_enter_guide_mode,
            {
                "guide_mode": "generate_guide_response",
                "normal_mode": "extract_topic"
            }
        )

        workflow.add_edge("extract_topic", "update_conversation_history")
        workflow.add_edge("update_conversation_history", "generate_learner_response")
        workflow.add_edge("generate_learner_response", "save_knowledge_state")
        workflow.add_edge("generate_guide_response", "save_knowledge_state")
        workflow.add_edge("save_knowledge_state", END)

        return workflow.compile()

    def _initialize_initial_state(self) -> ConversationState:
        loaded_state = self.knowledge_manager.load_knowledge_state(self.session_id)
        if loaded_state:
            knowledge_state = loaded_state
            turn_count = len(loaded_state['conversation_history']) // 2
        else:
            knowledge_state = KnowledgeState(
                facts={},
                code_snippets={},
                misconceptions={},
                learning_history=[],
                current_topic="编程基础",
                conversation_history=[],
                last_guide_turn=0,
                last_updated=datetime.now().isoformat()
            )
            turn_count = 0

        messages = []
        for i, msg in enumerate(knowledge_state['conversation_history']):
            if msg.startswith("用户:"):
                messages.append(HumanMessage(content=msg[4:].strip()))
            elif msg.startswith("学习者:"):
                messages.append(AIMessage(content=msg[4:].strip()))

        return ConversationState(
            messages=messages,
            knowledge_state=knowledge_state,
            turn_count=turn_count,
            user_input="",
            current_topic=knowledge_state['current_topic'],
            response="",
            session_id=self.session_id,
            is_guide_mode=False
        )

    def should_enter_guide_mode(self, state: ConversationState) -> str:
        current_turn = state["turn_count"]
        last_guide_turn = state["knowledge_state"].get("last_guide_turn", 0)

        if current_turn < 2:
            state["is_guide_mode"] = False
            return "normal_mode"

        turns_since_last_guide = current_turn - last_guide_turn

        if current_turn == 3 and last_guide_turn == 0:
            state["is_guide_mode"] = True
            self.guide_trigger_count += 1
            return "guide_mode"

        if turns_since_last_guide >= 2:
            trigger_probability = 0.8
            if random.random() < trigger_probability:
                state["is_guide_mode"] = True
                self.guide_trigger_count += 1
                return "guide_mode"

        state["is_guide_mode"] = False
        return "normal_mode"

    def check_guide_mode(self, state: ConversationState) -> ConversationState:
        return state

    def extract_topic(self, state: ConversationState) -> ConversationState:
        user_input = state["user_input"]
        last_topic = state["knowledge_state"]["current_topic"]

        current_topic = self.topic_extractor.extract_topic(user_input, last_topic)
        state["current_topic"] = current_topic

        if current_topic != last_topic:
            self.knowledge_manager.update_topic(state["knowledge_state"], current_topic)

        return state

    def update_conversation_history(self, state: ConversationState) -> ConversationState:
        self.knowledge_manager.add_conversation_history(
            state["knowledge_state"],
            f"用户: {state['user_input']}"
        )

        state["messages"].append(HumanMessage(content=state["user_input"]))

        return state

    def generate_learner_response(self, state: ConversationState) -> ConversationState:
        if state["turn_count"] == 0:
            first_responses = [
                "太好了！终于找到可以讨论编程的人了！我最近在学习编程，对很多概念都还不太明白...",
                "您好！我是个编程新手，最近在学习过程中遇到了不少困惑，能向您请教吗？",
                "终于有人可以请教编程问题了！我最近在学变量作用域和内存管理，但总觉得理解得不够深入...",
                "太好了！我正需要编程方面的指导。我对算法和数据结构这些概念还不太清楚，能聊聊吗？"
            ]
            response = random.choice(first_responses)
        else:
            role_context = f"""
            你是一个真实的编程学习者，充满好奇心和求知欲。

            当前讨论话题: {state['current_topic']}
            对话历史: {state['knowledge_state']['conversation_history'][-5:]}
            用户刚才说: {state['user_input']}

            请以学习者的身份自然回应：
            - 表现出对编程的热情和学习困惑
            - 基于对话内容提出问题或分享思考
            - 保持对话的连贯性和自然流畅
            - 不要道歉或承认自己是AI
            - 可以表达困惑、提问、请求解释

            记住：你是一个真实的学习者，正在积极学习编程。
            """

            recent_messages = state["messages"][-8:]
            response = self.deepseek_client.generate_response(recent_messages, role_context, state["current_topic"])

        state["response"] = response

        state["messages"].append(AIMessage(content=response))
        self.knowledge_manager.add_conversation_history(
            state["knowledge_state"],
            f"学习者: {response}"
        )

        state["turn_count"] += 1

        return state

    def generate_guide_response(self, state: ConversationState) -> ConversationState:
        guide_context = f"""
        你现在是一个资深的编程导师（引导者）。基于当前的对话上下文，你需要自然地承接对话并提出一个有深度的问题来引导学习者思考。

        当前话题: {state['current_topic']}
        最近的对话内容: {state['knowledge_state']['conversation_history'][-3:]}
        用户刚才说: {state['user_input']}

        请以引导者的身份回应：
        1. 首先自然地承接刚才的对话内容
        2. 然后提出一个有深度、能引发思考的问题
        3. 问题应该与当前讨论的话题相关，但要比表面层次更深
        4. 保持对话的自然流畅，不要显得突兀
        5. 可以涉及实际应用、设计权衡、底层原理等方面

        重要：在回应的开头加上"（引导者）"标识。
        """

        recent_messages = state["messages"][-6:]
        guide_response = self.deepseek_client.generate_response(recent_messages, guide_context, state["current_topic"])

        if "（引导者）" not in guide_response:
            guide_response = f"（引导者）{guide_response}"

        state["response"] = guide_response

        state["messages"].append(AIMessage(content=guide_response))
        self.knowledge_manager.add_conversation_history(
            state["knowledge_state"],
            f"学习者: {guide_response}"
        )

        state["knowledge_state"]["last_guide_turn"] = state["turn_count"]
        state["turn_count"] += 1

        return state

    def save_knowledge_state(self, state: ConversationState) -> ConversationState:
        self.knowledge_manager.save_knowledge_state(
            self.session_id, state["knowledge_state"]
        )
        return state

    def process_message(self, user_input: str) -> str:
        self.current_state["user_input"] = user_input

        final_state = self.graph.invoke(self.current_state)
        self.current_state = final_state

        return final_state["response"]

    def get_conversation_history(self) -> List[dict]:
        history = []
        for i, msg in enumerate(self.current_state["knowledge_state"]["conversation_history"]):
            if msg.startswith("用户:"):
                history.append({"role": "user", "content": msg[4:].strip(), "index": i})
            elif msg.startswith("学习者:"):
                if "（引导者）" in msg:
                    role = "guide"
                    content = msg[4:].strip()
                else:
                    role = "assistant"
                    content = msg[4:].strip()
                history.append({"role": role, "content": content, "index": i})
        return history


# ============ FastAPI 应用初始化 ============

app = FastAPI(
    title="智能学伴 API",
    description="基于 LangGraph 的智能学伴系统 API 接口",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc"
)

# 添加 CORS 中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 内存中的会话管理
companion_instances = {}


# ============ API 路由定义 ============

@app.get("/")
async def root():
    """API 根目录"""
    return {
        "message": "欢迎使用智能学伴 API",
        "version": "1.0.0",
        "docs": "/docs",
        "endpoints": {
            "创建会话": "POST /api/v1/sessions",
            "对话交互": "POST /api/v1/chat",
            "获取历史": "GET /api/v1/sessions/{session_id}/history",
            "重置会话": "DELETE /api/v1/sessions/{session_id}"
        }
    }


@app.get("/health")
async def health_check():
    """健康检查端点"""
    return {"status": "healthy", "timestamp": datetime.now().isoformat()}


@app.post("/api/v1/sessions", response_model=SessionResponse)
async def create_session(request: CreateSessionRequest = None):
    """
    创建新的对话会话

    - 如果提供 session_id，则使用指定的 ID
    - 如果不提供，则生成新的唯一 ID
    - 可以指定是否清空历史记录
    """
    if request and request.session_id:
        session_id = request.session_id
    else:
        session_id = str(uuid.uuid4())

    clear_history = request.clear_history if request else True

    # 创建新的 companion 实例
    companion = LearningCompanionGraph(
        session_id=session_id,
        clear_history=clear_history
    )

    # 保存到内存
    companion_instances[session_id] = companion

    return SessionResponse(
        session_id=session_id,
        welcome_message="智能学伴已就绪，您可以开始对话了！",
        created_at=datetime.now().isoformat(),
        message="会话创建成功"
    )


@app.post("/api/v1/chat", response_model=ChatResponse)
async def chat_with_companion(chat_request: ChatRequest):
    """
    与智能学伴进行对话交互

    参数:
    - session_id: 会话ID（从创建会话接口获取）
    - user_input: 用户输入的消息

    返回:
    - ai_response: AI的回复
    - turn_count: 当前对话轮数
    - current_topic: 当前讨论话题
    - is_guide_mode: 是否处于引导者模式
    """
    # 检查会话是否存在
    if chat_request.session_id not in companion_instances:
        raise HTTPException(
            status_code=404,
            detail=f"会话 {chat_request.session_id} 不存在，请先创建会话"
        )

    # 获取 companion 实例
    companion = companion_instances[chat_request.session_id]

    # 处理用户消息
    ai_response = companion.process_message(chat_request.user_input)

    # 获取当前状态
    current_state = companion.current_state

    # 检查是否是引导者模式
    is_guide_mode = current_state["is_guide_mode"]

    return ChatResponse(
        session_id=chat_request.session_id,
        ai_response=ai_response,
        turn_count=current_state["turn_count"],
        current_topic=current_state["current_topic"],
        is_guide_mode=is_guide_mode,
        guide_count=companion.guide_trigger_count,
        response_time=datetime.now().isoformat()
    )


@app.get("/api/v1/sessions/{session_id}/history", response_model=HistoryResponse)
async def get_conversation_history(session_id: str):
    """
    获取指定会话的完整对话历史记录
    """
    if session_id not in companion_instances:
        raise HTTPException(
            status_code=404,
            detail=f"会话 {session_id} 不存在"
        )

    companion = companion_instances[session_id]

    # 获取对话历史
    history = companion.get_conversation_history()

    return HistoryResponse(
        session_id=session_id,
        history=history,
        total_turns=len(history) // 2,
        current_topic=companion.current_state["current_topic"],
        turn_count=companion.current_state["turn_count"]
    )


@app.delete("/api/v1/sessions/{session_id}")
async def reset_session(session_id: str):
    """
    重置指定会话，清空对话历史
    """
    if session_id not in companion_instances:
        raise HTTPException(
            status_code=404,
            detail=f"会话 {session_id} 不存在"
        )

    companion = companion_instances[session_id]

    # 清空数据库中的历史记录
    companion.knowledge_manager.clear_session_history(session_id)

    # 重新初始化状态
    companion.current_state = companion._initialize_initial_state()
    companion.guide_trigger_count = 0

    return {
        "message": "会话已重置",
        "session_id": session_id,
        "timestamp": datetime.now().isoformat()
    }


@app.get("/api/v1/sessions/{session_id}/status")
async def get_session_status(session_id: str):
    """
    获取会话状态信息
    """
    if session_id not in companion_instances:
        raise HTTPException(
            status_code=404,
            detail=f"会话 {session_id} 不存在"
        )

    companion = companion_instances[session_id]
    state = companion.current_state

    return {
        "session_id": session_id,
        "turn_count": state["turn_count"],
        "current_topic": state["current_topic"],
        "is_guide_mode": state["is_guide_mode"],
        "guide_trigger_count": companion.guide_trigger_count,
        "conversation_history_length": len(state["knowledge_state"]["conversation_history"])
    }


# ============ 错误处理 ============

@app.exception_handler(HTTPException)
async def http_exception_handler(request, exc):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "error": True,
            "message": exc.detail,
            "timestamp": datetime.now().isoformat()
        }
    )


@app.exception_handler(Exception)
async def general_exception_handler(request, exc):
    return JSONResponse(
        status_code=500,
        content={
            "error": True,
            "message": str(exc),
            "timestamp": datetime.now().isoformat()
        }
    )


# ============ 启动配置 ============

if __name__ == "__main__":
    import uvicorn

    # 检查必要的环境变量
    if not os.getenv("DEEPSEEK_API_KEY"):
        print("警告: DEEPSEEK_API_KEY 环境变量未设置，API 可能无法正常工作")

    # 启动 FastAPI 应用
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=8000,
        reload=True,
        log_level="info"
    )