# 使用官方 Redis 镜像
FROM redis:7.2-alpine

# 设置中国时区
RUN apk add --no-cache tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 复制Redis配置文件
COPY redis.conf /usr/local/etc/redis/redis.conf

# 复制初始化脚本
COPY init/ /docker-entrypoint-init.d/

# 设置权限
RUN chmod -R +x /docker-entrypoint-init.d/ && \
    mkdir -p /var/log/redis && \
    chown redis:redis /var/log/redis

# 使用自定义配置文件启动
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]