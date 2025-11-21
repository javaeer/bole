#!/bin/bash
set -e

echo "=== 初始化Redis数据 ==="

# 等待Redis启动
until redis-cli -h localhost -p 6379 -a 12345678 ping > /dev/null 2>&1; do
    echo "等待Redis启动..."
    sleep 2
done

echo "开始初始化数据..."

# 使用应用用户初始化数据
redis-cli -h localhost -p 6379 -a yunlou123 <<EOF

# 设置系统配置
SET system:name "Bole CV"
SET system:version "1.0.0"
SET system:start_time "$(date +%s)"

# 初始化用户会话数据
HSET user:session:1001 username "张三" login_time "$(date +%s)" last_active "$(date +%s)"
HSET user:session:1002 username "李四" login_time "$(date +%s)" last_active "$(date +%s)"

# 初始化应用数据
SADD app:categories "电子产品" "家居用品" "图书文具"
ZADD app:hot_products 100 "笔记本电脑" 80 "智能手机" 60 "平板电脑"

# 初始化缓存数据
SET cache:homepage:data '{"banners":[],"products":[]}' EX 3600
SET cache:config:global '{"site_name":"云楼商城","theme":"default"}' EX 86400

# 初始化消息队列
LPUSH queue:emails '{"to":"user1@example.com","subject":"欢迎邮件"}'
LPUSH queue:notifications '{"user_id":1001,"message":"系统通知"}'

# 设置统计信息
HMSET stats:daily date "$(date +%Y-%m-%d)" visitors 0 orders 0 revenue 0.0

echo "Redis数据初始化完成"
EOF