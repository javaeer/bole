#!/bin/bash
set -e

echo "=== 创建PostgreSQL用户和数据库 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    echo "等待PostgreSQL启动..."
    sleep 10
done

echo "PostgreSQL已启动，开始创建用户和数据库..."

# 创建用户和数据库
psql -v ON_ERROR_STOP=1 -U postgres <<-EOSQL
    -- 创建应用用户
    CREATE USER bole WITH PASSWORD 'bole123';
    CREATE USER readonly WITH PASSWORD 'readonly123';
    CREATE USER backup WITH PASSWORD 'backup123';

    -- 创建数据库
    CREATE DATABASE bole WITH
        OWNER = bole
        ENCODING = 'UTF8'
        LC_COLLATE = 'en_US.utf8'
        LC_CTYPE = 'en_US.utf8'
        TEMPLATE = template0;

    CREATE DATABASE backup WITH
        OWNER = backup
        ENCODING = 'UTF8'
        LC_COLLATE = 'en_US.utf8'
        LC_CTYPE = 'en_US.utf8'
        TEMPLATE = template0;

    -- 授予权限
    GRANT ALL PRIVILEGES ON DATABASE bole TO bole;
    GRANT ALL PRIVILEGES ON DATABASE backup TO backup;

    -- 为readonly用户授予只读权限
    GRANT CONNECT ON DATABASE bole TO readonly;
EOSQL

echo "用户和数据库创建完成"