# 使用 Python 3.13 的官方精简版镜像，基于 Debian 12 (Bookworm)
FROM python:3.13-slim-bookworm

# 设置工作目录
WORKDIR /app

# 1. 安装系统依赖 & 清理缓存 (减少镜像层大小)
RUN apt-get update && apt-get install -y --no-install-recommends \
    # 安装编译Python包可能需要的工具
    gcc \
    && rm -rf /var/lib/apt/lists/*

# 2. 复制依赖清单并安装Python依赖
# 先复制 requirements.txt，利用Docker缓存层，避免依赖未变更时重复安装
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 3. 将当前目录所有文件复制到容器的工作目录
COPY . .

# 4. 设置环境变量 (按需调整)
ENV PYTHONPATH=/app \
    PYTHONUNBUFFERED=1

# 5. 声明容器运行时监听的端口 (FastAPI默认 8000)
EXPOSE 8000

# 6. 启动 FastAPI 应用
# 使用 uvicorn 运行 main.py 中的 app 对象，监听所有接口
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]