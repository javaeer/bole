#!/bin/bash
set -e

echo "=== 创建数据库扩展 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    sleep 2
done

echo "创建数据库扩展..."

# 在bole数据库创建扩展
psql -v ON_ERROR_STOP=1 -U postgres -d bole <<-EOSQL
    -- 创建常用扩展
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    CREATE EXTENSION IF NOT EXISTS "pgcrypto";
    CREATE EXTENSION IF NOT EXISTS "pg_stat_statements";
    CREATE EXTENSION IF NOT EXISTS "btree_gin";
    CREATE EXTENSION IF NOT EXISTS "btree_gist";

    -- 授予扩展权限给应用用户
    GRANT USAGE ON SCHEMA public TO bole;
EOSQL

echo "数据库扩展创建完成"