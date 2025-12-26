# 使用官方 PostgreSQL 镜像
FROM harbor.yunlou.net.cn/library/postgres:14

# 设置中国时区
ENV TZ=Asia/Shanghai

# 复制自定义配置
COPY conf/ /etc/postgresql/

# 复制初始化脚本
COPY init/ /docker-entrypoint-initdb.d/

# 设置权限
RUN chmod -R 755 /docker-entrypoint-initdb.d/ && \
    chown -R postgres:postgres /docker-entrypoint-initdb.d/ && \
    mkdir -p /var/log/postgresql && \
    chown postgres:postgres /var/log/postgresql

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD pg_isready -U postgres -d postgres || exit 1
