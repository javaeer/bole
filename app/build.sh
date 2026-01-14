#!/bin/bash
set -e

echo "🚀 定制Nginx镜像 + Uniapp HTTPS一键部署脚本"
echo "========================================="

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --domain DOMAIN           必填，网站域名"
    echo "  --email EMAIL             必填，管理员邮箱"
    echo "  --image IMAGE             可选，自定义Nginx镜像（默认使用内置Uniapp）"
    echo "  --api-server URL          可选，后台API服务器地址"
    echo "  --api-prefix PREFIX       可选，API路径前缀，默认/api"
    echo "  --enable-cors             可选，启用CORS跨域支持"
    echo "  --help                    显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 --domain app.example.com --email admin@example.com"
    echo "  $0 --domain app.example.com --email admin@example.com --api-server https://api.example.com"
    echo "  $0 --domain app.example.com --email admin@example.com --image mycompany/uniapp-nginx:latest"
    echo ""
    echo "说明:"
    echo "  - 默认使用内置Uniapp项目的定制Nginx镜像"
    echo "  - 无需提供Uniapp构建目录，文件已在镜像中"
    echo "  - 支持HTTPS自动配置和证书续期"
    exit 0
}

# 参数解析
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --domain)
                DOMAIN="$2"
                shift 2
                ;;
            --email)
                EMAIL="$2"
                shift 2
                ;;
            --image)
                NGINX_IMAGE="$2"
                shift 2
                ;;
            --api-server)
                API_SERVER="$2"
                shift 2
                ;;
            --api-prefix)
                API_PREFIX="$2"
                shift 2
                ;;
            --enable-cors)
                ENABLE_CORS=true
                shift
                ;;
            --help)
                show_help
                ;;
            *)
                echo "❌ 未知参数: $1"
                show_help
                ;;
        esac
    done
}

# 初始化参数
DOMAIN=""
EMAIL=""
NGINX_IMAGE="harbor.yunlou.net.cn/bole/bole-app:latest"  # 默认定制镜像
API_SERVER=""
API_PREFIX="/api"
ENABLE_CORS=false

# 解析参数
parse_arguments "$@"

# 验证必填参数
if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
    echo "❌ 错误: 必须提供域名和邮箱"
    echo ""
    show_help
fi

echo "📝 配置信息:"
echo "  - 域名: $DOMAIN"
echo "  - 邮箱: $EMAIL"
echo "  - Nginx镜像: $NGINX_IMAGE"
echo "  - 部署类型: 使用定制镜像（Uniapp文件内置）"

if [ -n "$API_SERVER" ]; then
    echo "  - 后台API服务器: $API_SERVER"
    echo "  - API路径前缀: $API_PREFIX"
    if [ "$ENABLE_CORS" = true ]; then
        echo "  - CORS跨域: 已启用"
    fi
else
    echo "  - 后台API服务器: 未配置"
fi

echo ""

echo "🔍 检查系统依赖..."
deps=("docker" "docker-compose" "curl")

for dep in "${deps[@]}"; do
    if ! command -v "$dep" &> /dev/null; then
        echo "❌ 缺少依赖: $dep"
        echo "   请安装: $dep"
        exit 1
    fi
done
echo "✅ 所有依赖已安装"

# 检查Docker镜像是否存在
echo "🔍 检查Nginx镜像..."
if ! docker image inspect "$NGINX_IMAGE" > /dev/null 2>&1; then
    echo "⚠️  警告: 镜像 $NGINX_IMAGE 不存在，将尝试拉取"
    if ! docker pull "$NGINX_IMAGE"; then
        echo "❌ 无法拉取镜像 $NGINX_IMAGE"
        echo "   请确保镜像存在且可访问"
        echo "   或者使用 --image 参数指定其他镜像"
        exit 1
    fi
else
    echo "✅ 镜像 $NGINX_IMAGE 已存在"
fi

# === 配置区（基于脚本目录的绝对路径）===
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BASE_DIR="$SCRIPT_DIR"

# 定义绝对路径变量
CONFIG_DIR="$BASE_DIR/config"
CERTS_DIR="$BASE_DIR/certs"
LOGS_DIR="$BASE_DIR/logs"
ACME_DIR="$BASE_DIR/acme-challenge"

# 计算安全的容器名称
# Docker容器名称只允许[a-zA-Z0-9][a-zA-Z0-9_.-]字符
CONTAINER_NAME_PREFIX=$(echo "uniapp-$(echo "$DOMAIN" | sed 's/[^a-zA-Z0-9_.-]/-/g')" | sed 's/--*/-/g' | sed 's/^-//' | sed 's/-$//')
TEMP_CONTAINER_NAME="temp-cert-$(date +%s)"

echo "🔧 容器名称前缀: $CONTAINER_NAME_PREFIX"
echo "🔧 临时容器名称: $TEMP_CONTAINER_NAME"

# ==============================

echo "🚀 开始部署定制Nginx镜像..."
echo "📝 配置信息:"
echo "   域名: $DOMAIN"
echo "   邮箱: $EMAIL"
echo "   Nginx镜像: $NGINX_IMAGE"
echo "   配置目录: $CONFIG_DIR"
echo "   证书目录: $CERTS_DIR"
echo "   基础目录: $BASE_DIR"

if [ -n "$API_SERVER" ]; then
    echo "   API代理: $API_SERVER"
    echo "   API前缀: $API_PREFIX"
fi

# 创建必要目录
echo "📁 创建目录结构..."
mkdir -p "$CONFIG_DIR" "$CERTS_DIR" "$LOGS_DIR" "$ACME_DIR"
chmod -R 777 "$ACME_DIR"
chmod -R 755 "$CONFIG_DIR"

# 创建健康检查文件（用于容器健康检查）
echo "📝 创建健康检查文件..."
cat > "$BASE_DIR/health.html" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>健康检查 - $DOMAIN</title>
    <meta charset="utf-8">
    <style>
        body {
            font-family: Arial, sans-serif;
            text-align: center;
            padding: 50px;
            background: #f5f5f5;
        }
        .container {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            display: inline-block;
        }
        .status {
            font-size: 24px;
            font-weight: bold;
            color: #52c41a;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚀 定制Nginx + Uniapp</h1>
        <div class="status">✅ 运行正常</div>
        <p>服务时间: $(date)</p>
        <p>镜像版本: $NGINX_IMAGE</p>
    </div>
</body>
</html>
EOF

# 创建API配置文件（如果配置了API服务器）
if [ -n "$API_SERVER" ]; then
    echo "⚙️ 配置API代理..."

    # 提取API服务器的主机和端口
    API_PROTOCOL=$(echo "$API_SERVER" | sed -n 's/^\(https\?\):\/\/.*/\1/p')
    API_HOST_PORT=$(echo "$API_SERVER" | sed -n 's/^https\?:\/\/\(.*\)/\1/p')

    if [ -z "$API_PROTOCOL" ] || [ -z "$API_HOST_PORT" ]; then
        echo "❌ 错误: API服务器地址格式不正确: $API_SERVER"
        echo "       正确格式: http://host:port 或 https://host:port"
        exit 1
    fi

    # 创建API代理配置
    cat > "$CONFIG_DIR/api-proxy.conf" << EOF
# API后端服务器配置
# 目标服务器: $API_SERVER
# 路径前缀: $API_PREFIX

upstream api_backend {
    server $API_HOST_PORT;

    # 连接池设置
    keepalive 32;
}

# API代理配置
location $API_PREFIX/ {
    proxy_pass $API_SERVER;
    proxy_http_version 1.1;
    proxy_set_header Upgrade \$http_upgrade;
    proxy_set_header Connection 'upgrade';
    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
    proxy_cache_bypass \$http_upgrade;

    # 超时设置
    proxy_connect_timeout 60s;
    proxy_send_timeout 60s;
    proxy_read_timeout 60s;

    # 缓冲设置
    proxy_buffering on;
    proxy_buffer_size 4k;
    proxy_buffers 8 4k;
    proxy_busy_buffers_size 8k;

    # CORS配置（如果启用）
    $(if [ "$ENABLE_CORS" = true ]; then
        echo "    # CORS跨域配置"
        echo "    add_header 'Access-Control-Allow-Origin' '*' always;"
        echo "    add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;"
        echo "    add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;"
        echo "    add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;"
        echo "    add_header 'Access-Control-Max-Age' 1728000 always;"

        echo "    # 处理OPTIONS请求"
        echo "    if (\$request_method = 'OPTIONS') {"
        echo "        add_header 'Access-Control-Allow-Origin' '*';"
        echo "        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';"
        echo "        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization';"
        echo "        add_header 'Access-Control-Max-Age' 1728000;"
        echo "        add_header 'Content-Type' 'text/plain; charset=utf-8';"
        echo "        add_header 'Content-Length' 0;"
        echo "        return 204;"
        echo "    }"
    fi)
}

# API健康检查端点
location $API_PREFIX/health {
    proxy_pass $API_SERVER/health;
    proxy_set_header Host \$host;
    proxy_set_header X-Real-IP \$remote_addr;
    proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto \$scheme;
    access_log off;
}
EOF
    echo "✅ API代理配置已生成"
fi

# 创建专门的临时NGINX配置（用于证书验证）
echo "⚙️ 创建专门的临时NGINX配置..."

cat > "$CONFIG_DIR/temp-cert-nginx.conf" << EOF
# 临时Nginx配置 - 仅用于证书验证
# 这个配置专门用于Let's Encrypt证书验证

events {
    worker_connections 1024;
}

http {
    server {
        listen 80;
        server_name $DOMAIN;

        # ACME挑战验证路径
        location /.well-known/acme-challenge/ {
            root /var/www/acme;
            try_files \$uri =404;
        }

        # 其他所有请求返回404
        location / {
            return 404;
        }
    }
}
EOF

echo "✅ 临时证书验证配置文件已生成"

# 生成最终NGINX配置（包含API代理）
echo "⚙️ 生成最终NGINX配置..."

cat > "$CONFIG_DIR/nginx.conf" << EOF
# 定制Nginx镜像配置 - 正式环境
# 生成时间: $(date)
# 域名: $DOMAIN
# 镜像: $NGINX_IMAGE
$(if [ -n "$API_SERVER" ]; then echo "# API服务器: $API_SERVER"; fi)

# HTTP服务器（重定向到HTTPS）
server {
    listen 80;
    server_name $DOMAIN;

    # ACME挑战验证路径（用于证书续期）
    location /.well-known/acme-challenge/ {
        root /var/www/acme;
        try_files \$uri =404;
        access_log off;
    }

    # 健康检查（允许HTTP访问）
    location /health {
        alias /var/www/health/;
        try_files /health.html =404;
        access_log off;
    }

    # HTTP重定向到HTTPS
    location / {
        return 301 https://\$host\$request_uri;
    }
}

# HTTPS服务器
server {
    listen 443 ssl http2;
    server_name $DOMAIN;

    # SSL证书配置
    ssl_certificate /etc/nginx/certs/fullchain.pem;
    ssl_certificate_key /etc/nginx/certs/privkey.pem;

    # SSL优化配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_session_tickets off;

    # 安全头
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options SAMEORIGIN always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # Gzip压缩
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/json
        application/javascript
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # 健康检查
    location /health {
        alias /var/www/health/;
        try_files /health.html =404;
        access_log off;
    }

    # 静态资源缓存优化
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot|mp4|webm|ogg|mp3|wav)$ {
        # 使用镜像内置的静态文件
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # Uniapp前端路由支持（History模式）
    location / {
        # 使用镜像内置的Uniapp应用
        try_files \$uri \$uri/ /index.html;

        # 安全限制
        location ~* \.(htaccess|htpasswd|ini|log|sh|bak|sql|env|key)$ {
            deny all;
            return 404;
        }
    }

    $(if [ -n "$API_SERVER" ]; then
        echo "    # 包含API代理配置"
        echo "    include /etc/nginx/conf.d/api-proxy.conf;"
    fi)

    # 禁止访问隐藏文件
    location ~ /\. {
        deny all;
        access_log off;
        log_not_found off;
    }

    # 错误页面
    error_page 404 /index.html;
    error_page 500 502 503 504 /50x.html;
    location = /50x.html {
        internal;
    }

    # 访问日志
    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log warn;
}
EOF

echo "✅ 最终配置文件已生成"

# 生成docker-compose.yaml
echo "📦 生成Docker Compose配置..."

cat > "$BASE_DIR/docker-compose.yaml" << EOF
services:
  uniapp-nginx:
    image: $NGINX_IMAGE
    container_name: $CONTAINER_NAME_PREFIX
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      # 证书目录
      - $CERTS_DIR:/etc/nginx/certs:ro
      # Nginx配置
      - $CONFIG_DIR/nginx.conf:/etc/nginx/conf.d/default.conf:ro
      $(if [ -n "$API_SERVER" ]; then
        echo "      # API代理配置"
        echo "      - $CONFIG_DIR/api-proxy.conf:/etc/nginx/conf.d/api-proxy.conf:ro"
      fi)
      # ACME挑战目录（用于证书续期）
      - $ACME_DIR:/var/www/acme:rw
      # 健康检查页面
      - $BASE_DIR/health.html:/var/www/health/health.html:ro
      # 日志目录
      - $LOGS_DIR:/var/log/nginx
    environment:
      - TZ=Asia/Shanghai
      - DOMAIN=$DOMAIN
      $(if [ -n "$API_SERVER" ]; then
        echo "      - API_SERVER=$API_SERVER"
        echo "      - API_PREFIX=$API_PREFIX"
      fi)
      - NGINX_ENV=production
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
    networks:
      - uniapp-network

networks:
  uniapp-network:
    driver: bridge
EOF

echo "✅ Docker Compose配置已生成"

# 停止可能冲突的服务
echo "🛑 停止可能冲突的服务..."
docker-compose down 2>/dev/null || true

# 确保80端口没有被占用
echo "🔍 检查端口占用情况..."
if ss -tuln | grep -q ":80 "; then
    echo "⚠️  端口80已被占用，尝试停止占用80端口的服务..."
    sudo fuser -k 80/tcp 2>/dev/null || true
    sleep 2
fi

if ss -tuln | grep -q ":443 "; then
    echo "⚠️  端口443已被占用，尝试停止占用443端口的服务..."
    sudo fuser -k 443/tcp 2>/dev/null || true
    sleep 2
fi

# 启动临时容器用于证书验证
echo ""
echo "📦 启动临时Nginx服务（证书验证）..."

# 创建测试验证文件
echo "创建测试验证文件..."
mkdir -p "$ACME_DIR/.well-known/acme-challenge"
echo "test-acme-validation" > "$ACME_DIR/.well-known/acme-challenge/test"

echo "启动临时证书验证容器..."
docker run -d \
  --name "$TEMP_CONTAINER_NAME" \
  -p 80:80 \
  -v "$ACME_DIR:/var/www/acme" \
  -v "$CONFIG_DIR/temp-cert-nginx.conf:/etc/nginx/nginx.conf:ro" \
  --rm \
  nginx:alpine

echo "⏳ 等待临时容器启动..."
sleep 5

# 检查临时容器状态
echo "🔍 检查临时容器状态..."
if docker ps | grep -q "$TEMP_CONTAINER_NAME"; then
    echo "✅ 临时容器已启动"
else
    echo "❌ 临时容器启动失败"
    docker logs "$TEMP_CONTAINER_NAME" 2>/dev/null || echo "无法获取容器日志"
    exit 1
fi

# 测试验证路径
echo "🔍 测试ACME验证路径..."

# 先测试本地访问
echo "测试本地访问..."
LOCAL_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/.well-known/acme-challenge/test 2>/dev/null || echo "FAIL")
if [ "$LOCAL_TEST" = "200" ]; then
    echo "✅ 本地验证路径可访问 (状态码: $LOCAL_TEST)"

    # 获取容器内文件内容验证
    CONTAINER_CONTENT=$(docker exec "$TEMP_CONTAINER_NAME" cat /var/www/acme/.well-known/acme-challenge/test 2>/dev/null || echo "无法读取")
    echo "容器内验证文件内容: $CONTAINER_CONTENT"
else
    echo "❌ 本地验证路径访问失败 (状态码: $LOCAL_TEST)"
    echo "调试信息:"
    docker exec "$TEMP_CONTAINER_NAME" ls -la /var/www/acme/.well-known/acme-challenge/ 2>/dev/null || echo "无法列出目录"
    docker logs "$TEMP_CONTAINER_NAME" --tail=10
    exit 1
fi

# 测试域名访问（如果域名已解析）
echo "测试域名访问..."
if command -v nslookup > /dev/null 2>&1 && nslookup "$DOMAIN" > /dev/null 2>&1; then
    DOMAIN_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://$DOMAIN/.well-known/acme-challenge/test 2>/dev/null || echo "FAIL")
    if [ "$DOMAIN_TEST" = "200" ]; then
        echo "✅ 域名验证路径可访问 (状态码: $DOMAIN_TEST)"
    else
        echo "⚠️  域名验证路径访问失败 (状态码: $DOMAIN_TEST)"
        echo "   可能域名未解析到当前服务器，或防火墙/安全组限制"
        echo "   当前服务器IP: $(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')"
    fi
else
    echo "⚠️  跳过域名测试，域名可能未解析"
fi

# 申请SSL证书
echo ""
echo "🔐 申请SSL证书..."
set +e

# 先运行dry-run测试
echo "运行预检查..."
docker run --rm \
    -v "$CERTS_DIR:/etc/letsencrypt" \
    -v "$ACME_DIR:/var/www/acme" \
    certbot/certbot certonly \
    --webroot \
    -w /var/www/acme \
    -d "$DOMAIN" \
    --non-interactive \
    --agree-tos \
    --email "$EMAIL" \
    --preferred-challenges http \
    --dry-run

DRY_RUN_RESULT=$?

if [ $DRY_RUN_RESULT -eq 0 ]; then
    echo "✅ 预检查通过，开始正式申请证书..."

    # 正式申请证书
    echo "正式申请证书..."
    docker run --rm \
        -v "$CERTS_DIR:/etc/letsencrypt" \
        -v "$ACME_DIR:/var/www/acme" \
        certbot/certbot certonly \
        --webroot \
        -w /var/www/acme \
        -d "$DOMAIN" \
        --non-interactive \
        --agree-tos \
        --email "$EMAIL" \
        --preferred-challenges http \
        --force-renewal

    CERT_RESULT=$?
else
    echo "❌ 预检查失败，跳过正式申请"
    echo "请检查以下问题："
    echo "1. 域名解析是否正确"
    echo "2. 80端口是否可访问"
    echo "3. 防火墙/安全组设置"
    CERT_RESULT=1
fi

set -e

# 停止临时容器
echo "🛑 停止临时容器..."
docker stop "$TEMP_CONTAINER_NAME" 2>/dev/null || true

if [ $CERT_RESULT -eq 0 ]; then
    echo "✅ SSL证书申请成功"

    # 查找证书文件
    echo "查找证书文件..."
    mkdir -p "$CERTS_DIR"

    # 查找证书文件的可能位置
    CERT_PATHS=(
        "$CERTS_DIR/live/$DOMAIN/fullchain.pem"
        "$CERTS_DIR/live/$DOMAIN/privkey.pem"
        "$CERTS_DIR/archive/$DOMAIN/fullchain1.pem"
        "$CERTS_DIR/archive/$DOMAIN/privkey1.pem"
        "$CERTS_DIR/archive/$DOMAIN/fullchain.pem"
        "$CERTS_DIR/archive/$DOMAIN/privkey.pem"
    )

    FULLCHAIN=""
    PRIVKEY=""

    for path in "${CERT_PATHS[@]}"; do
        if [ -f "$path" ]; then
            if [[ "$path" == *"fullchain"* ]]; then
                FULLCHAIN="$path"
                echo "找到证书链: $path"
            elif [[ "$path" == *"privkey"* ]]; then
                PRIVKEY="$path"
                echo "找到私钥: $path"
            fi
        fi
    done

    if [ -n "$FULLCHAIN" ] && [ -n "$PRIVKEY" ]; then
        # 复制证书到标准位置
        cp "$FULLCHAIN" "$CERTS_DIR/fullchain.pem"
        cp "$PRIVKEY" "$CERTS_DIR/privkey.pem"

        echo "✅ 证书已复制到标准位置"
        echo "📁 证书位置:"
        echo "   - 证书链: $CERTS_DIR/fullchain.pem"
        echo "   - 私钥: $CERTS_DIR/privkey.pem"

        # 验证证书
        if openssl x509 -in "$CERTS_DIR/fullchain.pem" -noout 2>/dev/null; then
            NOT_AFTER=$(openssl x509 -in "$CERTS_DIR/fullchain.pem" -enddate -noout | cut -d= -f2)
            echo "✅ 证书有效，过期时间: $NOT_AFTER"
        else
            echo "⚠️  警告: 证书文件格式可能不正确"
        fi
    else
        echo "⚠️  警告: 未能找到证书文件"
        echo "请手动检查证书目录: $CERTS_DIR"
        find "$CERTS_DIR" -name "*.pem" -type f 2>/dev/null | head -10
    fi
else
    echo "❌ SSL证书申请失败"
    echo ""
    echo "🔧 故障排除步骤:"
    echo ""
    echo "1. 检查域名解析:"
    echo "   nslookup $DOMAIN"
    echo "   dig $DOMAIN +short"
    echo ""
    echo "2. 检查服务器IP:"
    SERVER_IP=$(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}' || ip addr show | grep -oP 'inet \K[\d.]+' | grep -v '127.0.0.1' | head -1)
    echo "   当前服务器IP: $SERVER_IP"
    echo ""
    echo "3. 测试端口访问:"
    echo "   curl -v http://$DOMAIN/.well-known/acme-challenge/test"
    echo "   curl -v http://localhost/.well-known/acme-challenge/test"
    echo ""
    echo "4. 检查临时容器日志:"
    echo "   docker logs $TEMP_CONTAINER_NAME"
    echo ""
    echo "5. 手动运行certbot测试:"
    echo "   docker run --rm -v $CERTS_DIR:/etc/letsencrypt -v $ACME_DIR:/var/www/acme certbot/certbot certonly --webroot -w /var/www/acme -d $DOMAIN --non-interactive --agree-tos --email $EMAIL --preferred-challenges http --dry-run"
    exit 1
fi

# 启动正式容器
echo ""
echo "🚀 启动正式Nginx服务..."
docker-compose up -d

echo "⏳ 等待服务启动..."
sleep 8

# 验证部署
echo ""
echo "🔍 验证部署..."

# 检查容器状态
echo "检查容器状态..."
if docker-compose ps | grep -q "Up"; then
    echo "✅ 容器运行正常"
else
    echo "❌ 容器启动失败"
    docker-compose logs --tail=20
    exit 1
fi

echo "测试HTTP重定向..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost 2>/dev/null || echo "FAIL")
if [ "$HTTP_STATUS" = "301" ] || [ "$HTTP_STATUS" = "302" ]; then
    echo "✅ HTTP重定向正常 (状态码: $HTTP_STATUS)"
else
    echo "⚠️  HTTP重定向可能有问题 (状态码: $HTTP_STATUS)"
fi

echo "测试HTTPS连接..."
HTTPS_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --insecure https://localhost/health 2>/dev/null || echo "FAIL")
if [ "$HTTPS_STATUS" = "200" ]; then
    echo "✅ HTTPS连接正常 (状态码: $HTTPS_STATUS)"
else
    echo "❌ HTTPS连接失败 (状态码: $HTTPS_STATUS)"
    echo "检查证书配置..."
    docker-compose exec uniapp-nginx nginx -t 2>&1 || echo "Nginx配置测试失败"
    docker-compose logs --tail=20
fi

# 测试API代理（如果配置了）
if [ -n "$API_SERVER" ]; then
    echo "测试API代理..."
    API_STATUS=$(curl -s -o /dev/null -w "%{http_code}" --insecure https://localhost$API_PREFIX/health 2>/dev/null || echo "FAIL")
    if [ "$API_STATUS" = "200" ]; then
        echo "✅ API代理配置成功 (状态码: $API_STATUS)"
    else
        echo "⚠️  API代理连接失败 (状态码: $API_STATUS)"
        echo "   请检查后台服务器 $API_SERVER 是否运行"
    fi
fi

# 设置自动续期
echo ""
echo "🔄 配置SSL证书自动续期..."
cat > "$BASE_DIR/renew-cert.sh" << EOF
#!/bin/bash
echo "\$(date): 开始证书续期流程..."

# 创建临时容器名称
TEMP_NAME="cert-renew-\$(date +%s)"

# 启动临时Nginx用于验证
echo "启动临时验证容器..."
docker run -d \\
  --name "\$TEMP_NAME" \\
  -p 80:80 \\
  -v $ACME_DIR:/var/www/acme \\
  -v $CONFIG_DIR/temp-cert-nginx.conf:/etc/nginx/nginx.conf:ro \\
  --rm \\
  nginx:alpine

# 等待服务启动
sleep 5

# 续期证书
echo "续期SSL证书..."
docker run --rm \\
  -v "$CERTS_DIR:/etc/letsencrypt" \\
  -v "$ACME_DIR:/var/www/acme" \\
  certbot/certbot renew \\
  --webroot -w /var/www/acme \\
  --quiet

RENEW_RESULT=\$?

# 停止临时容器
echo "停止临时容器..."
docker stop "\$TEMP_NAME" 2>/dev/null || true

if [ \$RENEW_RESULT -eq 0 ]; then
    # 复制新证书
    echo "查找并复制新证书..."
    if [ -f "$CERTS_DIR/live/$DOMAIN/fullchain.pem" ]; then
        cp "$CERTS_DIR/live/$DOMAIN/fullchain.pem" "$CERTS_DIR/fullchain.pem"
        cp "$CERTS_DIR/live/$DOMAIN/privkey.pem" "$CERTS_DIR/privkey.pem"
        echo "\$(date): ✅ 证书已更新"

        # 重启Nginx使新证书生效
        echo "重启Nginx服务..."
        docker-compose restart uniapp-nginx
        echo "✅ Nginx已重启加载新证书"
    else
        echo "\$(date): ⚠️  证书已续期但未找到新证书文件"
    fi
else
    echo "\$(date): ⚠️  证书续期检查完成（无需更新）"
fi

# 清理旧日志（保留30天）
find $LOGS_DIR -name "*.log" -mtime +30 -delete 2>/dev/null || true

echo "\$(date): 证书续期流程结束"
EOF

chmod +x "$BASE_DIR/renew-cert.sh"

# 添加到定时任务
echo "添加自动续期到crontab..."
CRON_JOB="0 3 * * * $BASE_DIR/renew-cert.sh >> $BASE_DIR/renew.log 2>&1"
if crontab -l 2>/dev/null | grep -q "renew-cert.sh"; then
    echo "⚠️  定时任务已存在，跳过添加"
else
    (crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -
    echo "✅ 自动续期已配置（每天凌晨3点执行）"
fi

# 创建管理脚本
echo "📝 创建管理脚本..."
cat > "$BASE_DIR/manage.sh" << EOF
#!/bin/bash
SCRIPT_DIR="\$(cd "\$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
cd "\$SCRIPT_DIR"

ACTION="\${1:-help}"

case "\$ACTION" in
    start)
        echo "🚀 启动服务..."
        docker-compose up -d
        echo "✅ 服务已启动"
        ;;
    stop)
        echo "🛑 停止服务..."
        docker-compose down
        echo "✅ 服务已停止"
        ;;
    restart)
        echo "🔄 重启服务..."
        docker-compose restart
        echo "✅ 服务已重启"
        ;;
    status)
        echo "📊 服务状态:"
        docker-compose ps
        ;;
    logs)
        echo "📋 查看日志（按Ctrl+C退出）:"
        docker-compose logs -f uniapp-nginx
        ;;
    shell)
        echo "🐚 进入容器Shell:"
        docker-compose exec uniapp-nginx sh
        ;;
    update-image)
        IMAGE_NAME="\${2:-$NGINX_IMAGE}"
        echo "🔄 更新Nginx镜像到: \$IMAGE_NAME"
        sed -i "s|image: .*|image: \$IMAGE_NAME|" docker-compose.yaml
        docker-compose pull uniapp-nginx
        docker-compose up -d
        echo "✅ 镜像已更新"
        ;;
    config)
        echo "📋 当前配置:"
        echo "   域名: $DOMAIN"
        echo "   邮箱: $EMAIL"
        echo "   Nginx镜像: $NGINX_IMAGE"
        if [ -n "$API_SERVER" ]; then
            echo "   API服务器: $API_SERVER"
            echo "   API前缀: $API_PREFIX"
        fi
        echo "   证书目录: $CERTS_DIR"
        echo "   日志目录: $LOGS_DIR"
        echo "   容器名称: $CONTAINER_NAME_PREFIX"
        echo ""
        echo "📁 目录结构:"
        ls -la .
        ;;
    test-ssl)
        echo "🔐 测试SSL证书:"
        if [ -f "$CERTS_DIR/fullchain.pem" ]; then
            echo "✅ 证书文件存在"
            openssl x509 -in "$CERTS_DIR/fullchain.pem" -text -noout 2>/dev/null | grep -A2 "Validity" || echo "无法读取证书信息"
        else
            echo "❌ 证书文件不存在"
            echo "检查路径: $CERTS_DIR/fullchain.pem"
        fi
        ;;
    test-acme)
        echo "🔍 测试ACME验证路径:"
        mkdir -p "$ACME_DIR/.well-known/acme-challenge"
        echo "test-\$(date +%s)" > "$ACME_DIR/.well-known/acme-challenge/test"
        echo "测试文件已创建: $ACME_DIR/.well-known/acme-challenge/test"
        echo "本地测试: curl http://localhost/.well-known/acme-challenge/test"
        echo "域名测试: curl http://$DOMAIN/.well-known/acme-challenge/test"
        ;;
    renew)
        echo "🔄 手动续期证书..."
        ./renew-cert.sh
        ;;
    backup)
        BACKUP_DIR="backups/\$(date +%Y%m%d_%H%M%S)"
        echo "💾 备份配置到: \$BACKUP_DIR"
        mkdir -p "\$BACKUP_DIR"
        cp -r certs "\$BACKUP_DIR/" 2>/dev/null || true
        cp -r config "\$BACKUP_DIR/" 2>/dev/null || true
        cp docker-compose.yaml "\$BACKUP_DIR/" 2>/dev/null || true
        echo "✅ 备份完成: \$BACKUP_DIR"
        ;;
    test-domain)
        echo "🌐 测试域名解析:"
        echo "域名: $DOMAIN"
        echo "解析结果:"
        nslookup "$DOMAIN" 2>/dev/null || dig "$DOMAIN" +short 2>/dev/null || echo "无法解析域名"
        echo ""
        echo "服务器IP:"
        curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print \$1}'
        echo ""
        echo "测试HTTP访问:"
        curl -I "http://$DOMAIN" 2>/dev/null | head -5 || echo "HTTP访问失败"
        echo ""
        echo "测试HTTPS访问:"
        curl -I --insecure "https://$DOMAIN" 2>/dev/null | head -5 || echo "HTTPS访问失败"
        ;;
    help|*)
        echo "📋 定制Nginx镜像管理脚本"
        echo ""
        echo "用法: \$0 <命令> [参数]"
        echo ""
        echo "命令列表:"
        echo "  start            启动服务"
        echo "  stop             停止服务"
        echo "  restart          重启服务"
        echo "  status           查看状态"
        echo "  logs             查看日志"
        echo "  shell            进入容器Shell"
        echo "  update-image [镜像] 更新Nginx镜像"
        echo "  config           查看配置"
        echo "  test-ssl         测试SSL证书"
        echo "  test-acme        测试ACME验证路径"
        echo "  test-domain      测试域名解析和访问"
        echo "  renew            手动续期证书"
        echo "  backup           备份配置"
        echo "  help             显示帮助"
        ;;
esac
EOF

chmod +x "$BASE_DIR/manage.sh"

# 创建快速测试脚本
cat > "$BASE_DIR/test-deployment.sh" << EOF
#!/bin/bash
echo "🧪 部署测试脚本"
echo "================"

# 测试基础连接
echo "1. 测试HTTP重定向..."
HTTP_STATUS=\$(curl -s -o /dev/null -w "%{http_code}" http://localhost 2>/dev/null || echo "FAIL")
if [ "\$HTTP_STATUS" = "301" ] || [ "\$HTTP_STATUS" = "302" ]; then
    echo "✅ HTTP重定向正常 (状态码: \$HTTP_STATUS)"
else
    echo "❌ HTTP重定向失败 (状态码: \$HTTP_STATUS)"
fi

echo ""
echo "2. 测试HTTPS连接..."
HTTPS_STATUS=\$(curl -s -o /dev/null -w "%{http_code}" --insecure https://localhost/health 2>/dev/null || echo "FAIL")
if [ "\$HTTPS_STATUS" = "200" ]; then
    echo "✅ HTTPS连接正常 (状态码: \$HTTPS_STATUS)"
else
    echo "❌ HTTPS连接失败 (状态码: \$HTTPS_STATUS)"
fi

echo ""
echo "3. 测试SSL证书..."
if command -v openssl > /dev/null && [ -f "$CERTS_DIR/fullchain.pem" ]; then
    echo | openssl s_client -connect localhost:443 -servername localhost 2>/dev/null | grep -q "Verify return code" && echo "✅ SSL证书验证通过"
else
    echo "⚠️  跳过SSL证书测试 (openssl未安装或证书不存在)"
fi

echo ""
echo "4. 测试容器状态..."
if docker-compose ps | grep -q "Up"; then
    echo "✅ 容器运行正常"
    docker-compose ps
else
    echo "❌ 容器运行异常"
    docker-compose ps
fi

echo ""
echo "5. 测试API代理..."
if [ -n "$API_SERVER" ]; then
    API_STATUS=\$(curl -s -o /dev/null -w "%{http_code}" --insecure https://localhost$API_PREFIX/health 2>/dev/null || echo "FAIL")
    if [ "\$API_STATUS" = "200" ]; then
        echo "✅ API代理正常 (状态码: \$API_STATUS)"
    else
        echo "❌ API代理失败 (状态码: \$API_STATUS)"
    fi
else
    echo "⚠️  跳过API代理测试 (未配置API服务器)"
fi

echo ""
echo "✅ 测试完成"
EOF

chmod +x "$BASE_DIR/test-deployment.sh"

# 显示部署完成信息
echo ""
echo "🎊 部署完成!"
echo "================"
echo ""
echo "✅ 定制Nginx镜像已成功部署"
echo ""
echo "🌐 访问地址:"
echo "   🔗 https://$DOMAIN"
echo "   🔗 http://$DOMAIN (自动重定向到HTTPS)"
echo ""
if [ -n "$API_SERVER" ]; then
    echo "🔗 API代理:"
    echo "   🔗 https://$DOMAIN$API_PREFIX"
    echo "   ➡️  目标: $API_SERVER"
    echo ""
fi
echo "📁 项目目录:"
echo "   $BASE_DIR/"
echo "   ├── docker-compose.yaml    # Docker配置"
echo "   ├── manage.sh              # 管理脚本"
echo "   ├── renew-cert.sh          # 证书续期脚本"
echo "   ├── test-deployment.sh     # 测试脚本"
echo "   ├── config/                # Nginx配置"
echo "   │   ├── nginx.conf         # 主配置"
echo "   │   ├── temp-cert-nginx.conf # 临时配置（证书验证用）"
echo "   │   └── api-proxy.conf     # API代理配置（如配置）"
echo "   ├── certs/                 # SSL证书"
echo "   │   ├── fullchain.pem      # 证书链"
echo "   │   └── privkey.pem        # 私钥"
echo "   ├── acme-challenge/        # ACME验证文件"
echo "   └── logs/                  # Nginx日志"
echo ""
echo "📋 管理命令:"
echo "   ./manage.sh start          # 启动服务"
echo "   ./manage.sh stop           # 停止服务"
echo "   ./manage.sh status         # 查看状态"
echo "   ./manage.sh logs           # 查看日志"
echo "   ./manage.sh renew          # 手动续期证书"
echo "   ./manage.sh test-ssl       # 测试SSL证书"
echo "   ./manage.sh test-acme      # 测试ACME验证路径"
echo "   ./manage.sh test-domain    # 测试域名解析"
echo ""
echo "🧪 快速测试:"
echo "   ./test-deployment.sh       # 运行部署测试"
echo ""
echo "🔧 配置说明:"
echo "   - Uniapp文件已内置在Nginx镜像中"
echo "   - HTTPS自动配置完成"
echo "   - 每天自动续期SSL证书"
echo "   - 详细的日志记录"
echo ""
echo "📊 服务状态:"
docker-compose ps
echo ""
echo "⏰ 证书自动续期: 每天凌晨3点"
echo "📅 证书有效期: 90天（Let's Encrypt）"
echo ""
echo "💡 提示:"
echo "   1. 如需更新Uniapp文件，请重新构建Docker镜像"
echo "   2. 如需修改Nginx配置，编辑 config/nginx.conf"
echo "   3. 查看实时日志: ./manage.sh logs"
echo "   4. 测试ACME验证: ./manage.sh test-acme"
echo "   5. 测试域名解析: ./manage.sh test-domain"
echo ""
echo "✅ 部署完成时间: $(date)"