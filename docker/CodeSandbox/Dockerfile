# 第一阶段：复制 go-judge 可执行文件
FROM criyle/go-judge:latest AS go-judge

# 第二阶段：构建运行环境
FROM debian:bookworm

# 写入阿里云 Debian 镜像源
RUN echo "deb http://mirrors.aliyun.com/debian/ bookworm main contrib non-free non-free-firmware\n\
    deb http://mirrors.aliyun.com/debian/ bookworm-updates main contrib non-free non-free-firmware\n\
    deb http://mirrors.aliyun.com/debian-security bookworm-security main contrib non-free non-free-firmware" \
    > /etc/apt/sources.list && \
    apt-get clean && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
    python3 \
    g++ \
    openjdk-17-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt

COPY --from=go-judge /opt/go-judge /opt/mount.yaml /opt/

EXPOSE 5050/tcp 5051/tcp 5052/tcp

ENTRYPOINT ["./go-judge"]
