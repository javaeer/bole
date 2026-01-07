#!/bin/bash
set -e

echo "=== 初始化数据库表结构 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    echo "等待 PostgreSQL 启动..."
    sleep 10
done

echo "初始化bole数据库表结构..."

# 检查数据库是否存在
if ! psql -U postgres -lqt | cut -d \| -f 1 | grep -qw bole; then
    echo "错误：数据库 bole 不存在"
    exit 1
fi

# 初始化bole数据库
psql -v ON_ERROR_STOP=1 -U bole -d bole <<-'EOSQL'
    -- 创建schema
    CREATE SCHEMA IF NOT EXISTS bole_app;
    CREATE SCHEMA IF NOT EXISTS bole_audit;

    -- 设置搜索路径
    ALTER DATABASE bole SET search_path TO bole_app, public;


    -- 系统配置表
    CREATE TABLE IF NOT EXISTS bole_app.t_config (
        -- 主键字段（使用字符串作为主键）
        config_key VARCHAR(255) PRIMARY KEY,
        config_value TEXT,
        config_desc VARCHAR(500),
        
        -- 审计字段
        created_by_id BIGINT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_by_id BIGINT,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_config_created_at ON bole_app.t_config(created_at);
    CREATE INDEX IF NOT EXISTS idx_config_updated_at ON bole_app.t_config(updated_at);
    CREATE INDEX IF NOT EXISTS idx_config_created_by_id ON bole_app.t_config(created_by_id);
    CREATE INDEX IF NOT EXISTS idx_config_updated_by_id ON bole_app.t_config(updated_by_id);

    -- 表注释和字段注释
    COMMENT ON TABLE bole_app.t_config IS '系统配置表';
    COMMENT ON COLUMN bole_app.t_config.config_key IS '配置键（主键）';
    COMMENT ON COLUMN bole_app.t_config.config_value IS '配置值';
    COMMENT ON COLUMN bole_app.t_config.config_desc IS '配置描述';
    COMMENT ON COLUMN bole_app.t_config.created_by_id IS '创建人ID';
    COMMENT ON COLUMN bole_app.t_config.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_config.updated_by_id IS '更新人ID';
    COMMENT ON COLUMN bole_app.t_config.updated_at IS '更新时间';

    -- 系统横幅表
    CREATE TABLE IF NOT EXISTS bole_app.t_banner (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        image VARCHAR(500),
        path VARCHAR(500),
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_banner_sort ON bole_app.t_banner(sort);
    CREATE INDEX IF NOT EXISTS idx_banner_created_at ON bole_app.t_banner(created_at);
    CREATE INDEX IF NOT EXISTS idx_banner_deleted ON bole_app.t_banner(deleted);

    -- 表注释和字段注释
    COMMENT ON TABLE bole_app.t_banner IS '系统横幅表';
    COMMENT ON COLUMN bole_app.t_banner.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_banner.name IS '横幅名称';
    COMMENT ON COLUMN bole_app.t_banner.image IS '图片路径/URL';
    COMMENT ON COLUMN bole_app.t_banner.path IS '跳转路径';
    COMMENT ON COLUMN bole_app.t_banner.sort IS '排序号（数字越小越靠前）';
    COMMENT ON COLUMN bole_app.t_banner.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_banner.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_banner.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

-- 字典表
    CREATE TABLE IF NOT EXISTS bole_app.t_dict (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        parent_id BIGINT DEFAULT 0,
        path VARCHAR(255),
        level INTEGER DEFAULT 1,
        name VARCHAR(255) NOT NULL,
        type VARCHAR(100),
        code VARCHAR(100),
        value VARCHAR(500),
        label VARCHAR(500),
        state INTEGER DEFAULT 1,
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_dict_parent_id ON bole_app.t_dict(parent_id);
    CREATE INDEX IF NOT EXISTS idx_dict_level ON bole_app.t_dict(level);
    CREATE INDEX IF NOT EXISTS idx_dict_type ON bole_app.t_dict(type);
    CREATE INDEX IF NOT EXISTS idx_dict_code ON bole_app.t_dict(code);
    CREATE INDEX IF NOT EXISTS idx_dict_state ON bole_app.t_dict(state);
    CREATE INDEX IF NOT EXISTS idx_dict_created_at ON bole_app.t_dict(created_at);

    -- 表注释和字段注释
    COMMENT ON TABLE bole_app.t_dict IS '字典表';
    COMMENT ON COLUMN bole_app.t_dict.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_dict.parent_id IS '父节点ID';
    COMMENT ON COLUMN bole_app.t_dict.path IS '路径';
    COMMENT ON COLUMN bole_app.t_dict.level IS '层级';
    COMMENT ON COLUMN bole_app.t_dict.name IS '字典名称';
    COMMENT ON COLUMN bole_app.t_dict.type IS '字典类型';
    COMMENT ON COLUMN bole_app.t_dict.code IS '字典编码';
    COMMENT ON COLUMN bole_app.t_dict.value IS '字典值';
    COMMENT ON COLUMN bole_app.t_dict.label IS '字典标签';
    COMMENT ON COLUMN bole_app.t_dict.state IS '状态（1启用，0停用）';
    COMMENT ON COLUMN bole_app.t_dict.sort IS '排序号';
    COMMENT ON COLUMN bole_app.t_dict.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_dict.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_dict.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 创建用户表
    CREATE TABLE IF NOT EXISTS bole_app.t_user (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 基础信息字段
        company_id BIGINT,
        gender INTEGER DEFAULT 0,
        username VARCHAR(100),
        password VARCHAR(255),
        email VARCHAR(100),
        phone VARCHAR(20),
        name VARCHAR(100),
        avatar TEXT,
        
        -- 职业信息字段
        title VARCHAR(100),
        location VARCHAR(100),
        website VARCHAR(255),
        github VARCHAR(100),
        wechat VARCHAR(50),
        wechat_open_id VARCHAR(50),
        wechat_union_id VARCHAR(50),

        -- 个人简介字段
        bio TEXT,
        
        -- 统计字段
        followers INTEGER DEFAULT 0,
        fans INTEGER DEFAULT 0,
        likes INTEGER DEFAULT 0,
        
        -- 状态字段
        status INTEGER DEFAULT 1,
        work_years INTEGER DEFAULT 0,
        
        -- 时间字段
        last_login_at TIMESTAMP,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_user_username ON bole_app.t_user(username);
    CREATE INDEX IF NOT EXISTS idx_user_phone ON bole_app.t_user(phone);
    CREATE INDEX IF NOT EXISTS idx_user_email ON bole_app.t_user(email);
    CREATE INDEX IF NOT EXISTS idx_user_company ON bole_app.t_user(company_id);
    CREATE INDEX IF NOT EXISTS idx_user_wechat_open_id ON bole_app.t_user(wechat_open_id);
    CREATE INDEX IF NOT EXISTS idx_user_status ON bole_app.t_user(status);
    CREATE INDEX IF NOT EXISTS idx_user_created_at ON bole_app.t_user(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_user IS '用户表';
    COMMENT ON COLUMN bole_app.t_user.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_user.gender IS '性别';
    COMMENT ON COLUMN bole_app.t_user.company_id IS '当前所在企业ID';
    COMMENT ON COLUMN bole_app.t_user.username IS '用户名';
    COMMENT ON COLUMN bole_app.t_user.password IS '密码';
    COMMENT ON COLUMN bole_app.t_user.email IS '邮箱';
    COMMENT ON COLUMN bole_app.t_user.phone IS '手机号';
    COMMENT ON COLUMN bole_app.t_user.name IS '姓名';
    COMMENT ON COLUMN bole_app.t_user.avatar IS '头像URL';
    COMMENT ON COLUMN bole_app.t_user.title IS '职位/头衔';
    COMMENT ON COLUMN bole_app.t_user.location IS '所在地';
    COMMENT ON COLUMN bole_app.t_user.website IS '个人网站';
    COMMENT ON COLUMN bole_app.t_user.github IS 'GitHub账号';
    COMMENT ON COLUMN bole_app.t_user.wechat IS '微信号';
    COMMENT ON COLUMN bole_app.t_user.wechat_open_id IS '微信OPENID';
    COMMENT ON COLUMN bole_app.t_user.wechat_union_id IS '微信UNIONID';
    COMMENT ON COLUMN bole_app.t_user.bio IS '个人简介';
    COMMENT ON COLUMN bole_app.t_user.followers IS '粉丝数';
    COMMENT ON COLUMN bole_app.t_user.fans IS '关注数';
    COMMENT ON COLUMN bole_app.t_user.likes IS '获赞数';
    COMMENT ON COLUMN bole_app.t_user.status IS '状态(0-禁用,1-正常)';
    COMMENT ON COLUMN bole_app.t_user.work_years IS '工作年限';
    COMMENT ON COLUMN bole_app.t_user.last_login_at IS '最后登录时间';
    COMMENT ON COLUMN bole_app.t_user.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_user.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_user.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建企业表
    CREATE TABLE IF NOT EXISTS bole_app.t_company (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 基础信息字段
        name VARCHAR(200) NOT NULL,
        email VARCHAR(100),
        holder VARCHAR(100),
        
        -- 联系信息字段
        location VARCHAR(100),
        website VARCHAR(255),
        github VARCHAR(100),
        wechat VARCHAR(50),

        -- 简介字段
        bio TEXT,

        -- 统计字段
        followers INTEGER DEFAULT 0,
        fans INTEGER DEFAULT 0,
        likes INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_company_name ON bole_app.t_company(name);
    CREATE INDEX IF NOT EXISTS idx_company_email ON bole_app.t_company(email);
    CREATE INDEX IF NOT EXISTS idx_company_holder ON bole_app.t_company(holder);
    CREATE INDEX IF NOT EXISTS idx_company_created_at ON bole_app.t_company(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_company IS '企业表';
    COMMENT ON COLUMN bole_app.t_company.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_company.name IS '企业名称';
    COMMENT ON COLUMN bole_app.t_company.email IS '企业邮箱';
    COMMENT ON COLUMN bole_app.t_company.holder IS '企业法人/持有人';
    COMMENT ON COLUMN bole_app.t_company.location IS '企业所在地';
    COMMENT ON COLUMN bole_app.t_company.website IS '企业官网';
    COMMENT ON COLUMN bole_app.t_company.github IS 'GitHub账号';
    COMMENT ON COLUMN bole_app.t_company.wechat IS '微信公众号';
    COMMENT ON COLUMN bole_app.t_company.bio IS '企业简介';
    COMMENT ON COLUMN bole_app.t_company.followers IS '粉丝数';
    COMMENT ON COLUMN bole_app.t_company.fans IS '关注数';
    COMMENT ON COLUMN bole_app.t_company.likes IS '获赞数';
    COMMENT ON COLUMN bole_app.t_company.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_company.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_company.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建角色表
    CREATE TABLE IF NOT EXISTS bole_app.t_role (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 角色信息字段
        name VARCHAR(100) NOT NULL,
        code VARCHAR(50) NOT NULL UNIQUE,
        description TEXT,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_role_name ON bole_app.t_role(name);
    CREATE INDEX IF NOT EXISTS idx_role_code ON bole_app.t_role(code);
    CREATE INDEX IF NOT EXISTS idx_role_created_at ON bole_app.t_role(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_role IS '角色表';
    COMMENT ON COLUMN bole_app.t_role.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_role.name IS '角色名称';
    COMMENT ON COLUMN bole_app.t_role.code IS '角色代码(唯一)';
    COMMENT ON COLUMN bole_app.t_role.description IS '角色描述';
    COMMENT ON COLUMN bole_app.t_role.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_role.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_role.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建用户角色关联表
    CREATE TABLE IF NOT EXISTS bole_app.t_user_role (
        user_id BIGINT NOT NULL,
        role_id BIGINT NOT NULL,
        
        -- 外键约束
        CONSTRAINT fk_user_role_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE,
        
        CONSTRAINT fk_user_role_role 
            FOREIGN KEY (role_id) 
            REFERENCES bole_app.t_role(id) 
            ON DELETE CASCADE,
        
        -- 设置复合主键
        PRIMARY KEY (user_id, role_id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_user_role_user_id ON bole_app.t_user_role(user_id);
    CREATE INDEX IF NOT EXISTS idx_user_role_role_id ON bole_app.t_user_role(role_id);

    -- 注释
    COMMENT ON TABLE bole_app.t_user_role IS '用户角色关联表';
    COMMENT ON COLUMN bole_app.t_user_role.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_user_role.role_id IS '角色ID';

    -- 创建企业评论表
    CREATE TABLE IF NOT EXISTS bole_app.t_company_comment (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 树形结构字段
        parent_id BIGINT DEFAULT 0,
        path VARCHAR(500) DEFAULT '',
        level INTEGER DEFAULT 1,
        
        -- 企业关联字段
        company_id BIGINT NOT NULL,
        
        -- 评论内容字段
        content TEXT NOT NULL,
        
        -- 用户信息字段
        user_id BIGINT NOT NULL,
        user_name VARCHAR(100),
        user_avatar TEXT,
        
        -- 评分字段
        score INTEGER CHECK (score >= 1 AND score <= 5),
        
        -- 状态字段
        status INTEGER DEFAULT 0,
        sort INTEGER DEFAULT 0,
        
        -- 统计字段
        like_count INTEGER DEFAULT 0,
        reply_count INTEGER DEFAULT 0,
        
        -- 匿名标识
        anonymous BOOLEAN DEFAULT FALSE,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_comment_company 
            FOREIGN KEY (company_id) 
            REFERENCES bole_app.t_company(id) 
            ON DELETE CASCADE,
        
        CONSTRAINT fk_comment_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_comment_company ON bole_app.t_company_comment(company_id);
    CREATE INDEX IF NOT EXISTS idx_comment_user ON bole_app.t_company_comment(user_id);
    CREATE INDEX IF NOT EXISTS idx_comment_parent ON bole_app.t_company_comment(parent_id);
    CREATE INDEX IF NOT EXISTS idx_comment_status ON bole_app.t_company_comment(status);
    CREATE INDEX IF NOT EXISTS idx_comment_score ON bole_app.t_company_comment(score);
    CREATE INDEX IF NOT EXISTS idx_comment_created_at ON bole_app.t_company_comment(created_at);
    CREATE INDEX IF NOT EXISTS idx_comment_path ON bole_app.t_company_comment(path);
    CREATE INDEX IF NOT EXISTS idx_comment_level ON bole_app.t_company_comment(level);

    -- 注释
    COMMENT ON TABLE bole_app.t_company_comment IS '企业评论表';
    COMMENT ON COLUMN bole_app.t_company_comment.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_company_comment.parent_id IS '父评论ID(0-根评论)';
    COMMENT ON COLUMN bole_app.t_company_comment.path IS '评论路径(用于快速查询子树)';
    COMMENT ON COLUMN bole_app.t_company_comment.level IS '评论层级(1-一级评论,2-二级评论...)';
    COMMENT ON COLUMN bole_app.t_company_comment.company_id IS '企业ID';
    COMMENT ON COLUMN bole_app.t_company_comment.content IS '评论内容';
    COMMENT ON COLUMN bole_app.t_company_comment.user_id IS '评论人ID';
    COMMENT ON COLUMN bole_app.t_company_comment.user_name IS '评论人姓名(冗余字段)';
    COMMENT ON COLUMN bole_app.t_company_comment.user_avatar IS '评论人头像(冗余字段)';
    COMMENT ON COLUMN bole_app.t_company_comment.score IS '评分(1-5分)';
    COMMENT ON COLUMN bole_app.t_company_comment.status IS '评论状态(0-待审核,1-已发布,2-已删除)';
    COMMENT ON COLUMN bole_app.t_company_comment.sort IS '排序字段';
    COMMENT ON COLUMN bole_app.t_company_comment.like_count IS '点赞数';
    COMMENT ON COLUMN bole_app.t_company_comment.reply_count IS '回复数';
    COMMENT ON COLUMN bole_app.t_company_comment.anonymous IS '是否匿名(false-否,true-是)';
    COMMENT ON COLUMN bole_app.t_company_comment.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_company_comment.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_company_comment.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建公司经历表
    CREATE TABLE IF NOT EXISTS bole_app.t_company_experiences (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        company_id BIGINT NOT NULL,
        
        -- 职位信息字段
        position VARCHAR(200) NOT NULL,
        start_date DATE NOT NULL,
        end_date DATE,
        is_current BOOLEAN DEFAULT FALSE,
        
        -- 描述和链接字段
        description TEXT,
        website JSONB DEFAULT '[]'::jsonb,
        
        -- 排序字段
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_company_experience_company 
            FOREIGN KEY (company_id) 
            REFERENCES bole_app.t_company(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_company_exp_company ON bole_app.t_company_experiences(company_id);
    CREATE INDEX IF NOT EXISTS idx_company_exp_dates ON bole_app.t_company_experiences(start_date, end_date);
    CREATE INDEX IF NOT EXISTS idx_company_exp_current ON bole_app.t_company_experiences(is_current);
    CREATE INDEX IF NOT EXISTS idx_company_exp_sort ON bole_app.t_company_experiences(sort);
    CREATE INDEX IF NOT EXISTS idx_company_exp_created_at ON bole_app.t_company_experiences(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_company_experiences IS '公司经历表';
    COMMENT ON COLUMN bole_app.t_company_experiences.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_company_experiences.company_id IS '公司ID';
    COMMENT ON COLUMN bole_app.t_company_experiences.position IS '职位名称';
    COMMENT ON COLUMN bole_app.t_company_experiences.start_date IS '开始日期';
    COMMENT ON COLUMN bole_app.t_company_experiences.end_date IS '结束日期(为空表示至今)';
    COMMENT ON COLUMN bole_app.t_company_experiences.is_current IS '是否当前职位';
    COMMENT ON COLUMN bole_app.t_company_experiences.description IS '经历描述';
    COMMENT ON COLUMN bole_app.t_company_experiences.website IS '相关网站链接数组';
    COMMENT ON COLUMN bole_app.t_company_experiences.sort IS '排序字段';
    COMMENT ON COLUMN bole_app.t_company_experiences.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_company_experiences.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_company_experiences.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 求职意向表
    CREATE TABLE IF NOT EXISTS bole_app.t_job_intention (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        position VARCHAR(255),
        city VARCHAR(255),
        salary VARCHAR(100),
        job_type VARCHAR(100),
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束（如果需要关联用户表）
        -- CONSTRAINT fk_job_intention_user FOREIGN KEY (user_id) REFERENCES bole_app.t_user(id)
        
        -- 唯一约束（每个用户最多一条意向记录，根据业务需求可选）
        -- CONSTRAINT uk_user_job_intention UNIQUE (user_id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_job_intention_user_id ON bole_app.t_job_intention(user_id);
    CREATE INDEX IF NOT EXISTS idx_job_intention_position ON bole_app.t_job_intention(position);
    CREATE INDEX IF NOT EXISTS idx_job_intention_city ON bole_app.t_job_intention(city);
    CREATE INDEX IF NOT EXISTS idx_job_intention_job_type ON bole_app.t_job_intention(job_type);
    CREATE INDEX IF NOT EXISTS idx_job_intention_created_at ON bole_app.t_job_intention(created_at);
    CREATE INDEX IF NOT EXISTS idx_job_intention_deleted ON bole_app.t_job_intention(deleted);

    -- 注释
    COMMENT ON TABLE bole_app.t_job_intention IS '求职意向表';
    COMMENT ON COLUMN bole_app.t_job_intention.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_job_intention.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_job_intention.position IS '职位意向';
    COMMENT ON COLUMN bole_app.t_job_intention.city IS '期望城市';
    COMMENT ON COLUMN bole_app.t_job_intention.salary IS '期望薪资';
    COMMENT ON COLUMN bole_app.t_job_intention.job_type IS '工作类型（全职/兼职/实习等）';
    COMMENT ON COLUMN bole_app.t_job_intention.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_job_intention.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_job_intention.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 创建教育经历表
    CREATE TABLE IF NOT EXISTS bole_app.t_education_experience (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        user_id BIGINT NOT NULL,
        university_id BIGINT,

        -- 教育信息字段
        university VARCHAR(200) NOT NULL,
        major VARCHAR(100),
        degree VARCHAR(50),
        
        -- 时间字段
        start_date DATE,
        end_date DATE,
        
        -- 状态字段
        is_highest INTEGER DEFAULT 0,
        sort INTEGER DEFAULT 0,
        
        -- 描述和成就字段
        description TEXT,
        achievements JSONB,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_education_experience_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_education_experience_user_id ON bole_app.t_education_experience(user_id);
    CREATE INDEX IF NOT EXISTS idx_education_experience_university ON bole_app.t_education_experience(university);
    CREATE INDEX IF NOT EXISTS idx_education_experience_degree ON bole_app.t_education_experience(degree);
    CREATE INDEX IF NOT EXISTS idx_education_experience_start_date ON bole_app.t_education_experience(start_date);
    CREATE INDEX IF NOT EXISTS idx_education_experience_is_highest ON bole_app.t_education_experience(is_highest);
    CREATE INDEX IF NOT EXISTS idx_education_experience_created_at ON bole_app.t_education_experience(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_education_experience IS '教育经历表';
    COMMENT ON COLUMN bole_app.t_education_experience.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_education_experience.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_education_experience.university_id IS '学校ID';
    COMMENT ON COLUMN bole_app.t_education_experience.university IS '学校名称';
    COMMENT ON COLUMN bole_app.t_education_experience.major IS '专业';
    COMMENT ON COLUMN bole_app.t_education_experience.degree IS '学位(如:本科,硕士,博士)';
    COMMENT ON COLUMN bole_app.t_education_experience.start_date IS '开始日期';
    COMMENT ON COLUMN bole_app.t_education_experience.end_date IS '结束日期';
    COMMENT ON COLUMN bole_app.t_education_experience.is_highest IS '是否为最高学历(0-否,1-是)';
    COMMENT ON COLUMN bole_app.t_education_experience.sort IS '排序字段(数字越小越靠前)';
    COMMENT ON COLUMN bole_app.t_education_experience.description IS '经历描述';
    COMMENT ON COLUMN bole_app.t_education_experience.achievements IS '成就列表(JSON数组)';
    COMMENT ON COLUMN bole_app.t_education_experience.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_education_experience.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_education_experience.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建项目经验表
    CREATE TABLE IF NOT EXISTS bole_app.t_project_experience (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        user_id BIGINT NOT NULL,
        
        -- 项目基本信息
        name VARCHAR(200) NOT NULL,
        status INTEGER DEFAULT 1,
        start_date DATE,
        end_date DATE,
        
        -- 项目属性
        is_highest INTEGER DEFAULT 0,
        description TEXT,
        achievements JSONB,
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_project_experience_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_project_experience_user_id ON bole_app.t_project_experience(user_id);
    CREATE INDEX IF NOT EXISTS idx_project_experience_status ON bole_app.t_project_experience(status);
    CREATE INDEX IF NOT EXISTS idx_project_experience_start_date ON bole_app.t_project_experience(start_date);
    CREATE INDEX IF NOT EXISTS idx_project_experience_end_date ON bole_app.t_project_experience(end_date);
    CREATE INDEX IF NOT EXISTS idx_project_experience_is_highest ON bole_app.t_project_experience(is_highest);
    CREATE INDEX IF NOT EXISTS idx_project_experience_sort ON bole_app.t_project_experience(sort);
    CREATE INDEX IF NOT EXISTS idx_project_experience_created_at ON bole_app.t_project_experience(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_project_experience IS '项目经验表';
    COMMENT ON COLUMN bole_app.t_project_experience.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_project_experience.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_project_experience.name IS '项目名称';
    COMMENT ON COLUMN bole_app.t_project_experience.status IS '项目状态(0-进行中,1-已完成)';
    COMMENT ON COLUMN bole_app.t_project_experience.start_date IS '开始日期';
    COMMENT ON COLUMN bole_app.t_project_experience.end_date IS '结束日期';
    COMMENT ON COLUMN bole_app.t_project_experience.is_highest IS '是否为最高成就项目(0-否,1-是)';
    COMMENT ON COLUMN bole_app.t_project_experience.description IS '项目描述';
    COMMENT ON COLUMN bole_app.t_project_experience.achievements IS '项目成果(JSON数组)';
    COMMENT ON COLUMN bole_app.t_project_experience.sort IS '排序字段';
    COMMENT ON COLUMN bole_app.t_project_experience.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_project_experience.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_project_experience.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建工作经历表
    CREATE TABLE IF NOT EXISTS bole_app.t_work_experiences (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        user_id BIGINT NOT NULL,
        company_id BIGINT NOT NULL,
        company VARCHAR(200) NOT NULL,
        
        -- 工作信息字段
        position VARCHAR(200) NOT NULL,
        start_date DATE NOT NULL,
        end_date DATE,
        is_current BOOLEAN DEFAULT FALSE,
        description TEXT,
        
        -- 成就字段（JSON数组存储）
        achievements JSONB,
        
        -- 排序字段
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_work_exp_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE,
        
        CONSTRAINT fk_work_exp_company 
            FOREIGN KEY (company_id) 
            REFERENCES bole_app.t_company(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_work_exp_user_id ON bole_app.t_work_experiences(user_id);
    CREATE INDEX IF NOT EXISTS idx_work_exp_company_id ON bole_app.t_work_experiences(company_id);
    CREATE INDEX IF NOT EXISTS idx_work_exp_dates ON bole_app.t_work_experiences(start_date, end_date);
    CREATE INDEX IF NOT EXISTS idx_work_exp_is_current ON bole_app.t_work_experiences(is_current);
    CREATE INDEX IF NOT EXISTS idx_work_exp_sort ON bole_app.t_work_experiences(sort);
    CREATE INDEX IF NOT EXISTS idx_work_exp_created_at ON bole_app.t_work_experiences(created_at);

    -- 创建GIN索引用于JSONB字段的快速查询
    CREATE INDEX IF NOT EXISTS idx_work_exp_achievements ON bole_app.t_work_experiences USING GIN (achievements);

    -- 注释
    COMMENT ON TABLE bole_app.t_work_experiences IS '工作经历表';
    COMMENT ON COLUMN bole_app.t_work_experiences.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_work_experiences.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_work_experiences.company_id IS '公司ID';
    COMMENT ON COLUMN bole_app.t_work_experiences.company IS '公司名称';
    COMMENT ON COLUMN bole_app.t_work_experiences.position IS '职位名称';
    COMMENT ON COLUMN bole_app.t_work_experiences.start_date IS '开始日期';
    COMMENT ON COLUMN bole_app.t_work_experiences.end_date IS '结束日期（为空表示至今）';
    COMMENT ON COLUMN bole_app.t_work_experiences.is_current IS '是否当前职位';
    COMMENT ON COLUMN bole_app.t_work_experiences.description IS '工作描述';
    COMMENT ON COLUMN bole_app.t_work_experiences.achievements IS '工作成就（JSON数组）';
    COMMENT ON COLUMN bole_app.t_work_experiences.sort IS '排序字段';
    COMMENT ON COLUMN bole_app.t_work_experiences.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_work_experiences.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_work_experiences.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建技能表
    CREATE TABLE IF NOT EXISTS bole_app.t_skill (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,

        user_id BIGINT NOT NULL,
        name VARCHAR(255) NOT NULL,
        level VARCHAR(100),
        category VARCHAR(100),
        description TEXT,
        proficiency_percent INTEGER CHECK (proficiency_percent >= 0 AND proficiency_percent <= 100),
        experience_years NUMERIC(4,1),
        is_certified BOOLEAN DEFAULT FALSE,
        certificate_name VARCHAR(255),
        certificate_date DATE,
        tags TEXT,
        is_public BOOLEAN DEFAULT TRUE,
        sort INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,

        -- 外键约束
        CONSTRAINT fk_skill_user 
        FOREIGN KEY (user_id) 
        REFERENCES bole_app.t_user(id) 
        ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_skill_user_id ON bole_app.t_skill(user_id);
    CREATE INDEX IF NOT EXISTS idx_skill_category ON bole_app.t_skill(category);
    CREATE INDEX IF NOT EXISTS idx_skill_level ON bole_app.t_skill(level);
    CREATE INDEX IF NOT EXISTS idx_skill_public ON bole_app.t_skill(is_public);
    CREATE INDEX IF NOT EXISTS idx_skill_sort ON bole_app.t_skill(sort);
    CREATE INDEX IF NOT EXISTS idx_skill_deleted ON bole_app.t_skill(deleted);

    -- 创建复合索引以提高查询性能
    CREATE INDEX IF NOT EXISTS idx_skill_user_public ON bole_app.t_skill(user_id, is_public);
    CREATE INDEX IF NOT EXISTS idx_skill_user_category ON bole_app.t_skill(user_id, category);


    -- 注释
    COMMENT ON TABLE bole_app.t_skill IS '技能表';
    COMMENT ON COLUMN bole_app.t_skill.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_skill.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_skill.name IS '技能名称';
    COMMENT ON COLUMN bole_app.t_skill.level IS '技能等级';
    COMMENT ON COLUMN bole_app.t_skill.category IS '技能分类';
    COMMENT ON COLUMN bole_app.t_skill.description IS '技能描述';
    COMMENT ON COLUMN bole_app.t_skill.proficiency_percent IS '熟练度百分比(0-100)';
    COMMENT ON COLUMN bole_app.t_skill.experience_years IS '经验年限';
    COMMENT ON COLUMN bole_app.t_skill.is_certified IS '是否认证';
    COMMENT ON COLUMN bole_app.t_skill.certificate_name IS '证书名称';
    COMMENT ON COLUMN bole_app.t_skill.certificate_date IS '获证日期';
    COMMENT ON COLUMN bole_app.t_skill.tags IS '标签(逗号分隔)';
    COMMENT ON COLUMN bole_app.t_skill.is_public IS '是否公开';
    COMMENT ON COLUMN bole_app.t_skill.sort IS '排序字段';
    COMMENT ON COLUMN bole_app.t_skill.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_skill.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_skill.deleted IS '逻辑删除(0-正常,1-删除)';


    -- 简历组件表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes_component (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        key VARCHAR(100) NOT NULL,
        default_config JSONB,

        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_resumes_component_name ON bole_app.t_resumes_component(name);
    CREATE INDEX IF NOT EXISTS idx_resumes_component_key ON bole_app.t_resumes_component(key);
    CREATE INDEX IF NOT EXISTS idx_resumes_component_created_at ON bole_app.t_resumes_component(created_at);
    CREATE INDEX IF NOT EXISTS idx_resumes_component_deleted ON bole_app.t_resumes_component(deleted);

    -- 注释
    COMMENT ON TABLE bole_app.t_resumes_component IS '简历组件表';
    COMMENT ON COLUMN bole_app.t_resumes_component.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes_component.name IS '组件名称';
    COMMENT ON COLUMN bole_app.t_resumes_component.key IS '预定义组件类型名称';
    COMMENT ON COLUMN bole_app.t_resumes_component.default_config IS '默认配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes_component.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes_component.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes_component.deleted IS '逻辑删除标志(0:未删除,1:已删除)';


    -- 简历模板表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes_template (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        code VARCHAR(100) UNIQUE NOT NULL,
        description TEXT,
        preview_image VARCHAR(500),
        is_active BOOLEAN DEFAULT FALSE,
        version VARCHAR(50) DEFAULT '1.0.0',
        global_style JSONB,
        global_layout JSONB,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_resumes_template_code ON bole_app.t_resumes_template(code);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_is_active ON bole_app.t_resumes_template(is_active);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_version ON bole_app.t_resumes_template(version);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_created_at ON bole_app.t_resumes_template(created_at);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_deleted ON bole_app.t_resumes_template(deleted);

    -- JSONB字段索引（如果经常查询JSON结构中的特定字段）
    -- CREATE INDEX IF NOT EXISTS idx_resumes_template_global_style ON bole_app.t_resumes_template USING gin(global_style);
    -- CREATE INDEX IF NOT EXISTS idx_resumes_template_layout ON bole_app.t_resumes_template USING gin(global_layout);

    -- 表注释和字段注释
    COMMENT ON TABLE bole_app.t_resumes_template IS '简历模板表';
    COMMENT ON COLUMN bole_app.t_resumes_template.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes_template.name IS '模板名称';
    COMMENT ON COLUMN bole_app.t_resumes_template.code IS '模板编码（唯一标识）';
    COMMENT ON COLUMN bole_app.t_resumes_template.description IS '模板描述';
    COMMENT ON COLUMN bole_app.t_resumes_template.preview_image IS '预览图路径/URL';
    COMMENT ON COLUMN bole_app.t_resumes_template.is_active IS '是否激活';
    COMMENT ON COLUMN bole_app.t_resumes_template.version IS '版本号';
    COMMENT ON COLUMN bole_app.t_resumes_template.global_style IS '全局样式配置（JSON格式）';
    COMMENT ON COLUMN bole_app.t_resumes_template.global_layout IS '布局配置（JSON格式）';
    COMMENT ON COLUMN bole_app.t_resumes_template.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes_template.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes_template.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 简历模板组件表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes_template_component (
        -- 主键字段
        template_id BIGINT NOT NULL,
        component_id BIGINT NOT NULL,
        name VARCHAR(255) NOT NULL,
        key VARCHAR(100) NOT NULL,
        default_config JSONB,
        props JSONB,
        styles JSONB,
        
        -- 外键约束（假设存在 t_resumes_template 表）
        CONSTRAINT fk_template_component_template 
            FOREIGN KEY (template_id) 
            REFERENCES bole_app.t_resumes_template(id) 
            ON DELETE CASCADE,

        CONSTRAINT fk_template_component_component 
            FOREIGN KEY (component_id) 
            REFERENCES bole_app.t_resumes_component(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_template_component_template_id ON bole_app.t_resumes_template_component(template_id);
    CREATE INDEX IF NOT EXISTS idx_template_component_component_id ON bole_app.t_resumes_template_component(component_id);
    CREATE INDEX IF NOT EXISTS idx_template_component_name ON bole_app.t_resumes_template_component(name);
    CREATE INDEX IF NOT EXISTS idx_template_component_key ON bole_app.t_resumes_template_component(key);

    -- 如果需要查询 JSONB 字段中的特定属性，可以创建 GIN 索引
    CREATE INDEX IF NOT EXISTS idx_template_component_props ON bole_app.t_resumes_template_component USING GIN (props);
    CREATE INDEX IF NOT EXISTS idx_template_component_styles ON bole_app.t_resumes_template_component USING GIN (styles);

    -- 表注释和字段注释
    COMMENT ON COLUMN bole_app.t_resumes_template_component.template_id IS '模板ID';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.component_id IS '组件ID';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.name IS '组件名称';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.key IS '预定义组件类型名称';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.default_config IS '默认配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.props IS '组件属性（JSON格式）';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.styles IS '组件样式变量（JSON格式）';

    -- 自我评价表
    CREATE TABLE IF NOT EXISTS bole_app.t_self_evaluation (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT NOT NULL,
        content TEXT,
        highlights JSONB,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
        
        -- 添加外键约束（如果存在用户表）
        -- CONSTRAINT fk_self_evaluation_user 
        -- FOREIGN KEY (user_id) 
        -- REFERENCES t_user(id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_self_evaluation_user_id ON bole_app.t_self_evaluation(user_id);
    CREATE INDEX IF NOT EXISTS idx_self_evaluation_created_at ON bole_app.t_self_evaluation(created_at);

    -- 如果需要查询 JSONB 字段中的特定属性，可以创建 GIN 索引
    CREATE INDEX IF NOT EXISTS idx_self_evaluation_highlights ON bole_app.t_self_evaluation USING GIN (highlights);
    

    -- 注释
    COMMENT ON TABLE bole_app.t_self_evaluation IS '自我评价表';
    COMMENT ON COLUMN bole_app.t_self_evaluation.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_self_evaluation.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_self_evaluation.content IS '评价内容';
    COMMENT ON COLUMN bole_app.t_self_evaluation.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_self_evaluation.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_self_evaluation.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 城市等级表
    CREATE TABLE IF NOT EXISTS bole_app.t_city_grade (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        level VARCHAR(50) NOT NULL,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_city_grade_name ON bole_app.t_city_grade(name);
    CREATE INDEX IF NOT EXISTS idx_city_grade_level ON bole_app.t_city_grade(level);
    CREATE INDEX IF NOT EXISTS idx_city_grade_created_at ON bole_app.t_city_grade(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_city_grade IS '城市等级表';
    COMMENT ON COLUMN bole_app.t_city_grade.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_city_grade.name IS '等级名称';
    COMMENT ON COLUMN bole_app.t_city_grade.level IS '等级级别（洲，国，省，市，区/县,乡镇，街道）';
    COMMENT ON COLUMN bole_app.t_city_grade.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_city_grade.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_city_grade.deleted IS '逻辑删除标志(0:未删除,1:已删除)';
    
    -- 创建简历表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255),
        user_id BIGINT NOT NULL,
        template_id BIGINT,
        status VARCHAR(50),
        view_count INTEGER DEFAULT 0,
        download_count INTEGER DEFAULT 0,
        
        -- JSON配置字段
        global_style JSONB,
        global_layout JSONB,
        components JSONB,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_resumes_user FOREIGN KEY (user_id) REFERENCES bole_app.t_user(id),
        CONSTRAINT fk_resumes_template FOREIGN KEY (template_id) REFERENCES bole_app.t_resumes_template(id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_resumes_user_id ON bole_app.t_resumes(user_id);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_id ON bole_app.t_resumes(template_id);
    CREATE INDEX IF NOT EXISTS idx_resumes_status ON bole_app.t_resumes(status);
    CREATE INDEX IF NOT EXISTS idx_resumes_created_at ON bole_app.t_resumes(created_at);
    CREATE INDEX IF NOT EXISTS idx_resumes_view_count ON bole_app.t_resumes(view_count);
    CREATE INDEX IF NOT EXISTS idx_resumes_download_count ON bole_app.t_resumes(download_count);

    -- 为JSON字段创建GIN索引（如果需要进行JSON查询）
    CREATE INDEX IF NOT EXISTS idx_resumes_global_style ON bole_app.t_resumes USING GIN (global_style);
    CREATE INDEX IF NOT EXISTS idx_resumes_layout ON bole_app.t_resumes USING GIN (global_layout);
    CREATE INDEX IF NOT EXISTS idx_resumes_components ON bole_app.t_resumes USING GIN (components);

    -- 注释
    COMMENT ON TABLE bole_app.t_resumes IS '简历表';
    COMMENT ON COLUMN bole_app.t_resumes.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes.name IS '简历名称';
    COMMENT ON COLUMN bole_app.t_resumes.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_resumes.template_id IS '模板ID';
    COMMENT ON COLUMN bole_app.t_resumes.status IS '简历状态';
    COMMENT ON COLUMN bole_app.t_resumes.view_count IS '查看次数';
    COMMENT ON COLUMN bole_app.t_resumes.download_count IS '下载次数';
    COMMENT ON COLUMN bole_app.t_resumes.global_style IS '全局样式配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes.global_layout IS '布局配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes.components IS '组件列表(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    --城市表
    CREATE TABLE IF NOT EXISTS bole_app.t_city (
            -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        parent_id BIGINT DEFAULT 0,
        path VARCHAR(255),
        level INTEGER DEFAULT 1,
        name VARCHAR(255) NOT NULL,
        en_name VARCHAR(255),
        initial VARCHAR(10),
        pinyin VARCHAR(255),
        short_name VARCHAR(255),
        longitude DOUBLE PRECISION,
        latitude DOUBLE PRECISION,
        city_grade_id BIGINT,
        cnw_station_code VARCHAR(100),
        nmc_station_code VARCHAR(100),
        nmc_province_code VARCHAR(100),
        cma_station_code VARCHAR(100),
            -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,

        --添加外键约束
        CONSTRAINT fk_city_city_grade 
        FOREIGN KEY (city_grade_id) 
        REFERENCES bole_app.t_city_grade(id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_city_parent_id  ON bole_app.t_city(parent_id);
    CREATE INDEX IF NOT EXISTS idx_city_level  ON bole_app.t_city(level);
    CREATE INDEX IF NOT EXISTS idx_city_name  ON bole_app.t_city(name);
    CREATE INDEX IF NOT EXISTS idx_city_pinyin  ON bole_app.t_city(pinyin);
    CREATE INDEX IF NOT EXISTS idx_city_city_grade_id  ON bole_app.t_city(city_grade_id);
    CREATE INDEX IF NOT EXISTS idx_city_created_at  ON bole_app.t_city(created_at);
    -- 注释
    COMMENT ON TABLE bole_app.t_city IS '城市表';
    COMMENT ON COLUMN bole_app.t_city.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_city.parent_id IS '父节点ID';
    COMMENT ON COLUMN bole_app.t_city.path IS '路径';
    COMMENT ON COLUMN bole_app.t_city.level IS '层级';
    COMMENT ON COLUMN bole_app.t_city.name IS '城市名称';
    COMMENT ON COLUMN bole_app.t_city.en_name IS '英文名称';
    COMMENT ON COLUMN bole_app.t_city.initial IS '首字母';
    COMMENT ON COLUMN bole_app.t_city.pinyin IS '拼音';
    COMMENT ON COLUMN bole_app.t_city.short_name IS '简称';
    COMMENT ON COLUMN bole_app.t_city.longitude IS '经度';
    COMMENT ON COLUMN bole_app.t_city.latitude IS '纬度';
    COMMENT ON COLUMN bole_app.t_city.city_grade_id IS '城市等级ID，外键关联t_city_grade表';
    COMMENT ON COLUMN bole_app.t_city.cnw_station_code IS '中国天气网站点编码';
    COMMENT ON COLUMN bole_app.t_city.nmc_station_code IS '中央气象台站点编码';
    COMMENT ON COLUMN bole_app.t_city.nmc_province_code IS '中央气象台省份编码';
    COMMENT ON COLUMN bole_app.t_city.cma_station_code IS '中国气象局站点编码';
    COMMENT ON COLUMN bole_app.t_city.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_city.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_city.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 区域表
    CREATE TABLE IF NOT EXISTS bole_app.t_region (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        parent_id BIGINT DEFAULT 0,
        path VARCHAR(255),
        level INTEGER DEFAULT 1,
        name VARCHAR(255) NOT NULL,
        short_name VARCHAR(255),
        merger_name VARCHAR(255),
        initial VARCHAR(10),
        pinyin VARCHAR(255),
        jianpin VARCHAR(255),
        longitude DOUBLE PRECISION,
        latitude DOUBLE PRECISION,
        tel_code INTEGER,
        zip_code INTEGER,
        car_code VARCHAR(50),
        cnw_station_code VARCHAR(100),
        nmc_station_code VARCHAR(100),
        nmc_province_code VARCHAR(100),
        nmc_weather_url VARCHAR(255),
        cma_station_code VARCHAR(100),
        status INTEGER DEFAULT 1,
        sort INTEGER DEFAULT 0,
        is_hot INTEGER DEFAULT 0,
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 添加外键约束
        CONSTRAINT fk_region_parent 
        FOREIGN KEY (parent_id) 
        REFERENCES bole_app.t_region(id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_region_parent_id ON bole_app.t_region(parent_id);
    CREATE INDEX IF NOT EXISTS idx_region_level ON bole_app.t_region(level);
    CREATE INDEX IF NOT EXISTS idx_region_name ON bole_app.t_region(name);
    CREATE INDEX IF NOT EXISTS idx_region_pinyin ON bole_app.t_region(pinyin);
    CREATE INDEX IF NOT EXISTS idx_region_jianpin ON bole_app.t_region(jianpin);
    CREATE INDEX IF NOT EXISTS idx_region_initial ON bole_app.t_region(initial);
    CREATE INDEX IF NOT EXISTS idx_region_status ON bole_app.t_region(status);
    CREATE INDEX IF NOT EXISTS idx_region_is_hot ON bole_app.t_region(is_hot);
    CREATE INDEX IF NOT EXISTS idx_region_created_at ON bole_app.t_region(created_at);
    CREATE INDEX IF NOT EXISTS idx_region_deleted ON bole_app.t_region(deleted);

    -- 如果需要按地理位置查询，可以添加复合索引
    CREATE INDEX IF NOT EXISTS idx_region_location ON bole_app.t_region(longitude, latitude);

    -- 注释
    COMMENT ON TABLE bole_app.t_region IS '区域表（行政区划）';
    COMMENT ON COLUMN bole_app.t_region.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_region.parent_id IS '父节点ID';
    COMMENT ON COLUMN bole_app.t_region.path IS '路径';
    COMMENT ON COLUMN bole_app.t_region.level IS '层级';
    COMMENT ON COLUMN bole_app.t_region.name IS '区域名称';
    COMMENT ON COLUMN bole_app.t_region.short_name IS '简称';
    COMMENT ON COLUMN bole_app.t_region.merger_name IS '全称';
    COMMENT ON COLUMN bole_app.t_region.initial IS '首字母';
    COMMENT ON COLUMN bole_app.t_region.pinyin IS '拼音（全拼）';
    COMMENT ON COLUMN bole_app.t_region.jianpin IS '拼音（简拼）';
    COMMENT ON COLUMN bole_app.t_region.longitude IS '经度';
    COMMENT ON COLUMN bole_app.t_region.latitude IS '纬度';
    COMMENT ON COLUMN bole_app.t_region.tel_code IS '电话区号';
    COMMENT ON COLUMN bole_app.t_region.zip_code IS '邮政编码';
    COMMENT ON COLUMN bole_app.t_region.car_code IS '车牌编码';
    COMMENT ON COLUMN bole_app.t_region.cnw_station_code IS '中国天气网站点编码';
    COMMENT ON COLUMN bole_app.t_region.nmc_station_code IS '中央气象台站点编码';
    COMMENT ON COLUMN bole_app.t_region.nmc_province_code IS '中央气象台省份编码';
    COMMENT ON COLUMN bole_app.t_region.nmc_weather_url IS '中央气象台天气URL';
    COMMENT ON COLUMN bole_app.t_region.cma_station_code IS '中国气象局站点编码';
    COMMENT ON COLUMN bole_app.t_region.status IS '状态：0-禁用，1-启用';
    COMMENT ON COLUMN bole_app.t_region.sort IS '排序';
    COMMENT ON COLUMN bole_app.t_region.is_hot IS '是否热门：0-否，1-是';
    COMMENT ON COLUMN bole_app.t_region.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_region.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_region.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 文件表
    CREATE TABLE IF NOT EXISTS bole_app.t_file (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        file_key VARCHAR(64) NOT NULL,
        file_size VARCHAR(50),
        file_name VARCHAR(50),
        file_size_bytes BIGINT,
        original_filename VARCHAR(500),
        storage_path VARCHAR(1000),
        storage_type VARCHAR(20) NOT NULL,
        content_type VARCHAR(100),
        last_access_time TIMESTAMP,
        access_url VARCHAR(1000),
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_file_file_key ON bole_app.t_file(file_key);
    CREATE INDEX IF NOT EXISTS idx_file_storage_type ON bole_app.t_file(storage_type);
    CREATE INDEX IF NOT EXISTS idx_file_content_type ON bole_app.t_file(content_type);
    CREATE INDEX IF NOT EXISTS idx_file_last_access_time ON bole_app.t_file(last_access_time);
    CREATE INDEX IF NOT EXISTS idx_file_created_at ON bole_app.t_file(created_at);
    CREATE INDEX IF NOT EXISTS idx_file_deleted ON bole_app.t_file(deleted);

    -- 添加唯一约束（确保file_key唯一）
    CREATE UNIQUE INDEX IF NOT EXISTS uk_file_file_key ON bole_app.t_file(file_key) WHERE deleted = 0;

    -- 表注释和字段注释
    COMMENT ON TABLE bole_app.t_file IS '文件表';
    COMMENT ON COLUMN bole_app.t_file.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_file.file_key IS '文件全局唯一标识（MD5/SHA256）';
    COMMENT ON COLUMN bole_app.t_file.file_name IS '文件名称';
    COMMENT ON COLUMN bole_app.t_file.file_size IS '文件大小（带单位）';
    COMMENT ON COLUMN bole_app.t_file.file_size_bytes IS '文件大小（字节）';
    COMMENT ON COLUMN bole_app.t_file.original_filename IS '原始文件名';
    COMMENT ON COLUMN bole_app.t_file.storage_path IS '存储路径（相对路径）';
    COMMENT ON COLUMN bole_app.t_file.storage_type IS '存储类型：LOCAL, MINIO';
    COMMENT ON COLUMN bole_app.t_file.content_type IS '文件类型';
    COMMENT ON COLUMN bole_app.t_file.last_access_time IS '最后访问时间';
    COMMENT ON COLUMN bole_app.t_file.access_url IS '可访问的URL';
    COMMENT ON COLUMN bole_app.t_file.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_file.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_file.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 收藏模板关联表
    CREATE TABLE IF NOT EXISTS bole_app.t_favorite_template (
        -- 主键字段（联合主键）
        user_id BIGINT NOT NULL,
        template_id BIGINT NOT NULL,

        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 添加联合主键约束
        PRIMARY KEY (user_id, template_id),
        
        -- 添加外键约束
        CONSTRAINT fk_favorite_template_user 
        FOREIGN KEY (user_id) 
        REFERENCES bole_app.t_user(id) 
        ON DELETE CASCADE,
        
        CONSTRAINT fk_favorite_template_template 
        FOREIGN KEY (template_id) 
        REFERENCES bole_app.t_resumes_template(id) 
        ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_favorite_template_user_id ON bole_app.t_favorite_template(user_id);
    CREATE INDEX IF NOT EXISTS idx_favorite_template_template_id ON bole_app.t_favorite_template(template_id);
    CREATE INDEX IF NOT EXISTS idx_favorite_template_user_template ON bole_app.t_favorite_template(user_id, template_id);

    -- 注释
    COMMENT ON TABLE bole_app.t_favorite_template IS '用户收藏模板关联表';
    COMMENT ON COLUMN bole_app.t_favorite_template.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_favorite_template.template_id IS '简历模板ID';
    COMMENT ON COLUMN bole_app.t_favorite_template.created_at IS '收藏时间';

    -- 关注公司表
    CREATE TABLE IF NOT EXISTS bole_app.t_follow_company (
        user_id BIGINT NOT NULL,
        company_id BIGINT NOT NULL,
        -- 可选：创建时间，用于记录关注的时间
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        -- 设置联合主键
        PRIMARY KEY (user_id, company_id)
    );

    -- 创建索引（根据查询需求，可能需要为company_id创建索引，因为可能会根据公司查找关注用户）
    CREATE INDEX IF NOT EXISTS idx_follow_company_user_id ON bole_app.t_follow_company(user_id);
    CREATE INDEX IF NOT EXISTS idx_follow_company_company_id ON bole_app.t_follow_company(company_id);

    -- 注释
    COMMENT ON TABLE bole_app.t_follow_company IS '关注公司表';
    COMMENT ON COLUMN bole_app.t_follow_company.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_follow_company.company_id IS '公司ID';
    COMMENT ON COLUMN bole_app.t_follow_company.created_at IS '创建时间（关注时间）';

    -- 消息模板表
    CREATE TABLE IF NOT EXISTS bole_app.t_msg_template (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 消息模板业务字段
        subject VARCHAR(500) NOT NULL,
        template TEXT,
        tc_template TEXT,
        en_template TEXT,
        website VARCHAR(500),
        is_verify BOOLEAN DEFAULT FALSE,
        length INTEGER,
        duration INTEGER,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 约束
        CONSTRAINT chk_msg_template_length CHECK (length > 0),
        CONSTRAINT chk_msg_template_duration CHECK (duration > 0)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_msg_template_deleted ON bole_app.t_msg_template(deleted);
    CREATE INDEX IF NOT EXISTS idx_msg_template_created_at ON bole_app.t_msg_template(created_at);
    CREATE INDEX IF NOT EXISTS idx_msg_template_is_verify ON bole_app.t_msg_template(is_verify);
    CREATE INDEX IF NOT EXISTS idx_msg_template_subject ON bole_app.t_msg_template(subject);
    CREATE INDEX IF NOT EXISTS idx_msg_template_updated_at ON bole_app.t_msg_template(updated_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_msg_template IS '消息模板表';
    COMMENT ON COLUMN bole_app.t_msg_template.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_msg_template.subject IS '主题';
    COMMENT ON COLUMN bole_app.t_msg_template.template IS '中文模板';
    COMMENT ON COLUMN bole_app.t_msg_template.tc_template IS '繁体中文模板';
    COMMENT ON COLUMN bole_app.t_msg_template.en_template IS '英文内容模板';
    COMMENT ON COLUMN bole_app.t_msg_template.website IS '模板对应APP下载地址或站点地址';
    COMMENT ON COLUMN bole_app.t_msg_template.is_verify IS '是否需要校验(需要则将校验主要内容(content)的准确性与有效时间)';
    COMMENT ON COLUMN bole_app.t_msg_template.length IS '验证码的长度(当typeId为1时不可为空)';
    COMMENT ON COLUMN bole_app.t_msg_template.duration IS '有效时长(单位根据业务确定，如：分钟、秒等)';
    COMMENT ON COLUMN bole_app.t_msg_template.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_msg_template.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_msg_template.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 短信表
    CREATE TABLE IF NOT EXISTS bole_app.t_sms (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        phone VARCHAR(255) NOT NULL,
        text TEXT,
        content TEXT,
        template_id BIGINT,
        state INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_ems_phone ON bole_app.t_sms(phone);
    CREATE INDEX IF NOT EXISTS idx_ems_template_id ON bole_app.t_sms(template_id);
    CREATE INDEX IF NOT EXISTS idx_ems_state ON bole_app.t_sms(state);
    CREATE INDEX IF NOT EXISTS idx_ems_created_at ON bole_app.t_sms(created_at);

    -- 添加外键约束（如果存在模板表）
    -- ALTER TABLE bole_app.t_sms ADD CONSTRAINT fk_ems_template 
    -- FOREIGN KEY (template_id) REFERENCES bole_app.t_message_template(id);

    -- 注释
    COMMENT ON TABLE bole_app.t_sms IS '邮件发送记录表';
    COMMENT ON COLUMN bole_app.t_sms.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_sms.phone IS '收件人邮箱地址';
    COMMENT ON COLUMN bole_app.t_sms.text IS '邮件文本内容';
    COMMENT ON COLUMN bole_app.t_sms.content IS '核心内容（如验证码）';
    COMMENT ON COLUMN bole_app.t_sms.template_id IS '邮件模板ID';
    COMMENT ON COLUMN bole_app.t_sms.state IS '发送状态（0:待发送,1:发送中,2:发送成功,3:发送失败）';
    COMMENT ON COLUMN bole_app.t_sms.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_sms.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_sms.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 邮件表
    CREATE TABLE IF NOT EXISTS bole_app.t_ems (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        address VARCHAR(255) NOT NULL,
        text TEXT,
        content TEXT,
        template_id BIGINT,
        state INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_ems_address ON bole_app.t_ems(address);
    CREATE INDEX IF NOT EXISTS idx_ems_template_id ON bole_app.t_ems(template_id);
    CREATE INDEX IF NOT EXISTS idx_ems_state ON bole_app.t_ems(state);
    CREATE INDEX IF NOT EXISTS idx_ems_created_at ON bole_app.t_ems(created_at);

    -- 添加外键约束（如果存在模板表）
    -- ALTER TABLE bole_app.t_ems ADD CONSTRAINT fk_ems_template 
    -- FOREIGN KEY (template_id) REFERENCES bole_app.t_message_template(id);

    -- 注释
    COMMENT ON TABLE bole_app.t_ems IS '邮件发送记录表';
    COMMENT ON COLUMN bole_app.t_ems.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_ems.address IS '收件人邮箱地址';
    COMMENT ON COLUMN bole_app.t_ems.text IS '邮件文本内容';
    COMMENT ON COLUMN bole_app.t_ems.content IS '核心内容（如验证码）';
    COMMENT ON COLUMN bole_app.t_ems.template_id IS '邮件模板ID';
    COMMENT ON COLUMN bole_app.t_ems.state IS '发送状态（0:待发送,1:发送中,2:发送成功,3:发送失败）';
    COMMENT ON COLUMN bole_app.t_ems.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_ems.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_ems.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 反馈表
    CREATE TABLE IF NOT EXISTS bole_app.t_feedback (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        user_id BIGINT,
        type VARCHAR(50),
        content TEXT,
        images JSONB,
        contact JSONB,

        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_feedback_user_id ON bole_app.t_feedback(user_id);
    CREATE INDEX IF NOT EXISTS idx_feedback_type ON bole_app.t_feedback(type);
    CREATE INDEX IF NOT EXISTS idx_feedback_created_at ON bole_app.t_feedback(created_at);
    CREATE INDEX IF NOT EXISTS idx_feedback_deleted ON bole_app.t_feedback(deleted);

    -- 如果需要按联系信息中的特定字段查询，可以创建GIN索引（按需使用）
    -- CREATE INDEX IF NOT EXISTS idx_feedback_contact_gin ON bole_app.t_feedback USING gin(contact);
    -- CREATE INDEX IF NOT EXISTS idx_feedback_images_gin ON bole_app.t_feedback USING gin(images);

    -- 添加外键约束（如果存在用户表）
    -- ALTER TABLE bole_app.t_feedback
    -- ADD CONSTRAINT fk_feedback_user
    -- FOREIGN KEY (user_id)
    -- REFERENCES bole_app.t_user(id);

    -- 注释
    COMMENT ON TABLE bole_app.t_feedback IS '用户反馈表';
    COMMENT ON COLUMN bole_app.t_feedback.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_feedback.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_feedback.type IS '反馈类型';
    COMMENT ON COLUMN bole_app.t_feedback.content IS '反馈内容';
    COMMENT ON COLUMN bole_app.t_feedback.images IS '图片列表（JSONB格式）';
    COMMENT ON COLUMN bole_app.t_feedback.contact IS '联系方式信息（JSONB格式）';
    COMMENT ON COLUMN bole_app.t_feedback.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_feedback.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_feedback.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 大学表
    CREATE TABLE IF NOT EXISTS bole_app.t_university (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        code VARCHAR(100) NOT NULL UNIQUE,
        type VARCHAR(50),
        holder VARCHAR(255),
        location VARCHAR(500),
        website VARCHAR(500),
        github VARCHAR(500),
        bio TEXT,
        followers INTEGER DEFAULT 0,
        fans INTEGER DEFAULT 0,
        likes INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_university_name ON bole_app.t_university(name);
    CREATE INDEX IF NOT EXISTS idx_university_code ON bole_app.t_university(code);
    CREATE INDEX IF NOT EXISTS idx_university_type ON bole_app.t_university(type);
    CREATE INDEX IF NOT EXISTS idx_university_holder ON bole_app.t_university(holder);
    CREATE INDEX IF NOT EXISTS idx_university_location ON bole_app.t_university(location);
    CREATE INDEX IF NOT EXISTS idx_university_created_at ON bole_app.t_university(created_at);
    CREATE INDEX IF NOT EXISTS idx_university_deleted ON bole_app.t_university(deleted);
    CREATE INDEX IF NOT EXISTS idx_university_followers ON bole_app.t_university(followers DESC);
    CREATE INDEX IF NOT EXISTS idx_university_likes ON bole_app.t_university(likes DESC);

    -- 注释
    COMMENT ON TABLE bole_app.t_university IS '大学表';
    COMMENT ON COLUMN bole_app.t_university.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_university.name IS '大学名称';
    COMMENT ON COLUMN bole_app.t_university.code IS '大学代码（唯一）';
    COMMENT ON COLUMN bole_app.t_university.type IS '大学类型（985/211/重点/普通）';
    COMMENT ON COLUMN bole_app.t_university.holder IS '主管单位';
    COMMENT ON COLUMN bole_app.t_university.location IS '地理位置';
    COMMENT ON COLUMN bole_app.t_university.website IS '官方网站';
    COMMENT ON COLUMN bole_app.t_university.github IS 'GitHub地址';
    COMMENT ON COLUMN bole_app.t_university.bio IS '简介';
    COMMENT ON COLUMN bole_app.t_university.followers IS '关注者数量';
    COMMENT ON COLUMN bole_app.t_university.fans IS '粉丝数量';
    COMMENT ON COLUMN bole_app.t_university.likes IS '点赞数量';
    COMMENT ON COLUMN bole_app.t_university.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_university.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_university.deleted IS '逻辑删除标志(0:未删除,1:已删除)';

    -- 关注大学表
    CREATE TABLE IF NOT EXISTS bole_app.t_follow_university (
        -- 业务字段
        user_id BIGINT NOT NULL,
        university_id BIGINT NOT NULL,
        
        -- 时间字段（继承自BaseEntity）
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 唯一约束：确保一个用户不能重复关注同一所大学
        CONSTRAINT uk_follow_university_user_uniq UNIQUE (user_id, university_id),
        
        -- 外键约束（根据实际业务需要添加）
        CONSTRAINT fk_follow_university_user FOREIGN KEY (user_id) REFERENCES bole_app.t_user(id),
        CONSTRAINT fk_follow_university_university FOREIGN KEY (university_id) REFERENCES bole_app.t_university(id)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_follow_university_user_id ON bole_app.t_follow_university(user_id);
    CREATE INDEX IF NOT EXISTS idx_follow_university_university_id ON bole_app.t_follow_university(university_id);

    -- 注释
    COMMENT ON TABLE bole_app.t_follow_university IS '用户关注大学表';
    COMMENT ON COLUMN bole_app.t_follow_university.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_follow_university.university_id IS '大学ID';
    COMMENT ON COLUMN bole_app.t_follow_university.created_at IS '创建时间';

    -- 审计日志表
    CREATE TABLE IF NOT EXISTS bole_audit.audit_logs (
        id BIGSERIAL PRIMARY KEY,
        table_name VARCHAR(100) NOT NULL,
        record_id BIGINT NOT NULL,
        action VARCHAR(10) NOT NULL,
        old_data JSONB,
        new_data JSONB,
        changed_by VARCHAR(50),
        changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_audit_logs_changed_at ON bole_audit.audit_logs(changed_at);
    CREATE INDEX IF NOT EXISTS idx_audit_logs_table_record ON bole_audit.audit_logs(table_name, record_id);

    -- 创建统一的更新时间触发器函数
    CREATE OR REPLACE FUNCTION bole_app.update_updated_at_column()
    RETURNS TRIGGER AS $$
    BEGIN
        NEW.updated_at = CURRENT_TIMESTAMP;
        RETURN NEW;
    END;
    $$ LANGUAGE plpgsql;

    -- 为所有需要更新时间的表创建触发器
    DO $$
    DECLARE
        tbl RECORD;
    BEGIN
        FOR tbl IN 
            SELECT table_name 
            FROM information_schema.tables 
            WHERE table_schema = 'bole_app' 
            AND table_name IN (
                't_config', 't_banner','t_dict',
                't_user', 't_company', 't_role', 't_company_comment', 
                't_company_experiences', 't_work_experiences', 't_job_intention',
                't_education_experience', 't_project_experience',
                't_resumes','t_resumes_component', 't_resumes_template',
                't_skill','t_city_grade','t_self_evaluation',
                't_city','t_region','t_file','t_msg_template',
                't_sms','t_ems','t_feedback',
                't_university','t_follow_university','audit_logs'
            )
        LOOP
            EXECUTE format('
                DROP TRIGGER IF EXISTS update_%s_updated_at ON bole_app.%s;
                CREATE TRIGGER update_%s_updated_at
                    BEFORE UPDATE ON bole_app.%s
                    FOR EACH ROW
                    EXECUTE FUNCTION bole_app.update_updated_at_column();
            ', tbl.table_name, tbl.table_name, tbl.table_name, tbl.table_name);
        END LOOP;
    END
    $$;

    -- 授予权限给bole用户
    GRANT USAGE ON SCHEMA bole_app TO bole;
    GRANT USAGE ON SCHEMA bole_audit TO bole;
    GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA bole_app TO bole;
    GRANT SELECT ON ALL TABLES IN SCHEMA bole_audit TO bole;
    GRANT USAGE ON ALL SEQUENCES IN SCHEMA bole_app TO bole;

    -- 授予只读权限（如果readonly用户存在）
    DO $$
    BEGIN
        IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'readonly') THEN
            GRANT USAGE ON SCHEMA bole_app TO readonly;
            GRANT USAGE ON SCHEMA bole_audit TO readonly;
            GRANT SELECT ON ALL TABLES IN SCHEMA bole_app TO readonly;
            GRANT SELECT ON ALL TABLES IN SCHEMA bole_audit TO readonly;
        END IF;
    END
    $$;

    -- 设置表的默认权限
    ALTER DEFAULT PRIVILEGES IN SCHEMA bole_app GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO bole;
    ALTER DEFAULT PRIVILEGES IN SCHEMA bole_app GRANT USAGE ON SEQUENCES TO bole;
    ALTER DEFAULT PRIVILEGES IN SCHEMA bole_audit GRANT SELECT ON TABLES TO bole;
    
    ALTER DEFAULT PRIVILEGES IN SCHEMA bole_app GRANT SELECT ON TABLES TO readonly;
    ALTER DEFAULT PRIVILEGES IN SCHEMA bole_audit GRANT SELECT ON TABLES TO readonly;

EOSQL

echo "数据库表结构初始化完成"