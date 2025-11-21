#!/bin/bash
set -e

echo "=== 设置存储桶策略 ==="

# 等待mc就绪
until mc ready local/minio 2>/dev/null; do
    sleep 2
done

# 配置 alias
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD

# 设置存储桶策略
echo "设置存储桶策略..."

# bole - 私有读写
mc anonymous set download local/bole
echo "✅ bole 桶设置为下载权限"

# backup - 备份数据，私有
mc anonymous set private local/backup
echo "✅ backup 桶设置为私有"

# logs - 日志文件，只读
mc anonymous set download local/logs
echo "✅ logs 桶设置为下载权限"

# temp - 临时文件，公共读写（7天生命周期）
mc anonymous set public local/temp
echo "✅ temp 桶设置为公共读写"

echo "存储桶策略设置完成"