FROM criyle/go-judge:latest AS go-judge 

FROM debian:latest

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    python3 \
    g++ \
    openjdk-17-jdk-headless \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt

COPY --from=go-judge /opt/go-judge /opt/mount.yaml /opt/

EXPOSE 5050/tcp 5051/tcp 5052/tcp

ENTRYPOINT ["./go-judge"]