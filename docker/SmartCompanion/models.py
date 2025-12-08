from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import datetime

class CreateSessionRequest(BaseModel):
    """创建会话请求模型"""
    session_id: Optional[str] = Field(
        default=None,
        description="可选的会话ID，如果不提供则自动生成"
    )
    clear_history: bool = Field(
        default=True,
        description="是否清空历史记录"
    )

class SessionResponse(BaseModel):
    """创建会话响应模型"""
    session_id: str
    welcome_message: str
    created_at: str
    message: str = "会话创建成功"

class ChatRequest(BaseModel):
    """对话请求模型"""
    session_id: str = Field(..., description="会话ID")
    user_input: str = Field(..., min_length=1, max_length=1000, description="用户输入")

class ChatResponse(BaseModel):
    """对话响应模型"""
    session_id: str
    ai_response: str
    turn_count: int
    current_topic: str
    is_guide_mode: bool = False
    guide_count: int = 0
    response_time: str

class HistoryItem(BaseModel):
    """历史记录项模型"""
    role: str  # user, assistant, guide
    content: str
    index: int

class HistoryResponse(BaseModel):
    """历史记录响应模型"""
    session_id: str
    history: List[HistoryItem]
    total_turns: int
    current_topic: str
    turn_count: int

class ErrorResponse(BaseModel):
    """错误响应模型"""
    error: bool = True
    message: str
    timestamp: str

class StatusResponse(BaseModel):
    """状态响应模型"""
    session_id: str
    turn_count: int
    current_topic: str
    is_guide_mode: bool
    guide_trigger_count: int
    conversation_history_length: int
    timestamp: str