#!/bin/bash
set -e

echo "=== 初始化数据库表结构 ==="

# 等待PostgreSQL启动
until pg_isready -h localhost -U postgres; do
    echo "等待 PostgreSQL 启动..."
    sleep 2
done

echo "初始化bole数据库表结构..."

# 检查数据库是否存在
if ! psql -U postgres -lqt | cut -d \| -f 1 | grep -qw bole; then
    echo "错误：数据库 bole 不存在"
    exit 1
fi

# 初始化bole数据库
psql -v ON_ERROR_STOP=1 -U bole -d bole <<-EOSQL
    -- 创建schema
    CREATE SCHEMA IF NOT EXISTS bole_app;
    CREATE SCHEMA IF NOT EXISTS bole_audit;

    -- 设置搜索路径
    ALTER DATABASE bole SET search_path TO bole_app, public;

    -- 创建用户表
    CREATE TABLE IF NOT EXISTS bole_app.t_user (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 基础信息字段
        company_id BIGINT,
        username VARCHAR(100) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
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
    CREATE INDEX IF NOT EXISTS idx_user_email ON bole_app.t_user(email);
    CREATE INDEX IF NOT EXISTS idx_user_company ON bole_app.t_user(company_id);
    CREATE INDEX IF NOT EXISTS idx_user_status ON bole_app.t_user(status);
    CREATE INDEX IF NOT EXISTS idx_user_created_at ON bole_app.t_user(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_user IS '用户表';
    COMMENT ON COLUMN bole_app.t_user.id IS '主键ID';
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

    -- 创建教育经历表
    CREATE TABLE IF NOT EXISTS bole_app.t_education_experience (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        user_id BIGINT NOT NULL,
        
        -- 教育信息字段
        school VARCHAR(200) NOT NULL,
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
    CREATE INDEX IF NOT EXISTS idx_education_experience_school ON bole_app.t_education_experience(school);
    CREATE INDEX IF NOT EXISTS idx_education_experience_degree ON bole_app.t_education_experience(degree);
    CREATE INDEX IF NOT EXISTS idx_education_experience_start_date ON bole_app.t_education_experience(start_date);
    CREATE INDEX IF NOT EXISTS idx_education_experience_is_highest ON bole_app.t_education_experience(is_highest);
    CREATE INDEX IF NOT EXISTS idx_education_experience_created_at ON bole_app.t_education_experience(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_education_experience IS '教育经历表';
    COMMENT ON COLUMN bole_app.t_education_experience.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_education_experience.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_education_experience.school IS '学校名称';
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

    -- 创建简历表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        user_id BIGINT NOT NULL,
        template_id BIGINT,
        
        -- 薪资信息
        salary_current VARCHAR(100),
        salary_expectation VARCHAR(100),
        
        -- 状态字段
        status VARCHAR(50) DEFAULT 'draft',
        
        -- 统计字段
        view_count INTEGER DEFAULT 0,
        download_count INTEGER DEFAULT 0,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_resumes_user 
            FOREIGN KEY (user_id) 
            REFERENCES bole_app.t_user(id) 
            ON DELETE CASCADE
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_resumes_user_id ON bole_app.t_resumes(user_id);
    CREATE INDEX IF NOT EXISTS idx_resumes_status ON bole_app.t_resumes(status);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_id ON bole_app.t_resumes(template_id);
    CREATE INDEX IF NOT EXISTS idx_resumes_created_at ON bole_app.t_resumes(created_at);

    -- 注释
    COMMENT ON TABLE bole_app.t_resumes IS '简历表';
    COMMENT ON COLUMN bole_app.t_resumes.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes.user_id IS '用户ID';
    COMMENT ON COLUMN bole_app.t_resumes.salary_current IS '当前薪资';
    COMMENT ON COLUMN bole_app.t_resumes.salary_expectation IS '期望薪资';
    COMMENT ON COLUMN bole_app.t_resumes.status IS '简历状态(draft-草稿,active-活跃,inactive-不活跃)';
    COMMENT ON COLUMN bole_app.t_resumes.template_id IS '使用的模板ID';
    COMMENT ON COLUMN bole_app.t_resumes.view_count IS '查看次数';
    COMMENT ON COLUMN bole_app.t_resumes.download_count IS '下载次数';
    COMMENT ON COLUMN bole_app.t_resumes.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建简历模板表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes_template (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 模板基本信息
        name VARCHAR(200) NOT NULL,
        store_path TEXT NOT NULL,
        template_config JSONB,
        category VARCHAR(100),
        framework_type VARCHAR(100),
        
        -- 状态和版本信息
        is_active BOOLEAN DEFAULT FALSE,
        version VARCHAR(50) DEFAULT '1.0.0',
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_resumes_template_name ON bole_app.t_resumes_template(name);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_category ON bole_app.t_resumes_template(category);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_framework ON bole_app.t_resumes_template(framework_type);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_active ON bole_app.t_resumes_template(is_active);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_created_at ON bole_app.t_resumes_template(created_at);
    CREATE INDEX IF NOT EXISTS idx_resumes_template_version ON bole_app.t_resumes_template(version);

    -- 创建 JSONB 索引（如果经常查询模板配置中的特定字段）
    CREATE INDEX IF NOT EXISTS idx_resumes_template_config ON bole_app.t_resumes_template USING GIN (template_config);

    -- 注释
    COMMENT ON TABLE bole_app.t_resumes_template IS '简历模板表';
    COMMENT ON COLUMN bole_app.t_resumes_template.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes_template.name IS '模板名称';
    COMMENT ON COLUMN bole_app.t_resumes_template.store_path IS '模板文件存储路径';
    COMMENT ON COLUMN bole_app.t_resumes_template.template_config IS '模板配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes_template.category IS '模板分类';
    COMMENT ON COLUMN bole_app.t_resumes_template.framework_type IS '框架类型';
    COMMENT ON COLUMN bole_app.t_resumes_template.is_active IS '是否激活';
    COMMENT ON COLUMN bole_app.t_resumes_template.version IS '版本号';
    COMMENT ON COLUMN bole_app.t_resumes_template.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes_template.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes_template.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建简历模板组件关联表
    CREATE TABLE IF NOT EXISTS bole_app.t_resumes_template_component (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 关联字段
        template_id BIGINT NOT NULL,
        component_id BIGINT NOT NULL,
        
        -- 组件信息字段
        component_type VARCHAR(100) NOT NULL,
        component_name VARCHAR(200) NOT NULL,
        component_config JSONB,
        sort INTEGER DEFAULT 0,
        is_required BOOLEAN DEFAULT FALSE,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 外键约束
        CONSTRAINT fk_template_component_template 
            FOREIGN KEY (template_id) 
            REFERENCES bole_app.t_resumes_template(id) 
            ON DELETE CASCADE,
        
        -- 唯一约束，避免同一模板中重复组件
        CONSTRAINT uk_template_component_unique 
            UNIQUE (template_id, component_id, component_type)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_template_component_template_id ON bole_app.t_resumes_template_component(template_id);
    CREATE INDEX IF NOT EXISTS idx_template_component_component_id ON bole_app.t_resumes_template_component(component_id);
    CREATE INDEX IF NOT EXISTS idx_template_component_type ON bole_app.t_resumes_template_component(component_type);
    CREATE INDEX IF NOT EXISTS idx_template_component_sort ON bole_app.t_resumes_template_component(sort);
    CREATE INDEX IF NOT EXISTS idx_template_component_required ON bole_app.t_resumes_template_component(is_required);
    CREATE INDEX IF NOT EXISTS idx_template_component_created_at ON bole_app.t_resumes_template_component(created_at);

    -- 创建复合索引（常用查询场景）
    CREATE INDEX IF NOT EXISTS idx_template_component_template_sort ON bole_app.t_resumes_template_component(template_id, sort);
    CREATE INDEX IF NOT EXISTS idx_template_component_template_type ON bole_app.t_resumes_template_component(template_id, component_type);

    -- 创建 JSONB 索引（如果经常查询组件配置中的特定字段）
    CREATE INDEX IF NOT EXISTS idx_template_component_config ON bole_app.t_resumes_template_component USING GIN (component_config);

    -- 注释
    COMMENT ON TABLE bole_app.t_resumes_template_component IS '简历模板组件关联表';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.template_id IS '模板ID';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.component_id IS '组件ID';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.component_type IS '组件类型';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.component_name IS '组件名称';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.component_config IS '组件配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.sort IS '排序顺序';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.is_required IS '是否必填';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_resumes_template_component.deleted IS '逻辑删除(0-正常,1-删除)';

    -- 创建组件库表
    CREATE TABLE IF NOT EXISTS bole_app.t_component_library (
        -- 主键字段
        id BIGSERIAL PRIMARY KEY,
        
        -- 组件基本信息
        component_type VARCHAR(100) NOT NULL,
        component_name VARCHAR(200) NOT NULL,
        default_config JSONB,
        framework_type VARCHAR(100),
        category VARCHAR(100),
        description TEXT,
        thumbnail TEXT,
        
        -- 时间字段
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        
        -- 逻辑删除字段
        deleted INTEGER DEFAULT 0,
        
        -- 唯一约束，避免重复组件
        CONSTRAINT uk_component_library_unique 
            UNIQUE (component_type, component_name, framework_type)
    );

    -- 创建索引
    CREATE INDEX IF NOT EXISTS idx_component_library_type ON bole_app.t_component_library(component_type);
    CREATE INDEX IF NOT EXISTS idx_component_library_name ON bole_app.t_component_library(component_name);
    CREATE INDEX IF NOT EXISTS idx_component_library_framework ON bole_app.t_component_library(framework_type);
    CREATE INDEX IF NOT EXISTS idx_component_library_category ON bole_app.t_component_library(category);
    CREATE INDEX IF NOT EXISTS idx_component_library_created_at ON bole_app.t_component_library(created_at);

    -- 创建复合索引（常用查询场景）
    CREATE INDEX IF NOT EXISTS idx_component_library_type_category ON bole_app.t_component_library(component_type, category);
    CREATE INDEX IF NOT EXISTS idx_component_library_framework_category ON bole_app.t_component_library(framework_type, category);

    -- 创建 JSONB 索引（如果经常查询默认配置中的特定字段）
    CREATE INDEX IF NOT EXISTS idx_component_library_default_config ON bole_app.t_component_library USING GIN (default_config);

    -- 创建全文搜索索引（如果需要对描述进行搜索）
    -- CREATE INDEX IF NOT EXISTS idx_component_library_description_search ON bole_app.t_component_library USING GIN (to_tsvector('chinese', description)); 

    -- 注释
    COMMENT ON TABLE bole_app.t_component_library IS '组件库表';
    COMMENT ON COLUMN bole_app.t_component_library.id IS '主键ID';
    COMMENT ON COLUMN bole_app.t_component_library.component_type IS '组件类型';
    COMMENT ON COLUMN bole_app.t_component_library.component_name IS '组件名称';
    COMMENT ON COLUMN bole_app.t_component_library.default_config IS '默认配置(JSON格式)';
    COMMENT ON COLUMN bole_app.t_component_library.framework_type IS '框架类型';
    COMMENT ON COLUMN bole_app.t_component_library.category IS '分类';
    COMMENT ON COLUMN bole_app.t_component_library.description IS '组件描述';
    COMMENT ON COLUMN bole_app.t_component_library.thumbnail IS '缩略图URL';
    COMMENT ON COLUMN bole_app.t_component_library.created_at IS '创建时间';
    COMMENT ON COLUMN bole_app.t_component_library.updated_at IS '更新时间';
    COMMENT ON COLUMN bole_app.t_component_library.deleted IS '逻辑删除(0-正常,1-删除)';

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
        CONSTRAINT fk_city_parent 
        FOREIGN KEY (parent_id) 
        REFERENCES bole_app.t_city(id),

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

    -- 系统配置表
    CREATE TABLE IF NOT EXISTS bole_app.system_config (
        config_key VARCHAR(100) PRIMARY KEY,
        config_value JSONB NOT NULL,
        description TEXT,
        updated_by VARCHAR(50),
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

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
                't_user', 't_company', 't_role', 't_company_comment', 
                't_company_experiences', 't_work_experiences', 
                't_education_experience', 't_project_experience',
                't_resumes', 't_resumes_template', 't_component_library',
                't_resumes_template_component','t_skill',
                't_city_grade','t_city'
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