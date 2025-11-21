#!/bin/bash
set -e

echo "=== 配置 MinIO Client ==="

# 配置 mc alias
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD

echo "MinIO Client 配置完成"