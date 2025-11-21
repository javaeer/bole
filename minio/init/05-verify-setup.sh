#!/bin/bash
set -e

echo "=== 验证 MinIO 设置 ==="

# 等待mc就绪
until mc ready local/minio 2>/dev/null; do
    sleep 2
done

# 配置 alias
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD

echo "1. 验证存储桶..."
mc ls local/

echo ""
echo "2. 验证存储桶策略..."
for bucket in $(mc ls local/ | awk '{print $5}'); do
    echo "存储桶: $bucket"
    mc anonymous get local/$bucket || echo "  无法获取策略"
done

echo ""
echo "3. 验证用户..."
mc admin user list local

echo ""
echo "4. 验证服务状态..."
mc admin info local

echo ""
echo "=== MinIO 验证完成 ==="
echo "✅ 所有验证步骤已完成"