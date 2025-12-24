import os
from dotenv import load_dotenv

load_dotenv()


class Config:
    # API 配置
    API_TITLE = "智能学伴 API"
    API_VERSION = "1.0.0"
    API_DESCRIPTION = "基于 LangGraph 的智能学伴系统 API 接口"

    # 服务器配置
    HOST = os.getenv("HOST", "0.0.0.0")
    PORT = int(os.getenv("PORT", 8000))

    # 数据库配置
    DATABASE_PATH = os.getenv("DATABASE_PATH", "knowledge.db")

    # DeepSeek API 配置
    DEEPSEEK_API_KEY = os.getenv("DEEPSEEK_API_KEY")
    DEEPSEEK_API_BASE = os.getenv("DEEPSEEK_API_BASE", "https://api.deepseek.com/v1")
    DEEPSEEK_MODEL = os.getenv("DEEPSEEK_MODEL", "deepseek-chat")

    # Redis 配置（可选）
    REDIS_URL = os.getenv("REDIS_URL", "redis://localhost:6379")
    REDIS_ENABLED = os.getenv("REDIS_ENABLED", "false").lower() == "true"

    # 会话配置
    SESSION_TIMEOUT = int(os.getenv("SESSION_TIMEOUT", 3600))  # 1小时

    # 安全配置
    API_KEY = os.getenv("API_KEY")  # 可选API密钥验证

    @classmethod
    def validate_config(cls):
        """验证配置"""
        errors = []

        if not cls.DEEPSEEK_API_KEY:
            errors.append("DEEPSEEK_API_KEY 环境变量未设置")

        if errors:
            print("配置错误:")
            for error in errors:
                print(f"  - {error}")
            return False

        return True


# 配置实例
config = Config()