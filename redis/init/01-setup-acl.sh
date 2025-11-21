#!/bin/bash
set -e

echo "=== 设置Redis ACL用户 ==="

# 等待Redis启动
until redis-cli -h localhost -p 6379 -a 12345678 ping > /dev/null 2>&1; do
    echo "等待Redis启动..."
    sleep 2
done

echo "Redis已启动，开始配置ACL用户..."

# 创建ACL文件
cat > /usr/local/etc/redis/users.acl <<EOF
# 默认用户关闭
user default off

# 管理员用户 - 所有权限
user admin on >admin123 ~* &* +@all

# 应用用户 - 读写权限，无管理权限
user bole on >bole123 ~* &* +@all -@admin -@dangerous

# 只读用户 - 只读权限
user readonly on >readonly123 ~* &* +@read +@pubsub +@connection

# Web应用用户 - 特定数据库权限
user webapp on >webapp123 ~bole:* ~myapp:* &* +@read +@write +@pubsub

# 备份用户 - 只读和备份权限
user backup on >backup123 ~* &* +@read +@keyspace
EOF

# 重新加载ACL配置
redis-cli -h localhost -p 6379 -a 12345678 ACL LOAD

echo "ACL用户配置完成"