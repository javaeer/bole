#!/bin/bash
set -e

echo "=== 设置键空间策略 ==="

# 等待Redis启动
until redis-cli -h localhost -p 6379 -a 12345678 ping > /dev/null 2>&1; do
    echo "等待Redis启动..."
    sleep 2
done

echo "设置键过期策略..."

# 使用管理员设置键策略
redis-cli -h localhost -p 6379 -a admin123 <<EOF

# 设置会话数据过期时间
EXPIRE user:session:1001 86400
EXPIRE user:session:1002 86400

# 设置缓存数据过期时间
EXPIRE cache:homepage:data 3600
EXPIRE cache:config:global 86400

# 创建定时任务键
SET cron:daily_backup "pending" EX 86300
SET cron:hourly_cleanup "pending" EX 3590

# 设置计数器
SET counter:page_views 0
SET counter:api_requests 0

echo "键空间策略设置完成"
EOF