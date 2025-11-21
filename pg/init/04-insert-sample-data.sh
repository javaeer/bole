#!/bin/bash
set -e

echo "=== 插入示例数据 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    sleep 2
done

echo "插入bole数据库示例数据..."

# 插入bole数据库示例数据
psql -v ON_ERROR_STOP=1 -U bole -d bole <<-EOSQL
    -- 插入用户数据
    INSERT INTO bole_app.users (username, email, password_hash, full_name) VALUES
    ('admin', 'admin@bole.com', crypt('admin123', gen_salt('bf')), '系统管理员'),
    ('zhangsan', 'zhangsan@bole.com', crypt('user123', gen_salt('bf')), '张三'),
    ('lisi', 'lisi@bole.com', crypt('user123', gen_salt('bf')), '李四'),
    ('wangwu', 'wangwu@bole.com', crypt('user123', gen_salt('bf')), '王五')
    ON CONFLICT (username) DO NOTHING;

    -- 插入用户配置
    INSERT INTO bole_app.user_profiles (user_id, preferences) VALUES
    (1, '{"theme": "dark", "language": "zh-CN", "notifications": true}'),
    (2, '{"theme": "light", "language": "zh-CN", "notifications": false}'),
    (3, '{"theme": "auto", "language": "en-US", "notifications": true}'),
    (4, '{"theme": "light", "language": "zh-CN", "notifications": true}')
    ON CONFLICT (user_id) DO NOTHING;

    -- 插入系统配置
    INSERT INTO bole_app.system_config (config_key, config_value, description) VALUES
    ('app.name', '"云楼生活"', '应用名称'),
    ('app.version', '"1.0.0"', '应用版本'),
    ('security.password_min_length', '8', '密码最小长度'),
    ('ui.default_theme', '"light"', '默认主题')
    ON CONFLICT (config_key) DO NOTHING;
EOSQL

echo "示例数据插入完成"