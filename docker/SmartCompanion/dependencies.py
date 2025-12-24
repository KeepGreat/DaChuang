from typing import Optional
import redis.asyncio as redis
from fastapi import HTTPException

from config import config
from main import companion_instances, LearningCompanionGraph

async def get_redis_client() -> Optional[redis.Redis]:
    """获取 Redis 客户端"""
    if config.REDIS_ENABLED:
        try:
            client = redis.from_url(config.REDIS_URL)
            await client.ping()  # 测试连接
            return client
        except Exception as e:
            print(f"Redis 连接失败: {e}")
            return None
    return None

def get_companion(session_id: str) -> LearningCompanionGraph:
    """获取或创建 companion 实例"""
    if session_id not in companion_instances:
        # 可以在这里从 Redis 恢复会话状态
        companion = LearningCompanionGraph(session_id=session_id, clear_history=False)
        companion_instances[session_id] = companion
    return companion_instances[session_id]

async def validate_api_key(api_key: Optional[str] = None):
    """API 密钥验证（可选）"""
    if config.API_KEY:
        if not api_key or api_key != config.API_KEY:
            raise HTTPException(
                status_code=403,
                detail="无效的 API 密钥"
            )
    return True