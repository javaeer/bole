#!/bin/bash
set -e

echo "=== 验证PostgreSQL设置 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    echo "等待PostgreSQL启动..."
    sleep 2
done

echo "1. 测试PostgreSQL连接..."
pg_isready -U postgres && echo "✅ PostgreSQL连接正常"

echo ""
echo "2. 验证数据库创建..."
psql -U postgres -c "SELECT datname FROM pg_database WHERE datistemplate = false;" | grep -E "(bole|myapp|backup)"

echo ""
echo "3. 验证用户创建..."
psql -U postgres -c "SELECT usename FROM pg_user WHERE usename NOT LIKE 'postgres%';"

echo ""
echo "4. 验证bole数据库表结构..."
psql -U bole -d bole -c "
SELECT table_schema, table_name
FROM information_schema.tables
WHERE table_schema IN ('bole_app', 'bole_audit')
ORDER BY table_schema, table_name;"

echo ""
echo "5. 验证数据插入..."
echo "   bole.t_user 表记录数:"
psql -U bole -d bole -c "SELECT COUNT(*) FROM bole_app.t_user;"

echo ""
echo "6. 测试用户权限..."
echo "   readonly用户访问测试:"
psql -U readonly -d bole -c "SELECT username FROM bole_app.t_user LIMIT 1;" > /dev/null && echo "✅ readonly用户权限正常"

echo ""
echo "8. 检查扩展..."
psql -U postgres -d bole -c "SELECT extname, extversion FROM pg_extension;"

echo ""
echo "=== PostgreSQL验证完成 ==="
echo "✅ 所有验证步骤已完成"