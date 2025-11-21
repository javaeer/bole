#!/bin/bash
set -e

echo "=== 初始化数据库表结构 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    sleep 2
done

echo "初始化bole数据库表结构..."

# 初始化bole数据库
psql -v ON_ERROR_STOP=1 -U bole -d bole <<-EOSQL
    -- 创建schema
    CREATE SCHEMA IF NOT EXISTS bole_app;
    CREATE SCHEMA IF NOT EXISTS bole_audit;

    -- 设置搜索路径
    ALTER DATABASE bole SET search_path TO bole_app, public;

    -- 用户表
    CREATE TABLE IF NOT EXISTS bole_app.users (
        id SERIAL PRIMARY KEY,
        uuid UUID DEFAULT uuid_generate_v4(),
        username VARCHAR(50) UNIQUE NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        full_name VARCHAR(100),
        status VARCHAR(20) DEFAULT 'active',
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        last_login TIMESTAMP WITH TIME ZONE
    );

    -- 用户配置表
    CREATE TABLE IF NOT EXISTS bole_app.user_profiles (
        user_id INTEGER PRIMARY KEY REFERENCES bole_app.users(id),
        preferences JSONB DEFAULT '{}',
        metadata JSONB DEFAULT '{}',
        created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

    -- 系统配置表
    CREATE TABLE IF NOT EXISTS bole_app.system_config (
        config_key VARCHAR(100) PRIMARY KEY,
        config_value JSONB NOT NULL,
        description TEXT,
        updated_by VARCHAR(50),
        updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

    -- 审计日志表
    CREATE TABLE IF NOT EXISTS bole_audit.audit_logs (
        id BIGSERIAL PRIMARY KEY,
        table_name VARCHAR(100) NOT NULL,
        record_id INTEGER NOT NULL,
        action VARCHAR(10) NOT NULL,
        old_data JSONB,
        new_data JSONB,
        changed_by VARCHAR(50),
        changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_users_username ON bole_app.users(username);
    CREATE INDEX IF NOT EXISTS idx_users_email ON bole_app.users(email);
    CREATE INDEX IF NOT EXISTS idx_users_status ON bole_app.users(status);
    CREATE INDEX IF NOT EXISTS idx_audit_logs_changed_at ON bole_audit.audit_logs(changed_at);
    CREATE INDEX IF NOT EXISTS idx_audit_logs_table_record ON bole_audit.audit_logs(table_name, record_id);

    -- 授予权限给webapp用户
    GRANT USAGE ON SCHEMA bole_app TO webapp;
    GRANT USAGE ON SCHEMA bole_audit TO webapp;
    GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA bole_app TO webapp;
    GRANT SELECT ON ALL TABLES IN SCHEMA bole_audit TO webapp;
    GRANT USAGE ON ALL SEQUENCES IN SCHEMA bole_app TO webapp;

    -- 授予只读权限
    GRANT USAGE ON SCHEMA bole_app TO readonly;
    GRANT USAGE ON SCHEMA bole_audit TO readonly;
    GRANT SELECT ON ALL TABLES IN SCHEMA bole_app TO readonly;
    GRANT SELECT ON ALL TABLES IN SCHEMA bole_audit TO readonly;
EOSQL
echo "数据库表结构初始化完成"