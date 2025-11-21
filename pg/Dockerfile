# 使用官方 PostgreSQL 镜像
FROM postgres:15-alpine

# 设置中国时区
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制自定义配置
COPY conf/postgresql.conf /etc/postgresql/
COPY conf/pg_hba.conf /etc/postgresql/

# 复制初始化脚本
COPY init/ /docker-entrypoint-initdb.d/

# 设置权限
RUN chmod -R 755 /docker-entrypoint-initdb.d/ && \
    chown -R postgres:postgres /docker-entrypoint-initdb.d/ && \
    mkdir -p /var/log/postgresql && \
    chown postgres:postgres /var/log/postgresql

# 设置数据目录权限
RUN chown -R postgres:postgres /var/lib/postgresql/data

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD pg_isready -U postgres -d postgres || exit 1

USER postgres