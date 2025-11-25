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
    -- 插入企业数据
    INSERT INTO bole_app.t_company (id, name, email, holder, location, website, github, wechat, bio, followers, fans, likes) VALUES
    (1, '腾讯科技', 'hr@tencent.com', '马化腾', '深圳', 'https://www.tencent.com', 'tencent', 'Tencent_Official', '领先的互联网科技公司', 50000, 2000, 15000),
    (2, '阿里巴巴', 'recruit@alibaba.com', '马云', '杭州', 'https://www.alibaba.com', 'alibaba', 'Alibaba_Official', '全球领先的电子商务平台', 45000, 1800, 12000),
    (3, '字节跳动', 'jobs@bytedance.com', '张一鸣', '北京', 'https://www.bytedance.com', 'bytedance', 'ByteDance_Official', '创造信息分发价值的技术公司', 30000, 1500, 8000);

    -- 插入用户数据
    INSERT INTO bole_app.t_user (id, company_id, username, password, email, phone, name, avatar, title, location, website, github, wechat, bio, followers, fans, likes, status, work_years, last_login_at) VALUES
    (1, 1, 'zhangsan', '$2a$10$r3d7i8JkL9mN0bV1c2X3Y4z', 'zhangsan@tencent.com', '13800138001', '张三', 'https://example.com/avatar1.jpg', '高级工程师', '深圳', 'https://zhangsan.dev', 'zhangsan', 'zhangsan_wx', '专注于后端开发和系统架构', 150, 80, 300, 1, 5, '2024-01-15 10:30:00'),
    (2, 1, 'lisi', '$2a$10$r3d7i8JkL9mN0bV1c2X3Y4z', 'lisi@tencent.com', '13800138002', '李四', 'https://example.com/avatar2.jpg', '前端开发专家', '北京', 'https://lisi.dev', 'lisi', 'lisi_wx', '热爱前端技术和用户体验设计', 200, 120, 450, 1, 7, '2024-01-14 15:20:00'),
    (3, 2, 'wangwu', '$2a$10$r3d7i8JkL9mN0bV1c2X3Y4z', 'wangwu@alibaba.com', '13800138003', '王五', 'https://example.com/avatar3.jpg', '架构师', '杭州', 'https://wangwu.dev', 'wangwu', 'wangwu_wx', '专注于分布式系统和云原生架构', 300, 150, 600, 1, 8, '2024-01-13 09:15:00'),
    (4, 3, 'zhaoliu', '$2a$10$r3d7i8JkL9mN0bV1c2X3Y4z', 'zhaoliu@bytedance.com', '13800138004', '赵六', 'https://example.com/avatar4.jpg', '全栈工程师', '上海', 'https://zhaoliu.dev', 'zhaoliu', 'zhaoliu_wx', '全栈开发，热爱新技术', 180, 90, 350, 1, 4, '2024-01-12 14:45:00');

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

    -- 插入简历模板数据
    INSERT INTO bole_app.t_resumes_template (id, name, store_path, template_config, category, framework_type, is_active, version) VALUES
    (1, '现代简约简历模板', '/templates/modern-simple', '{"sections": ["basic", "education", "work", "skills"], "colors": {"primary": "#2563eb", "secondary": "#64748b"}, "fonts": {"heading": "Arial", "body": "Helvetica"}}', '现代', 'React', true, '1.0.0'),
    (2, '专业传统简历模板', '/templates/professional-traditional', '{"sections": ["basic", "summary", "education", "work", "projects", "skills"], "colors": {"primary": "#000000", "secondary": "#333333"}, "fonts": {"heading": "Times New Roman", "body": "Georgia"}}', '传统', 'Vue', true, '1.1.0'),
    (3, '创意设计简历模板', '/templates/creative-design', '{"sections": ["basic", "portfolio", "skills", "education", "work"], "colors": {"primary": "#ec4899", "secondary": "#8b5cf6"}, "fonts": {"heading": "Poppins", "body": "Inter"}}', '创意', 'React', false, '2.0.0');

    -- 插入简历数据
    INSERT INTO bole_app.t_resumes (id, user_id, template_id, salary_current, salary_expectation, status, view_count, download_count) VALUES
    (1, 1, 1, '30-40万/年', '45-60万/年', 'active', 150, 25),
    (2, 2, 2, '25-35万/年', '40-55万/年', 'active', 120, 18),
    (3, 3, 1, '50-70万/年', '80-100万/年', 'draft', 45, 5),
    (4, 4, 3, '20-30万/年', '35-50万/年', 'inactive', 80, 12);

    -- 插入组件库数据
    INSERT INTO bole_app.t_component_library (id, component_type, component_name, default_config, framework_type, category, description, thumbnail) VALUES
    (1, 'header', '基础头部组件', '{"title": "个人简历", "subtitle": "", "showAvatar": true, "showContact": true}', 'React', 'layout', '简历头部信息展示组件', '/thumbnails/header-basic.jpg'),
    (2, 'education', '时间轴教育组件', '{"layout": "timeline", "showDuration": true, "showDescription": true}', 'React', 'content', '教育经历时间轴展示', '/thumbnails/education-timeline.jpg'),
    (3, 'work', '卡片式工作经历', '{"layout": "card", "showAchievements": true, "showSkills": true}', 'Vue', 'content', '工作经历卡片式布局', '/thumbnails/work-card.jpg'),
    (4, 'skills', '技能进度条组件', '{"layout": "progress", "showLevel": true, "maxLevel": 5}', 'React', 'content', '技能水平进度条展示', '/thumbnails/skills-progress.jpg');

    -- 插入简历模板组件关联数据
    INSERT INTO bole_app.t_resumes_template_component (id, template_id, component_id, component_type, component_name, component_config, sort, is_required) VALUES
    (1, 1, 1, 'header', '基础头部组件', '{"title": "个人简历", "showAvatar": true}', 1, true),
    (2, 1, 2, 'education', '时间轴教育组件', '{"layout": "timeline"}', 2, true),
    (3, 1, 3, 'work', '卡片式工作经历', '{"layout": "card"}', 3, true),
    (4, 1, 4, 'skills', '技能进度条组件', '{"layout": "progress"}', 4, false),
    (5, 2, 1, 'header', '基础头部组件', '{"title": "个人简历", "showAvatar": false}', 1, true),
    (6, 2, 2, 'education', '时间轴教育组件', '{"layout": "list"}', 2, true);

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

    -- 插入系统配置数据
    INSERT INTO bole_app.system_config (config_key, config_value, description, updated_by) VALUES
    ('site_name', '"技术伯乐"', '网站名称', 'admin'),
    ('max_file_size', '10485760', '最大文件上传大小(字节)', 'admin'),
    ('resume_template_count', '3', '可用简历模板数量', 'admin'),
    ('comment_audit_enabled', 'true', '是否启用评论审核', 'admin');

    -- 插入审计日志数据
    INSERT INTO bole_audit.audit_logs (id, table_name, record_id, action, old_data, new_data, changed_by) VALUES
    (1, 't_user', 1, 'UPDATE', '{"name": "张三", "title": "工程师"}', '{"name": "张三", "title": "高级工程师"}', 'zhangsan'),
    (2, 't_company_comment', 1, 'INSERT', NULL, '{"content": "公司文化很好...", "status": 0}', 'lisi'),
    (3, 't_resumes', 1, 'UPDATE', '{"status": "draft"}', '{"status": "active"}', 'zhangsan'),
    (4, 't_work_experiences', 2, 'INSERT', NULL, '{"position": "高级软件工程师", "company_id": 1}', 'zhangsan');


    -- 更新序列值，确保后续插入的主键不会冲突
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
    SELECT setval('bole_app.t_component_library_id_seq', (SELECT MAX(id) FROM bole_app.t_component_library));
    SELECT setval('bole_app.t_resumes_template_component_id_seq', (SELECT MAX(id) FROM bole_app.t_resumes_template_component));
    SELECT setval('bole_app.t_skill_id_seq', (SELECT MAX(id) FROM bole_app.t_skill));
    SELECT setval('bole_audit.audit_logs_id_seq', (SELECT MAX(id) FROM bole_audit.audit_logs));
EOSQL

echo "示例数据插入完成"