#!/bin/bash
set -e

echo "=== 插入示例数据 ==="

# 等待PostgreSQL启动
until pg_isready -U postgres; do
    sleep 2
done

echo "插入bole数据库示例数据..."

# 插入bole数据库示例数据
psql -v ON_ERROR_STOP=1 -U bole -d bole <<-'EOSQL'

    -- 插入系统配置测试数据
    INSERT INTO bole_app.t_config (config_key, config_value, config_desc, created_by_id, updated_by_id) VALUES
    ('system.name', '伯乐简历', '系统名称', 1, 1),
    ('system.version', '2.1.0', '系统版本号', 1, 1),
    ('system.copyright', 'Copyright © 2025 云楼科技 All Rights Reserved.', '系统版权信息', 1, 1),
    ('system.logo', '/static/images/logo.png', '系统Logo路径', 1, 1),
    ('system.favicon', '/static/images/favicon.ico', '网站图标路径', 1, 1),
    ('upload.max-size', '10485760', '文件上传最大大小(字节)', 1, 1),
    ('upload.allowed-types', 'jpg,jpeg,png,gif,pdf,doc,docx', '允许上传的文件类型', 1, 1),
    ('email.smtp.host', 'smtp.163.com', 'SMTP服务器地址', 1, 1),
    ('email.smtp.port', '465', 'SMTP服务器端口', 1, 1),
    ('email.smtp.ssl', 'true', '是否启用SSL加密', 1, 1),
    ('sms.provider', 'aliyun', '短信服务提供商', 1, 1),
    ('sms.signature', '云楼科技', '短信签名', 1, 1),
    ('security.login.max-attempts', '5', '最大登录尝试次数', 1, 1),
    ('security.login.lock-time', '30', '账户锁定时间(分钟)', 1, 1),
    ('security.password.min-length', '8', '密码最小长度', 1, 1),
    ('security.password.require-special-char', 'true', '密码是否需要特殊字符', 1, 1),
    ('cache.enabled', 'true', '是否启用缓存', 1, 1),
    ('cache.duration', '3600', '缓存持续时间(秒)', 1, 1),
    ('backup.enabled', 'true', '是否启用自动备份', 1, 1),
    ('backup.schedule', '0 2 * * *', '备份计划任务(cron表达式)', 1, 1),
    ('backup.retention-days', '30', '备份文件保留天数', 1, 1),
    ('notification.enabled', 'true', '是否启用系统通知', 1, 1),
    ('notification.email-enabled', 'true', '是否启用邮件通知', 1, 1),
    ('notification.sms-enabled', 'false', '是否启用短信通知', 1, 1),
    ('api.rate-limit.enabled', 'true', '是否启用API限流', 1, 1),
    ('api.rate-limit.requests-per-minute', '100', '每分钟API请求限制', 1, 1),
    ('ui.theme', 'default', '系统主题', 1, 1),
    ('ui.language', 'zh-CN', '系统语言', 1, 1),
    ('ui.timezone', 'Asia/Shanghai', '系统时区', 1, 1),
    ('maintenance.mode', 'false', '是否处于维护模式', 1, 1)
    ON CONFLICT (config_key) DO UPDATE SET
        config_value = EXCLUDED.config_value,
        config_desc = EXCLUDED.config_desc,
        updated_by_id = EXCLUDED.updated_by_id,
        updated_at = CURRENT_TIMESTAMP;


    -- 插入性别字典数据（类型：sys_sex）
    INSERT INTO bole_app.t_dict (parent_id, path, level, name, type, code, value, label, state, created_at, updated_at, deleted) VALUES
    (0, '1', 1, '性别', 'sys_sex', 'sex_root', '', '性别分类', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (1, '1.2', 2, '男', 'sys_sex', 'male', '1', '男', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (1, '1.3', 2, '女', 'sys_sex', 'female', '2', '女', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (1, '1.4', 2, '未知', 'sys_sex', 'unknown', '0', '未知', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

    -- 插入用户状态字典数据（类型：user_status）
    INSERT INTO bole_app.t_dict (parent_id, path, level, name, type, code, value, label, state, created_at, updated_at, deleted) VALUES
    (0, '5', 1, '用户状态', 'user_status', 'user_status_root', '', '用户状态分类', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (5, '5.6', 2, '正常', 'user_status', 'normal', '1', '正常', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (5, '5.7', 2, '禁用', 'user_status', 'disabled', '0', '禁用', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (5, '5.8', 2, '锁定', 'user_status', 'locked', '2', '锁定', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (5, '5.9', 2, '未激活', 'user_status', 'inactive', '3', '未激活', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

    -- 插入订单状态字典数据（类型：order_status）
    INSERT INTO bole_app.t_dict (parent_id, path, level, name, type, code, value, label, state, created_at, updated_at, deleted) VALUES
    (0, '10', 1, '订单状态', 'order_status', 'order_status_root', '', '订单状态分类', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.11', 2, '待支付', 'order_status', 'pending_payment', '1', '待支付', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.12', 2, '已支付', 'order_status', 'paid', '2', '已支付', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.13', 2, '已发货', 'order_status', 'shipped', '3', '已发货', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.14', 2, '已完成', 'order_status', 'completed', '4', '已完成', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.15', 2, '已取消', 'order_status', 'cancelled', '5', '已取消', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.16', 2, '退款中', 'order_status', 'refunding', '6', '退款中', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (10, '10.17', 2, '已退款', 'order_status', 'refunded', '7', '已退款', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);

    -- 插入商品状态字典数据（类型：product_status）
    INSERT INTO bole_app.t_dict (parent_id, path, level, name, type, code, value, label, state, created_at, updated_at, deleted) VALUES
    (0, '18', 1, '商品状态', 'product_status', 'product_status_root', '', '商品状态分类', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (18, '18.19', 2, '上架', 'product_status', 'on_sale', '1', '上架', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (18, '18.20', 2, '下架', 'product_status', 'off_shelf', '0', '下架', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (18, '18.21', 2, '缺货', 'product_status', 'out_of_stock', '2', '缺货', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (18, '18.22', 2, '预售', 'product_status', 'pre_sale', '3', '预售', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    (18, '18.23', 2, '停售', 'product_status', 'discontinued', '4', '停售', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0);


    -- 插入企业数据
    INSERT INTO bole_app.t_company (id, name, email, holder, location, website, github, wechat, bio, followers, fans, likes) VALUES
    (1, '腾讯科技', 'hr@tencent.com', '马化腾', '深圳', 'https://www.tencent.com', 'tencent', 'Tencent_Official', '领先的互联网科技公司', 50000, 2000, 15000),
    (2, '阿里巴巴', 'recruit@alibaba.com', '马云', '杭州', 'https://www.alibaba.com', 'alibaba', 'Alibaba_Official', '全球领先的电子商务平台', 45000, 1800, 12000),
    (3, '字节跳动', 'jobs@bytedance.com', '张一鸣', '北京', 'https://www.bytedance.com', 'bytedance', 'ByteDance_Official', '创造信息分发价值的技术公司', 30000, 1500, 8000);

    -- 插入用户数据
    INSERT INTO bole_app.t_user (id, company_id, username, password, email, phone, name, avatar, title, location, website, github, wechat, bio, followers, fans, likes, status, work_years, last_login_at) VALUES
    (1, 1, 'admin', '$2a$10$US6aouREA4bdMT.V5Pv.POu6iQWzousSsM.RRdTZ0omK3ivjLb7US', 'zhangsan@tencent.com', '13800138001', '张三', 'https://example.com/avatar1.jpg', '高级工程师', '深圳', 'https://zhangsan.dev', 'zhangsan', 'zhangsan_wx', '专注于后端开发和系统架构', 150, 80, 300, 1, 5, '2024-01-15 10:30:00'),
    (2, 1, 'lisi', '$2a$10$US6aouREA4bdMT.V5Pv.POu6iQWzousSsM.RRdTZ0omK3ivjLb7US', 'lisi@tencent.com', '13800138002', '李四', 'https://example.com/avatar2.jpg', '前端开发专家', '北京', 'https://lisi.dev', 'lisi', 'lisi_wx', '热爱前端技术和用户体验设计', 200, 120, 450, 1, 7, '2024-01-14 15:20:00'),
    (3, 2, 'wangwu', '$2a$10$US6aouREA4bdMT.V5Pv.POu6iQWzousSsM.RRdTZ0omK3ivjLb7US', 'wangwu@alibaba.com', '13800138003', '王五', 'https://example.com/avatar3.jpg', '架构师', '杭州', 'https://wangwu.dev', 'wangwu', 'wangwu_wx', '专注于分布式系统和云原生架构', 300, 150, 600, 1, 8, '2024-01-13 09:15:00'),
    (4, 3, 'zhaoliu', '$2a$10$US6aouREA4bdMT.V5Pv.POu6iQWzousSsM.RRdTZ0omK3ivjLb7US', 'zhaoliu@bytedance.com', '13800138004', '赵六', 'https://example.com/avatar4.jpg', '全栈工程师', '上海', 'https://zhaoliu.dev', 'zhaoliu', 'zhaoliu_wx', '全栈开发，热爱新技术', 180, 90, 350, 1, 4, '2024-01-12 14:45:00');

    -- 插入角色数据
    INSERT INTO bole_app.t_role (id, name, code, description) VALUES
    (1, '超级管理员', 'SUPER', '系统超级管理员，拥有所有权限'),
    (2, '普通用户', 'USER', '普通注册用户'),
    (3, '企业管理员', 'ADMIN', '企业管理員，可以管理企业相关信息'),
    (4, '内容审核员', 'MODERATOR', '负责内容审核和管理的用户');

    -- 插入用户角色关联数据
    INSERT INTO bole_app.t_user_role (user_id, role_id) VALUES
    (1, 2),
    (1, 3),
    (2, 2),
    (3, 2),
    (3, 3),
    (4, 2);

    -- 插入企业评论数据
    INSERT INTO bole_app.t_company_comment (id, parent_id, path, level, company_id, content, user_id, user_name, user_avatar, score, status, like_count, reply_count, anonymous) VALUES
    (1, 0, '1', 1, 1, '公司文化很好，技术氛围浓厚，同事之间相处融洽。', 2, '李四', 'https://example.com/avatar2.jpg', 5, 1, 25, 3, false),
    (2, 1, '1.2', 2, 1, '同意，特别是技术分享会很有收获！', 3, '王五', 'https://example.com/avatar3.jpg', 5, 1, 8, 0, false),
    (3, 0, '3', 1, 2, '工作压力有点大，但成长空间很大，能学到很多东西。', 1, '张三', 'https://example.com/avatar1.jpg', 4, 1, 15, 2, false),
    (4, 0, '4', 1, 3, '年轻有活力的团队，技术栈很新，适合年轻人发展。', 4, '赵六', 'https://example.com/avatar4.jpg', 5, 1, 20, 1, false);

    -- 插入公司经历数据
    INSERT INTO bole_app.t_company_experiences (id, company_id, position, start_date, end_date, is_current, description, website, sort) VALUES
    (1, 1, '软件开发工程师', '2020-03-01', '2022-05-31', false, '负责核心业务系统开发', '["https://project1.com", "https://project2.com"]', 1),
    (2, 1, '高级软件工程师', '2022-06-01', NULL, true, '负责系统架构设计和团队管理', '["https://project3.com"]', 2),
    (3, 2, 'Java开发工程师', '2019-01-15', '2021-12-31', false, '参与电商平台后端开发', '[]', 1);

    -- 插入教育经历数据
    INSERT INTO bole_app.t_education_experience (id, user_id, school, major, degree, start_date, end_date, is_highest, sort, description, achievements) VALUES
    (1, 1, '清华大学', '计算机科学与技术', '本科', '2014-09-01', '2018-06-30', 0, 1, '主修计算机相关课程', '["校级优秀毕业生", "ACM竞赛二等奖"]'),
    (2, 1, '北京大学', '软件工程', '硕士', '2018-09-01', '2021-06-30', 1, 2, '研究方向：分布式系统', '["发表论文2篇", "国家奖学金"]'),
    (3, 2, '浙江大学', '电子信息工程', '本科', '2015-09-01', '2019-06-30', 1, 1, '学习电子技术和编程', '["优秀学生干部", "创新项目奖"]'),
    (4, 3, '上海交通大学', '计算机科学', '博士', '2012-09-01', '2018-06-30', 1, 1, '研究方向：人工智能', '["发表SCI论文3篇", "博士国家奖学金"]');

    -- 插入项目经验数据
    INSERT INTO bole_app.t_project_experience (id, user_id, name, status, start_date, end_date, is_highest, description, achievements, sort) VALUES
    (1, 1, '分布式消息队列系统', 1, '2022-01-01', '2022-12-31', 1, '设计并实现高可用分布式消息队列', '["系统吞吐量提升50%", "支持每秒百万级消息处理"]', 1),
    (2, 1, '微服务架构迁移', 1, '2021-03-01', '2021-11-30', 0, '将单体应用迁移到微服务架构', '["系统可用性达到99.99%", "开发效率提升30%"]', 2),
    (3, 2, '前端性能优化平台', 1, '2023-01-01', '2023-06-30', 1, '开发前端性能监控和优化平台', '["页面加载时间减少40%", "获得公司技术创新奖"]', 1),
    (4, 3, '云原生容器平台', 0, '2023-03-01', NULL, 1, '基于Kubernetes的容器管理平台', '["支持千级节点管理", "实现自动化部署"]', 1);

    -- 插入工作经历数据
    INSERT INTO bole_app.t_work_experiences (id, user_id, company_id, position, start_date, end_date, is_current, description, achievements, sort) VALUES
    (1, 1, 1, '软件工程师', '2021-07-01', '2023-06-30', false, '负责核心业务功能开发', '["完成3个重大项目", "获得年度优秀员工"]', 1),
    (2, 1, 1, '高级软件工程师', '2023-07-01', NULL, true, '负责系统架构设计和团队指导', '["主导系统重构", "培养3名初级工程师"]', 2),
    (3, 2, 1, '前端开发工程师', '2019-07-01', '2022-12-31', false, '负责Web前端开发', '["开发10+核心页面", "性能优化成果显著"]', 1),
    (4, 2, 1, '前端开发专家', '2023-01-01', NULL, true, '负责前端架构和技术选型', '["引入微前端架构", "建立前端规范"]', 2),
    (5, 3, 2, 'Java开发工程师', '2018-07-01', '2021-08-31', false, '参与电商平台开发', '["处理高并发场景", "系统稳定性提升"]', 1),
    (6, 3, 2, '系统架构师', '2021-09-01', NULL, true, '负责系统架构设计', '["设计微服务架构", "技术团队建设"]', 2);

    -- 插入5个简历模板
    INSERT INTO bole_app.t_resumes_template (name, code, description, preview_image, is_active, version, global_style, layout, created_at) VALUES
    -- 1. 经典简洁模板
    (
        '经典简洁',
        'classic_simple_v1',
        '经典简约设计，适合传统行业和保守型求职者，布局清晰，重点突出',
        'https://example.com/images/resumes/classic_simple.png',
        true,
        '1.0.0',
        '{
            "theme": "classic",
            "fontFamily": "Microsoft YaHei, SimSun, serif",
            "fontSize": "12px",
            "lineHeight": "1.6",
            "primaryColor": "#2c3e50",
            "secondaryColor": "#7f8c8d",
            "backgroundColor": "#ffffff",
            "headerColor": "#3498db",
            "margin": "20mm",
            "padding": "10px"
        }'::jsonb,
        '{
            "sections": ["personal_info", "education", "work_experience", "skills", "projects"],
            "columns": 1,
            "sectionOrder": ["header", "personal_info", "summary", "work_experience", "education", "skills", "projects", "certifications"],
            "showPhoto": true,
            "photoPosition": "right_top",
            "pageSize": "A4",
            "orientation": "portrait"
        }'::jsonb,
        CURRENT_TIMESTAMP
    ),

    -- 2. 现代设计模板
    (
        '现代设计',
        'modern_design_v1',
        '现代扁平化设计，适合互联网、科技行业，视觉冲击力强',
        'https://example.com/images/resumes/modern_design.png',
        true,
        '1.2.0',
        '{
            "theme": "modern",
            "fontFamily": "PingFang SC, Helvetica, Arial, sans-serif",
            "fontSize": "14px",
            "lineHeight": "1.8",
            "primaryColor": "#1a237e",
            "secondaryColor": "#5c6bc0",
            "accentColor": "#ff9800",
            "backgroundColor": "#f5f7fa",
            "headerColor": "#1a237e",
            "borderRadius": "8px",
            "shadow": "0 2px 10px rgba(0,0,0,0.1)"
        }'::jsonb,
        '{
            "sections": ["personal_info", "summary", "work_experience", "projects", "skills", "education"],
            "columns": 2,
            "leftColumn": ["personal_info", "skills", "languages"],
            "rightColumn": ["summary", "work_experience", "projects", "education"],
            "showPhoto": true,
            "photoPosition": "left_top",
            "pageSize": "A4",
            "orientation": "portrait",
            "showSidebar": true
        }'::jsonb,
        CURRENT_TIMESTAMP
    ),

    -- 3. 创意艺术模板
    (
        '创意艺术',
        'creative_art_v1',
        '创意设计风格，适合设计师、艺术家、创意工作者，展现个性与创造力',
        'https://example.com/images/resumes/creative_art.png',
        true,
        '1.1.0',
        '{
            "theme": "creative",
            "fontFamily": "Montserrat, Roboto, sans-serif",
            "fontSize": "13px",
            "lineHeight": "1.7",
            "primaryColor": "#d81b60",
            "secondaryColor": "#8e24aa",
            "accentColor": "#ffeb3b",
            "backgroundColor": "#ffffff",
            "gradient": "linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)",
            "borderStyle": "dashed",
            "iconStyle": "filled"
        }'::jsonb,
        '{
            "sections": ["personal_info", "portfolio", "work_experience", "skills", "education", "awards"],
            "columns": 1,
            "sectionOrder": ["header", "personal_info", "portfolio", "work_experience", "skills", "education", "awards"],
            "showPhoto": true,
            "photoPosition": "center_top",
            "photoStyle": "circle",
            "pageSize": "A4",
            "orientation": "portrait",
            "showIcons": true
        }'::jsonb,
        CURRENT_TIMESTAMP
    ),

    -- 4. 专业商务模板
    (
        '专业商务',
        'professional_business_v1',
        '专业商务风格，适合金融、咨询、管理岗位，彰显专业与权威',
        'https://example.com/images/resumes/professional_business.png',
        true,
        '1.3.0',
        '{
            "theme": "professional",
            "fontFamily": "Times New Roman, Georgia, serif",
            "fontSize": "11px",
            "lineHeight": "1.5",
            "primaryColor": "#000000",
            "secondaryColor": "#333333",
            "accentColor": "#1a237e",
            "backgroundColor": "#ffffff",
            "headerColor": "#1a237e",
            "borderColor": "#e0e0e0",
            "fontWeight": "normal",
            "letterSpacing": "0.5px"
        }'::jsonb,
        '{
            "sections": ["personal_info", "professional_summary", "work_experience", "education", "certifications", "skills"],
            "columns": 1,
            "sectionOrder": ["header", "personal_info", "professional_summary", "work_experience", "education", "certifications", "skills"],
            "showPhoto": false,
            "pageSize": "A4",
            "orientation": "portrait",
            "margin": {
                "top": "15mm",
                "right": "15mm",
                "bottom": "15mm",
                "left": "15mm"
            },
            "lineStyle": "solid"
        }'::jsonb,
        CURRENT_TIMESTAMP
    ),

    -- 5. 学术研究模板
    (
        '学术研究',
        'academic_research_v1',
        '学术研究风格，适合学者、研究人员、教育工作者，突出学术成果',
        'https://example.com/images/resumes/academic_research.png',
        true,
        '1.0.0',
        '{
            "theme": "academic",
            "fontFamily": "Cambria, Georgia, serif",
            "fontSize": "12px",
            "lineHeight": "1.8",
            "primaryColor": "#2e7d32",
            "secondaryColor": "#558b2f",
            "backgroundColor": "#ffffff",
            "headerColor": "#1b5e20",
            "citationStyle": "APA",
            "paragraphIndent": "2em",
            "sectionSpacing": "20px"
        }'::jsonb,
        '{
            "sections": ["personal_info", "education", "research_interests", "publications", "conferences", "teaching_experience", "grants", "references"],
            "columns": 1,
            "sectionOrder": ["header", "personal_info", "education", "research_interests", "publications", "conferences", "teaching_experience", "grants", "skills", "references"],
            "showPhoto": false,
            "pageSize": "A4",
            "orientation": "portrait",
            "showPageNumbers": true,
            "headerFooter": true,
            "bibStyle": "APA"
        }'::jsonb,
        CURRENT_TIMESTAMP
    );

-- 1. ResumeBasicInfo（基本信息组件）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES (
        '基本信息',
        'ResumeBasicInfo',
        '{"props":{
            "title": "基本信息",
            "showAvatar": true,
            "avatarSize": "medium",
            "showName": true,
            "showGender": true,
            "showBirthday": true,
            "showPhone": true,
            "showEmail": true,
            "showLocation": true,
            "showWorkYears": true,
            "fields": ["name", "gender", "birthday", "phone", "email", "location", "workYears"]
        },"styles":{
            "fontSize": "16px",
            "titleColor": "#333333",
            "fieldColor": "#666666",
            "backgroundColor": "#FFFFFF",
            "padding": "20px",
            "borderRadius": "8px",
            "avatarBorder": "2px solid #e8e8e8"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);

-- 2. ResumeJobIntention（求职意向组件）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES ('求职意向',
        'ResumeJobIntention',
        '{"props":{
            "title": "求职意向",
            "showExpectedPosition": true,
            "showExpectedIndustry": true,
            "showExpectedSalary": true,
            "showWorkLocation": true,
            "showJobType": true,
            "showOnboardingTime": true,
            "showCurrentStatus": true,
            "salaryUnit": "K",
            "locationType": "city"
        },"styles":{
            "fontSize": "16px",
            "titleColor": "#333333",
            "highlightColor": "#1890ff",
            "backgroundColor": "#FFFFFF",
            "padding": "20px",
            "borderRadius": "8px",
            "boxShadow": "0 2px 8px rgba(0,0,0,0.1)"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);

-- 3. ResumeWorkExperience（工作经历组件）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES ('工作经历',
        'ResumeWorkExperience',
        '{"props":{
            "title": "工作经历",
            "maxItems": 5,
            "showCompanyLogo": true,
            "showCompanyName": true,
            "showJobTitle": true,
            "showDepartment": true,
            "showWorkPeriod": true,
            "showWorkContent": true,
            "showAchievements": true,
            "showSkills": true,
            "orderBy": "startDate",
            "orderDirection": "desc"
        },"styles":{
            "fontSize": "14px",
            "titleColor": "#333333",
            "companyColor": "#1890ff",
            "periodColor": "#999999",
            "backgroundColor": "#FFFFFF",
            "padding": "20px",
            "borderRadius": "8px",
            "itemSpacing": "16px",
            "timelineColor": "#e8e8e8"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);

-- 4. ResumeEducation（教育背景组件）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES (
        '教育背景',
        'ResumeEducation',
        '{"props":{
            "title": "教育背景",
            "maxItems": 3,
            "showSchoolLogo": true,
            "showSchoolName": true,
            "showMajor": true,
            "showDegree": true,
            "showEducationPeriod": true,
            "showGPA": true,
            "showHonors": true,
            "showCourses": true,
            "orderBy": "graduationDate",
            "orderDirection": "desc",
            "degreeFormat": "full"
        },"styles":{
            "fontSize": "14px",
            "titleColor": "#333333",
            "schoolColor": "#52c41a",
            "majorColor": "#666666",
            "backgroundColor": "#FFFFFF",
            "padding": "20px",
            "borderRadius": "8px",
            "itemSpacing": "12px",
            "borderLeft": "3px solid #52c41a"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);

-- 5. ResumeSelfEvaluation（自我评价组件）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES (
        '自我评价',
        'ResumeSelfEvaluation',
        '{"props":{
            "title": "自我评价",
            "maxLength": 500,
            "showCharacterTraits": true,
            "showSkillsSummary": true,
            "showCareerGoals": true,
            "showStrengths": true,
            "showHobbies": true,
            "characterTraits": ["责任心强", "学习能力强", "团队协作"],
            "format": "paragraph",
            "allowRichText": true
        },"styles":{
            "fontSize": "14px",
            "titleColor": "#333333",
            "contentColor": "#555555",
            "backgroundColor": "#fafafa",
            "padding": "20px",
            "borderRadius": "8px",
            "lineHeight": "1.8",
            "border": "1px solid #f0f0f0",
            "highlightBackground": "#fff7e6"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);

-- 6. 额外添加一个技能专长组件（ResumeSkills）
INSERT INTO bole_app.t_resumes_component
(name, key, default_config, created_at, updated_at, deleted)
VALUES (
        '技能专长',
        'ResumeSkills',
        '{"props":{
            "title": "技能专长",
            "skillCategories": ["编程语言", "框架工具", "数据库", "其他技能"],
            "showSkillLevel": true,
            "showExperienceYears": true,
            "skillLevelType": "progress",
            "maxSkillsPerCategory": 8,
            "groupByCategory": true
        },"styles":{
            "fontSize": "14px",
            "titleColor": "#333333",
            "skillNameColor": "#555555",
            "progressColor": "#1890ff",
            "backgroundColor": "#FFFFFF",
            "padding": "20px",
            "borderRadius": "8px",
            "categorySpacing": "24px",
            "skillSpacing": "12px"
        }}'::jsonb,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        0);


    -- 插入技能数据
    INSERT INTO bole_app.t_skill (user_id, name, level, category, description, proficiency_percent, experience_years, is_certified, certificate_name, certificate_date, tags, is_public, sort) VALUES
    -- 用户1的技能 (用户ID: 1)
    (1, 'Java编程', '高级', '编程语言', '熟练掌握Java语言特性，包括集合、多线程、IO等', 85, 5.5, true, 'Oracle Certified Professional', '2022-03-15', 'Java,后端,编程', true, 1),
    (1, 'Spring框架', '高级', '框架', '精通Spring、Spring Boot、Spring Cloud等框架', 90, 4.0, true, 'Spring Professional Certification', '2023-01-20', 'Spring,Java,微服务', true, 2),
    (1, 'MySQL', '中级', '数据库', '熟悉MySQL数据库设计、优化和SQL调优', 75, 3.0, false, NULL, NULL, '数据库,SQL,MySQL', true, 3),

    -- 用户2的技能 (用户ID: 2)
    (2, 'Python编程', '高级', '编程语言', '熟练使用Python进行数据分析和Web开发', 88, 6.0, true, 'Python Institute PCPP', '2022-08-10', 'Python,数据分析,Web', true, 1),
    (2, '机器学习', '中级', '人工智能', '掌握常用机器学习算法和框架', 70, 2.5, true, 'TensorFlow Developer Certificate', '2023-05-12', '机器学习,AI,Python', true, 2),
    (2, 'Docker', '中级', '运维', '熟悉Docker容器化部署和管理', 65, 2.0, false, NULL, NULL, 'Docker,容器,运维', true, 4),

    -- 用户3的技能 (用户ID: 3)
    (3, 'React', '高级', '前端', '精通React框架和生态，包括Hooks、Redux等', 82, 4.5, false, NULL, NULL, 'React,前端,JavaScript', true, 1),
    (3, 'JavaScript', '高级', '编程语言', '熟练掌握JavaScript ES6+特性和异步编程', 85, 5.0, false, NULL, NULL, 'JavaScript,前端,编程', true, 2),
    (3, 'Node.js', '中级', '后端', '能够使用Node.js开发服务端应用', 70, 2.5, true, 'Node.js Services Development', '2022-11-05', 'Node.js,后端,JavaScript', false, 3),

    -- 用户4的技能 (用户ID: 4)
    (4, '项目管理', '高级', '管理', '具备大型项目管理经验，熟悉敏捷开发流程', 80, 8.0, true, 'PMP认证', '2021-09-30', '项目管理,敏捷,PMP', true, 1),
    (4, '团队协作', '高级', '软技能', '良好的团队沟通和协作能力', 90, 10.0, false, NULL, NULL, '沟通,协作,领导力', true, 2);

    -- 插入城市等级数据
    INSERT INTO bole_app.t_city_grade (name, level, created_at, updated_at) VALUES
    ('省', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('市', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('区/县', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('乡/镇', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('村/街道', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    -- 插入城市数据
    INSERT INTO bole_app.t_city (name, parent_id, city_grade_id, created_at, updated_at) VALUES
    ('北京市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('上海市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('广州市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('深圳市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('杭州市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('南京市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('苏州市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('西安市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('成都市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('重庆市', 0, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    -- 插入审计日志数据
    INSERT INTO bole_audit.audit_logs (id, table_name, record_id, action, old_data, new_data, changed_by) VALUES
    (1, 't_user', 1, 'UPDATE', '{"name": "张三", "title": "工程师"}', '{"name": "张三", "title": "高级工程师"}', 'zhangsan'),
    (2, 't_company_comment', 1, 'INSERT', NULL, '{"content": "公司文化很好...", "status": 0}', 'lisi'),
    (3, 't_resumes', 1, 'UPDATE', '{"status": "draft"}', '{"status": "active"}', 'zhangsan'),
    (4, 't_work_experiences', 2, 'INSERT', NULL, '{"position": "高级软件工程师", "company_id": 1}', 'zhangsan');


    -- 更新序列值，确保后续插入的主键不会冲突
    SELECT setval('bole_app.t_banner_id_seq', (SELECT MAX(id) FROM bole_app.t_banner));
    SELECT setval('bole_app.t_dict_id_seq', (SELECT MAX(id) FROM bole_app.t_dict));
    SELECT setval('bole_app.t_user_id_seq', (SELECT MAX(id) FROM bole_app.t_user));
    SELECT setval('bole_app.t_company_id_seq', (SELECT MAX(id) FROM bole_app.t_company));
    SELECT setval('bole_app.t_role_id_seq', (SELECT MAX(id) FROM bole_app.t_role));
    SELECT setval('bole_app.t_company_comment_id_seq', (SELECT MAX(id) FROM bole_app.t_company_comment));
    SELECT setval('bole_app.t_company_experiences_id_seq', (SELECT MAX(id) FROM bole_app.t_company_experiences));
    SELECT setval('bole_app.t_education_experience_id_seq', (SELECT MAX(id) FROM bole_app.t_education_experience));
    SELECT setval('bole_app.t_project_experience_id_seq', (SELECT MAX(id) FROM bole_app.t_project_experience));
    SELECT setval('bole_app.t_work_experiences_id_seq', (SELECT MAX(id) FROM bole_app.t_work_experiences));
    SELECT setval('bole_app.t_resumes_id_seq', (SELECT MAX(id) FROM bole_app.t_resumes));
    SELECT setval('bole_app.t_resumes_template_id_seq', (SELECT MAX(id) FROM bole_app.t_resumes_template));
    SELECT setval('bole_app.t_resumes_component_id_seq', (SELECT MAX(id) FROM bole_app.t_resumes_component));
    SELECT setval('bole_app.t_skill_id_seq', (SELECT MAX(id) FROM bole_app.t_skill));
    SELECT setval('bole_audit.audit_logs_id_seq', (SELECT MAX(id) FROM bole_audit.audit_logs));
EOSQL

echo "示例数据插入完成"