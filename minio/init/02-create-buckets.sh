#!/bin/bash
set -e

echo "=== 创建存储桶 ==="

# 等待mc就绪
until mc ready local/minio 2>/dev/null; do
    sleep 2
done

# 配置 alias
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD

# 存储桶列表
buckets=("bole" "backup" "logs" "temp")

for bucket in "${buckets[@]}"; do
    if mc ls local/"$bucket" >/dev/null 2>&1; then
        echo "✅ 存储桶 '$bucket' 已存在"
    else
        mc mb local/"$bucket"
        echo "✅ 创建存储桶: $bucket"
    fi
done

echo "存储桶创建完成"