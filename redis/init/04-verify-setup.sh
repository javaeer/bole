#!/bin/bash
set -e

echo "=== 验证Redis设置 ==="

# 等待Redis启动
until redis-cli -h localhost -p 6379 -a 12345678 ping > /dev/null 2>&1; do
    echo "等待Redis启动..."
    sleep 2
done

echo "1. 测试连接..."
redis-cli -h localhost -p 6379 -a 12345678 ping

echo ""
echo "2. 验证ACL用户..."
redis-cli -h localhost -p 6379 -a admin123 ACL LIST

echo ""
echo "3. 验证数据初始化..."
echo "   系统名称: $(redis-cli -h localhost -p 6379 -a bole123 GET system:name)"
echo "   用户会话: $(redis-cli -h localhost -p 6379 -a bole123 HGETALL user:session:1001)"
echo "   应用分类: $(redis-cli -h localhost -p 6379 -a bole123 SMEMBERS app:categories)"
echo "   热门商品: $(redis-cli -h localhost -p 6379 -a bole123 ZRANGE app:hot_products 0 -1 WITHSCORES)"

echo ""
echo "4. 测试不同用户权限..."
echo "   管理员权限测试:"
redis-cli -h localhost -p 6379 -a admin123 CLIENT LIST | head -2

echo "   应用用户权限测试:"
redis-cli -h localhost -p 6379 -a bole123 GET system:name

echo "   只读用户权限测试:"
redis-cli -h localhost -p 6379 -a readonly123 GET system:name

echo ""
echo "5. 检查内存和信息..."
redis-cli -h localhost -p 6379 -a admin123 INFO memory | grep -E "(used_memory|maxmemory)"
redis-cli -h localhost -p 6379 -a admin123 INFO keyspace

echo ""
echo "=== Redis验证完成 ==="
echo "✅ 所有验证步骤已完成"