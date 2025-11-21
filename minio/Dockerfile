# 使用官方 MinIO 镜像
FROM minio/minio:latest

# 设置中国时区
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制mc (MinIO Client) 用于初始化脚本
COPY mc /usr/bin/

# 设置权限
RUN chmod +x /usr/bin/mc

# 复制初始化脚本
COPY init/ /docker-entrypoint-init.d/

# 设置权限
RUN chmod -R +x /docker-entrypoint-init.d/

# 使用自定义启动脚本
COPY minio-start.sh /usr/bin/minio-start.sh
RUN chmod +x /usr/bin/minio-start.sh

# 设置健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD mc ready local/minio || exit 1

ENTRYPOINT ["/usr/bin/minio-start.sh"]