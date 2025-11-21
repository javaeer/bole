#!/bin/sh
set -e

echo "=== 启动 MinIO 服务 ==="

# 设置默认环境变量
export MINIO_ROOT_USER="${MINIO_ROOT_USER:-admin}"
export MINIO_ROOT_PASSWORD="${MINIO_ROOT_PASSWORD:-admin123}"
export MINIO_API_PORT="${MINIO_API_PORT:-9000}"
export MINIO_CONSOLE_PORT="${MINIO_CONSOLE_PORT:-9001}"

# 显示配置信息
echo "MinIO 配置:"
echo "- Root User: $MINIO_ROOT_USER"
echo "- API Port: $MINIO_API_PORT"
echo "- Console Port: $MINIO_CONSOLE_PORT"
echo "- Data Directory: /data"

# 启动 MinIO 服务器（后台运行）
echo "启动 MinIO 服务器..."
minio server /data \
    --address ":$MINIO_API_PORT" \
    --console-address ":$MINIO_CONSOLE_PORT" &

# 等待 MinIO 启动
echo "等待 MinIO 启动..."
until mc ready local/minio 2>/dev/null; do
    echo "MinIO 尚未就绪，等待 2 秒..."
    sleep 2
done

echo "MinIO 服务器已启动!"

# 执行初始化脚本
if [ -d "/docker-entrypoint-init.d" ]; then
    for script in /docker-entrypoint-init.d/*.sh; do
        if [ -f "$script" ] && [ -x "$script" ]; then
            echo "执行初始化脚本: $script"
            "$script"
        fi
    done
fi

echo "=== MinIO 初始化完成 ==="

# 等待所有后台进程
wait