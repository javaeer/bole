#!/bin/bash
set -e

echo "🚀 Uniapp + Nginx + HTTPS + API代理 一键部署脚本"
echo "=============================================="

# 显示帮助
show_help() {
  echo "用法: $0 [选项]"
  echo ""
  echo "必选参数:"
  echo "  --domain DOMAIN           网站域名"
  echo "  --email EMAIL             管理员邮箱"
  echo ""
  echo "可选参数:"
  echo "  --html-dir DIR            Uniapp构建目录（默认: 当前目录/html）"
  echo "  --api-server URL          后台API服务器地址（支持http/https）"
  echo "  --api-prefix PREFIX       API路径前缀（默认: /api）"
  echo "  --api-timeout SECONDS     API代理超时时间（默认: 30秒）"
  echo "  --enable-cors             启用CORS跨域支持"
  echo "  --init-only               仅初始化目录结构"
  echo "  --help                    显示此帮助"
  echo ""
  echo "示例:"
  echo "  1. 基础部署:"
  echo "     $0 --domain app.example.com --email admin@example.com"
  echo ""
  echo "  2. 带API服务器:"
  echo "     $0 --domain app.example.com --email admin@example.com \\"
  echo "         --api-server https://api.example.com"
  echo ""
  echo "  3. 自定义API配置:"
  echo "     $0 --domain app.example.com --email admin@example.com \\"
  echo "         --api-server http://backend:8080 \\"
  echo "         --api-prefix /v1/api \\"
  echo "         --api-timeout 60 \\"
  echo "         --enable-cors"
  echo ""
  echo "  4. 指定Uniapp构建目录:"
  echo "     $0 --domain app.example.com --email admin@example.com \\"
  echo "         --html-dir ./dist/build/h5"
  echo ""
  echo "工作流程:"
  echo "  1. 创建项目目录结构"
  echo "  2. 配置Nginx、HTTPS和API代理"
  echo "  3. 申请SSL证书"
  echo "  4. 启动服务"
  echo "  5. 后续只需更新html/目录中的文件"
  exit 0
}

# 解析参数
DOMAIN=""
EMAIL=""
HTML_DIR=""
API_SERVER=""
API_PREFIX="/api"
API_TIMEOUT="30"
ENABLE_CORS=false
INIT_ONLY=false

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
  --html-dir)
    HTML_DIR="$2"
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
  --api-timeout)
    API_TIMEOUT="$2"
    shift 2
    ;;
  --enable-cors)
    ENABLE_CORS=true
    shift
    ;;
  --init-only)
    INIT_ONLY=true
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

# 验证参数
if [ -z "$DOMAIN" ] || [ -z "$EMAIL" ]; then
  echo "❌ 错误: 必须提供域名和邮箱"
  show_help
fi

# 验证API服务器URL格式（如果提供）
if [ -n "$API_SERVER" ]; then
  if [[ ! $API_SERVER =~ ^https?:// ]]; then
    echo "❌ 错误: API服务器地址格式不正确"
    echo "    正确格式: http://hostname 或 https://hostname"
    echo "    当前值: $API_SERVER"
    exit 1
  fi
fi

echo "📋 部署配置:"
echo "  ✅ 域名: $DOMAIN"
echo "  ✅ 邮箱: $EMAIL"
if [ -n "$HTML_DIR" ]; then
  echo "  ✅ 源码目录: $HTML_DIR"
else
  echo "  ⚠️  源码目录: 未指定（将使用默认页面）"
fi

if [ -n "$API_SERVER" ]; then
  echo "  🔗 API服务器: $API_SERVER"
  echo "    ├─ 路径前缀: $API_PREFIX"
  echo "    ├─ 超时时间: ${API_TIMEOUT}秒"
  if [ "$ENABLE_CORS" = true ]; then
    echo "    └─ CORS跨域: 已启用"
  else
    echo "    └─ CORS跨域: 未启用"
  fi
else
  echo "  ⚠️  API服务器: 未配置"
fi
echo ""

# 检查依赖
echo "🔍 检查系统依赖..."
for cmd in docker docker-compose curl; do
  if ! command -v $cmd &>/dev/null; then
    echo "❌ 缺少依赖: $cmd"
    echo "   请安装:"
    echo "   - Docker: https://docs.docker.com/engine/install/"
    echo "   - Docker Compose: https://docs.docker.com/compose/install/"
    echo "   - curl: 通常系统自带，或通过包管理器安装"
    exit 1
  fi
done
echo "✅ 依赖检查通过"

# 创建项目目录
PROJECT_NAME="app-${DOMAIN//./-}"
PROJECT_DIR="$(pwd)/$PROJECT_NAME"
echo "📁 创建项目目录: $PROJECT_DIR"

# 创建所有必要的目录
mkdir -p "$PROJECT_DIR"/{config,certs,logs,acme-challenge,html,backups}
cd "$PROJECT_DIR"

# 记录配置
echo "记录部署配置..."
cat >.deploy-config <<EOF
# 部署配置文件
# 生成时间: $(date)
DOMAIN="$DOMAIN"
EMAIL="$EMAIL"
HTML_DIR="$HTML_DIR"
API_SERVER="$API_SERVER"
API_PREFIX="$API_PREFIX"
API_TIMEOUT="$API_TIMEOUT"
ENABLE_CORS="$ENABLE_CORS"
PROJECT_DIR="$PROJECT_DIR"
EOF

# 如果提供了HTML目录，复制文件
if [ -n "$HTML_DIR" ] && [ -d "$HTML_DIR" ]; then
  echo "📦 复制Uniapp构建文件..."
  echo "源目录: $HTML_DIR"
  echo "目标目录: $PROJECT_DIR/html"

  # 清空目标目录
  rm -rf "$PROJECT_DIR"/html/*

  # 复制文件，保持权限
  if command -v rsync >/dev/null 2>&1; then
    rsync -av "$HTML_DIR"/ "$PROJECT_DIR/html/" --exclude="node_modules" --exclude=".*"
  else
    cp -r "$HTML_DIR"/* "$PROJECT_DIR/html/" 2>/dev/null || {
      echo "⚠️ 复制文件时出错，尝试逐个复制..."
      find "$HTML_DIR" -maxdepth 1 -name "*" ! -name ".*" ! -name "node_modules" -exec cp -r {} "$PROJECT_DIR/html/" \;
    }
  fi

  echo "✅ 文件复制完成"

  # 检查是否有index.html
  if [ ! -f "$PROJECT_DIR/html/index.html" ]; then
    echo "⚠️ 警告: 未找到index.html文件"
    echo "       Uniapp项目通常需要构建到dist或build目录"
    echo "       请确保构建命令正确: npm run build:h5 或 yarn build:h5"
    echo "       当前html目录内容:"
    ls -la "$PROJECT_DIR/html/" 2>/dev/null || echo "目录为空"
  else
    echo "✅ 找到index.html文件"
  fi
else
  echo "📝 创建默认页面..."
  cat >html/index.html <<EOF
<!DOCTYPE html>
<html>
<head>
    <title>部署成功 - $DOMAIN</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        header {
            text-align: center;
            margin-bottom: 40px;
            padding: 20px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px;
        }
        h1 {
            margin: 0;
            font-size: 2.5em;
        }
        .status-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin: 20px 0;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .config-item {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }
        .config-label {
            font-weight: bold;
            color: #555;
        }
        .config-value {
            color: #1890ff;
            font-family: monospace;
        }
        .api-info {
            background: #e6f7ff;
            border-left: 4px solid #1890ff;
            padding: 15px;
            margin: 20px 0;
        }
        .next-steps {
            background: #f6ffed;
            border-left: 4px solid #52c41a;
            padding: 15px;
            margin: 20px 0;
        }
        code {
            background: #f5f5f5;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Monaco', 'Menlo', monospace;
        }
        .test-buttons {
            display: flex;
            gap: 10px;
            margin: 20px 0;
        }
        .test-btn {
            padding: 10px 20px;
            background: #1890ff;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
        }
        .test-btn:hover {
            background: #40a9ff;
        }
    </style>
</head>
<body>
    <header>
        <h1>🚀 部署成功!</h1>
        <p>Uniapp项目HTTPS环境已就绪</p>
    </header>

    <div class="status-card">
        <h2>📋 部署信息</h2>
        <div class="config-item">
            <span class="config-label">域名:</span>
            <span class="config-value">$DOMAIN</span>
        </div>
        <div class="config-item">
            <span class="config-label">HTTPS状态:</span>
            <span class="config-value" style="color: #52c41a;">✅ 已启用</span>
        </div>
        <div class="config-item">
            <span class="config-label">部署目录:</span>
            <span class="config-value">$PROJECT_DIR</span>
        </div>
        <div class="config-item">
            <span class="config-label">部署时间:</span>
            <span class="config-value">$(date)</span>
        </div>
    </div>

    $(if [ -n "$API_SERVER" ]; then
    echo "<div class='api-info'>
        <h3>🔗 API代理配置</h3>
        <div class='config-item'>
            <span class='config-label'>服务器地址:</span>
            <span class='config-value'>$API_SERVER</span>
        </div>
        <div class='config-item'>
            <span class='config-label'>路径前缀:</span>
            <span class='config-value'>$API_PREFIX</span>
        </div>
        <div class='config-item'>
            <span class='config-label'>超时时间:</span>
            <span class='config-value'>${API_TIMEOUT}秒</span>
        </div>
        $(if [ "$ENABLE_CORS" = true ]; then
      echo "<div class='config-item'>
                <span class='config-label'>CORS支持:</span>
                <span class='config-value' style='color: #52c41a;'>✅ 已启用</span>
            </div>"
    fi)
    </div>"
  fi)

    <div class="next-steps">
        <h3>📝 下一步操作</h3>
        <ol>
            <li>在本地构建Uniapp项目：<code>npm run build:h5</code></li>
            <li>将构建文件上传到：<code>$PROJECT_DIR/html/</code></li>
            <li>访问网站：<a href="https://$DOMAIN" target="_blank">https://$DOMAIN</a></li>
        </ol>

        <div class="test-buttons">
            <a href="https://$DOMAIN" class="test-btn" target="_blank">访问网站</a>
            $(if [ -n "$API_SERVER" ]; then
    echo "<a href=\"https://$DOMAIN$API_PREFIX/health\" class=\"test-btn\" target=\"_blank\">测试API</a>"
  fi)
            <a href="http://$DOMAIN" class="test-btn" target="_blank">测试重定向</a>
        </div>
    </div>

    <div class="status-card">
        <h3>🛠️ 管理命令</h3>
        <p>进入项目目录后执行：</p>
        <pre><code>cd $PROJECT_DIR
./manage.sh status    # 查看状态
./manage.sh logs      # 查看日志
./manage.sh update &lt;目录&gt;  # 更新文件
./manage.sh info      # 查看信息</code></pre>
    </div>

    <footer style="text-align: center; margin-top: 40px; color: #666; font-size: 0.9em;">
        <p>部署完成时间: $(date)</p>
        <p>如需帮助，请查看目录中的 README.md 文件</p>
    </footer>

    <script>
        // 页面加载后自动测试API
        window.addEventListener('load', function() {
            $(if [ -n "$API_SERVER" ]; then
    echo "setTimeout(function() {
                    fetch('https://$DOMAIN$API_PREFIX/health')
                        .then(response => {
                            if (response.ok) {
                                console.log('API连接正常');
                            } else {
                                console.warn('API连接失败，状态码:', response.status);
                            }
                        })
                        .catch(error => {
                            console.warn('API连接错误:', error.message);
                        });
                }, 1000);"
  fi)
        });
    </script>
</body>
</html>
EOF
  echo "✅ 默认页面已创建"
fi

# 生成Nginx配置
echo "⚙️ 生成Nginx配置..."

# 生成API代理配置块
API_CONFIG_BLOCK=""
if [ -n "$API_SERVER" ]; then
  # 提取主机和端口
  API_PROTOCOL=$(echo "$API_SERVER" | sed -n 's/^\(https\?\):\/\/.*/\1/p')
  API_HOST_PORT=$(echo "$API_SERVER" | sed -n 's/^https\?:\/\/\(.*\)/\1/p')

  # 生成CORS配置
  CORS_CONFIG=""
  if [ "$ENABLE_CORS" = true ]; then
    CORS_CONFIG=$(
      cat <<'EOF'
        # CORS跨域配置
        add_header 'Access-Control-Allow-Origin' '*' always;
        add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS' always;
        add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization' always;
        add_header 'Access-Control-Expose-Headers' 'Content-Length,Content-Range' always;
        add_header 'Access-Control-Max-Age' 1728000 always;

        # 处理OPTIONS预检请求
        if ($request_method = 'OPTIONS') {
            add_header 'Access-Control-Allow-Origin' '*';
            add_header 'Access-Control-Allow-Methods' 'GET, POST, PUT, DELETE, OPTIONS';
            add_header 'Access-Control-Allow-Headers' 'DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range,Authorization';
            add_header 'Access-Control-Max-Age' 1728000;
            add_header 'Content-Type' 'text/plain; charset=utf-8';
            add_header 'Content-Length' 0;
            return 204;
        }
EOF
    )
  fi

  # 生成API代理配置
  API_CONFIG_BLOCK=$(
    cat <<EOF
    # API代理配置
    location $API_PREFIX/ {
        proxy_pass $API_SERVER/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host \$proxy_host; #取决于后端要求 如后端要求使用前端域名，则为\$host
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_cache_bypass \$http_upgrade;

        # 超时设置
        proxy_connect_timeout ${API_TIMEOUT}s;
        proxy_send_timeout ${API_TIMEOUT}s;
        proxy_read_timeout ${API_TIMEOUT}s;

        # 缓冲设置
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
        proxy_busy_buffers_size 8k;

        # 错误处理
        proxy_intercept_errors on;
        error_page 500 502 503 504 = @api_error;

        $CORS_CONFIG
    }

    # API错误处理
    location @api_error {
        add_header Content-Type application/json;
        return 500 '{"error": "API Gateway Error", "message": "Backend service unavailable", "timestamp": "$(date -Iseconds)"}';
    }

    # API健康检查（特殊处理）
    location $API_PREFIX/health {
        proxy_pass $API_SERVER/health;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        access_log off;

        $(if [ "$ENABLE_CORS" = true ]; then
      echo "        add_header 'Access-Control-Allow-Origin' '*' always;"
    fi)
    }
EOF
  )
fi

# 生成主Nginx配置
cat >config/nginx.conf <<EOF
# Uniapp项目Nginx配置
# 生成时间: $(date)
# 域名: $DOMAIN
$(if [ -n "$API_SERVER" ]; then echo "# API服务器: $API_SERVER"; fi)

# HTTP服务器（重定向到HTTPS）
server {
    listen 80;
    server_name $DOMAIN;

    # ACME挑战验证
    location /.well-known/acme-challenge/ {
        root /var/www/acme;
        try_files \$uri =404;
        access_log off;
    }

    # 健康检查端点
    location /health {
        add_header Content-Type application/json;
        return 200 '{"status": "healthy", "service": "nginx", "timestamp": "$(date -Iseconds)", "ssl": false}';
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

    # SSL证书
    ssl_certificate /etc/nginx/certs/fullchain.pem;
    ssl_certificate_key /etc/nginx/certs/privkey.pem;

    # SSL配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # 安全头
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options SAMEORIGIN always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;

    # 性能优化
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/json
        application/xml+rss
        application/atom+xml
        image/svg+xml;

    # 静态文件根目录
    root /var/www/html;
    index index.html;

    # 健康检查端点
    location /health {
        add_header Content-Type application/json;
        return 200 '{"status": "healthy", "service": "nginx", "timestamp": "$(date -Iseconds)", "ssl": true, "domain": "$DOMAIN"}';
        access_log off;
    }

    # Uniapp前端路由支持（History模式）
    location / {
        try_files \$uri \$uri/ /index.html;

        # 安全限制
        location ~* \.(htaccess|htpasswd|ini|log|sh|bak|sql|env|key)$ {
            deny all;
            return 404;
        }
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot|mp4|webm|ogg|mp3|wav)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
        access_log off;
    }

    # API代理配置
    $API_CONFIG_BLOCK

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
        root /var/www/html;
        internal;
    }
}
EOF

echo "✅ Nginx配置已生成"

# 生成简单的临时配置（用于证书申请）
cat >config/nginx-temp.conf <<EOF
# 临时配置 - 仅用于证书申请
server {
    listen 80;
    server_name $DOMAIN;

    location /.well-known/acme-challenge/ {
        root /var/www/acme;
        try_files \$uri =404;
    }

    location / {
        return 404;
    }
}
EOF

echo "✅ 临时配置已生成"

# 生成Docker Compose配置
echo "📦 生成Docker Compose配置..."

cat >docker-compose.yml <<EOF
version: '3.8'

services:
  nginx:
    image: nginx:alpine
    container_name: nginx-$PROJECT_NAME
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./certs:/etc/nginx/certs:ro
      - ./config/nginx.conf:/etc/nginx/conf.d/default.conf:ro
      - ./html:/var/www/html:ro
      - ./acme-challenge:/var/www/acme:rw
      - ./logs:/var/log/nginx
    environment:
      - TZ=Asia/Shanghai
      - DOMAIN=$DOMAIN
      $(if [ -n "$API_SERVER" ]; then
  echo "      - API_SERVER=$API_SERVER"
  echo "      - API_PREFIX=$API_PREFIX"
fi)
    healthcheck:
      test: ["CMD", "wget", "--no-verbose", "--tries=1", "--spider", "http://localhost/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    logging:
      driver: "json-file"
      options:
        max-size: "10m"
        max-file: "3"
EOF

echo "✅ Docker Compose配置已生成"

# 如果只是初始化，则退出
if [ "$INIT_ONLY" = true ]; then
  echo ""
  echo "✅ 初始化完成!"
  echo "目录已创建: $PROJECT_DIR"
  echo ""
  echo "📁 重要目录:"
  echo "  网站文件: $PROJECT_DIR/html/"
  echo "  配置文件: $PROJECT_DIR/config/"
  echo "  证书文件: $PROJECT_DIR/certs/"
  echo ""
  echo "📋 下一步操作:"
  echo "  1. 将Uniapp构建文件放入 html/ 目录"
  echo "  2. 运行: ./deploy.sh --domain $DOMAIN --email $EMAIL"
  $(if [ -n "$API_SERVER" ]; then
    echo "  3. 注意: 已配置API代理到 $API_SERVER"
  fi)
  exit 0
fi

# 停止可能冲突的服务
echo "🛑 停止可能冲突的服务..."
docker-compose down 2>/dev/null || true

# 确保80端口可用
echo "🔍 释放80端口..."
if command -v ss >/dev/null 2>&1; then
  if ss -tuln | grep -q ":80 "; then
    echo "端口80被占用，尝试释放..."
    sudo fuser -k 80/tcp 2>/dev/null || true
    sleep 2
  fi
else
  echo "跳过端口检查（ss命令不可用）"
fi

# 申请SSL证书
echo ""
echo "🔐 申请SSL证书..."

# 创建ACME验证目录结构
mkdir -p acme-challenge/.well-known/acme-challenge

# 启动临时容器进行证书申请
echo "启动临时验证容器..."
TEMP_CONTAINER="certbot-temp-$(date +%s)"
docker run -d \
  --name "$TEMP_CONTAINER" \
  -p 80:80 \
  -v "$(pwd)/acme-challenge:/var/www/acme" \
  -v "$(pwd)/config/nginx-temp.conf:/etc/nginx/conf.d/default.conf" \
  --rm \
  nginx:alpine

echo "等待容器启动..."
sleep 5

# 测试验证路径
echo "测试ACME验证路径..."
TEST_FILE="test-$(date +%s).txt"
echo "certbot-validation-$TEST_FILE" >acme-challenge/.well-known/acme-challenge/$TEST_FILE

# 测试本地访问
if curl -s "http://localhost/.well-known/acme-challenge/$TEST_FILE" | grep -q "certbot-validation"; then
  echo "✅ ACME验证路径本地可访问"

  # 测试域名访问（可选）
  if ping -c 1 "$DOMAIN" >/dev/null 2>&1; then
    if curl -s "http://$DOMAIN/.well-known/acme-challenge/$TEST_FILE" 2>/dev/null | grep -q "certbot-validation"; then
      echo "✅ ACME验证路径域名可访问"
    else
      echo "⚠️  ACME验证路径域名不可访问（可能域名未解析）"
    fi
  else
    echo "⚠️  跳过域名测试（无法ping通域名）"
  fi
else
  echo "❌ ACME验证路径本地无法访问"
  echo "调试信息:"
  docker logs "$TEMP_CONTAINER" 2>/dev/null || echo "无法获取容器日志"
  docker stop "$TEMP_CONTAINER" 2>/dev/null || true
  exit 1
fi

# 申请证书
echo "申请Let's Encrypt证书..."
set +e
docker run --rm \
  -v "$(pwd)/certs:/etc/letsencrypt" \
  -v "$(pwd)/acme-challenge:/var/www/acme" \
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
set -e

# 停止临时容器
docker stop "$TEMP_CONTAINER" 2>/dev/null || true

if [ $CERT_RESULT -eq 0 ]; then
  echo "✅ 证书申请成功"

  # 查找并复制证书文件
  echo "准备证书文件..."
  CERT_FOUND=false

  # 查找证书的可能位置
  CERT_PATHS=(
    "certs/live/$DOMAIN/fullchain.pem"
    "certs/live/$DOMAIN/privkey.pem"
    "certs/archive/$DOMAIN/fullchain1.pem"
    "certs/archive/$DOMAIN/privkey1.pem"
  )

  for cert_path in "${CERT_PATHS[@]}"; do
    if [ -f "$cert_path" ]; then
      echo "找到证书文件: $cert_path"
      if [[ "$cert_path" == *"fullchain"* ]]; then
        cp "$cert_path" certs/fullchain.pem
        echo "复制证书链到 certs/fullchain.pem"
        CERT_FOUND=true
      elif [[ "$cert_path" == *"privkey"* ]]; then
        cp "$cert_path" certs/privkey.pem
        echo "复制私钥到 certs/privkey.pem"
      fi
    fi
  done

  if [ "$CERT_FOUND" = true ] && [ -f "certs/fullchain.pem" ] && [ -f "certs/privkey.pem" ]; then
    echo "✅ 证书准备完成"

    # 验证证书信息
    if command -v openssl >/dev/null 2>&1; then
      echo "证书信息:"
      openssl x509 -in certs/fullchain.pem -noout -subject | sed 's/subject=//'
      openssl x509 -in certs/fullchain.pem -noout -dates | sed 's/notBefore=/生效时间: /; s/notAfter=/过期时间: /'
    fi
  else
    echo "❌ 证书文件准备失败"
    echo "请手动检查证书目录:"
    find certs -type f -name "*.pem" | head -10
    exit 1
  fi
else
  echo "❌ 证书申请失败"
  echo ""
  echo "🔧 故障排查步骤:"
  echo ""
  echo "1. 检查域名解析:"
  echo "   nslookup $DOMAIN"
  echo "   dig +short $DOMAIN"
  echo ""
  echo "2. 检查服务器IP:"
  echo "   当前IP: $(curl -s ifconfig.me 2>/dev/null || hostname -I | awk '{print $1}')"
  echo ""
  echo "3. 检查端口80:"
  echo "   curl -I http://localhost/.well-known/acme-challenge/$TEST_FILE"
  echo "   curl -I http://$DOMAIN/.well-known/acme-challenge/$TEST_FILE"
  echo ""
  echo "4. 手动申请证书（备选方案）:"
  echo "   sudo apt install certbot"
  echo "   sudo certbot certonly --standalone -d $DOMAIN --email $EMAIL --agree-tos"
  echo "   然后复制证书到 $PROJECT_DIR/certs/"
  echo ""
  exit 1
fi

# 启动正式服务
echo ""
echo "🚀 启动Nginx服务..."
docker-compose up -d

echo "等待服务启动..."
sleep 10

# 验证部署
echo ""
echo "🔍 验证部署..."

# 检查容器状态
if docker ps | grep -q "nginx-$PROJECT_NAME"; then
  echo "✅ 容器运行正常"
else
  echo "❌ 容器启动失败"
  docker-compose logs
  exit 1
fi

# 测试HTTP重定向
echo "测试HTTP重定向..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost 2>/dev/null || echo "FAIL")
if [ "$HTTP_STATUS" = "301" ] || [ "$HTTP_STATUS" = "302" ]; then
  echo "✅ HTTP自动重定向到HTTPS (状态码: $HTTP_STATUS)"
else
  echo "⚠️  HTTP重定向异常 (状态码: $HTTP_STATUS)"
fi

# 测试HTTPS连接
echo "测试HTTPS连接..."
if curl -s -k "https://localhost/health" >/dev/null 2>&1; then
  echo "✅ HTTPS连接成功"

  # 获取健康检查信息
  HEALTH_INFO=$(curl -s -k "https://localhost/health")
  echo "健康状态: $HEALTH_INFO"
else
  echo "❌ HTTPS连接失败"
  echo "检查Nginx配置..."
  docker-compose logs --tail=20
fi

# 测试API代理（如果配置了）
if [ -n "$API_SERVER" ]; then
  echo "测试API代理..."
  if curl -s -k "https://localhost$API_PREFIX/health" >/dev/null 2>&1; then
    echo "✅ API代理连接成功"
  else
    echo "⚠️  API代理连接失败"
    echo "   请检查:"
    echo "   1. API服务器 $API_SERVER 是否运行"
    echo "   2. API服务器是否开放访问"
    echo "   3. 网络连接是否正常"
  fi
fi

# 创建证书续期脚本
echo ""
echo "🔄 创建证书续期脚本..."

cat >renew-cert.sh <<EOF
#!/bin/bash
echo "\$(date): 开始证书续期流程..."

cd "$PROJECT_DIR"

# 创建临时验证容器
TEMP_NAME="cert-renew-\$(date +%s)"
docker run -d \\
  --name "\$TEMP_NAME" \\
  -p 80:80 \\
  -v "\$(pwd)/acme-challenge:/var/www/acme" \\
  -v "\$(pwd)/config/nginx-temp.conf:/etc/nginx/conf.d/default.conf" \\
  --rm \\
  nginx:alpine

sleep 5

# 续期证书
docker run --rm \\
  -v "\$(pwd)/certs:/etc/letsencrypt" \\
  -v "\$(pwd)/acme-challenge:/var/www/acme" \\
  certbot/certbot renew \\
  --webroot -w /var/www/acme \\
  --quiet

RENEW_RESULT=\$?

# 停止临时容器
docker stop "\$TEMP_NAME" 2>/dev/null || true

if [ \$RENEW_RESULT -eq 0 ]; then
    # 更新证书文件
    echo "更新证书文件..."
    if [ -f "certs/live/$DOMAIN/fullchain.pem" ]; then
        cp certs/live/$DOMAIN/fullchain.pem certs/fullchain.pem
        cp certs/live/$DOMAIN/privkey.pem certs/privkey.pem
        echo "\$(date): ✅ 证书已更新"

        # 重启Nginx
        docker-compose restart nginx
        echo "✅ Nginx已重启加载新证书"
    else
        echo "\$(date): ⚠️  证书已续期但未找到新证书文件"
    fi
else
    echo "\$(date): ⚠️  证书续期检查完成（无需更新）"
fi

# 清理旧日志
find logs -name "*.log" -mtime +30 -delete 2>/dev/null || true

echo "\$(date): 证书续期流程结束"
EOF

chmod +x renew-cert.sh

# 添加定时任务
echo "配置自动续期..."
CRON_JOB="0 3 * * * $PROJECT_DIR/renew-cert.sh >> $PROJECT_DIR/renew.log 2>&1"
if crontab -l 2>/dev/null | grep -q "renew-cert.sh"; then
  echo "⚠️  续期任务已存在，跳过添加"
else
  (
    crontab -l 2>/dev/null
    echo "$CRON_JOB"
  ) | crontab -
  echo "✅ 每天凌晨3点自动续期"
fi

# 创建管理脚本
echo "📝 创建管理脚本..."

cat >manage.sh <<EOF
#!/bin/bash
cd "\$(dirname "\$0")"

case "\$1" in
    start)
        echo "启动服务..."
        docker-compose up -d
        echo "✅ 服务已启动"
        ;;
    stop)
        echo "停止服务..."
        docker-compose down
        echo "✅ 服务已停止"
        ;;
    restart)
        echo "重启服务..."
        docker-compose restart
        echo "✅ 服务已重启"
        ;;
    status)
        echo "服务状态:"
        docker-compose ps
        echo ""
        echo "容器日志最后10行:"
        docker-compose logs --tail=10
        ;;
    logs)
        echo "查看实时日志 (Ctrl+C退出):"
        docker-compose logs -f
        ;;
    update)
        SOURCE_DIR="\${2:-}"

        if [ -z "\$SOURCE_DIR" ]; then
            echo "请指定源目录: ./manage.sh update /path/to/uniapp/dist"
            echo ""
            echo "当前html目录内容:"
            ls -la html/ 2>/dev/null || echo "目录为空"
            exit 1
        fi

        if [ ! -d "\$SOURCE_DIR" ]; then
            echo "目录不存在: \$SOURCE_DIR"
            exit 1
        fi

        echo "从 \$SOURCE_DIR 更新到 html/"
        echo "备份当前文件..."
        BACKUP_DIR="backups/html-backup-\$(date +%Y%m%d-%H%M%S)"
        mkdir -p "\$BACKUP_DIR"
        cp -r html/* "\$BACKUP_DIR/" 2>/dev/null || true

        echo "更新文件..."
        rm -rf html/*
        cp -r "\$SOURCE_DIR"/* html/ 2>/dev/null || {
            echo "复制失败，恢复备份..."
            cp -r "\$BACKUP_DIR"/* html/ 2>/dev/null || true
            echo "❌ 文件更新失败"
            exit 1
        }

        echo "✅ 文件更新完成"
        echo "备份位置: \$BACKUP_DIR"

        # 重启服务
        docker-compose restart
        echo "✅ 服务已重启"
        ;;
    backup)
        BACKUP_DIR="../backup-\$(date +%Y%m%d-%H%M%S)"
        echo "备份到: \$BACKUP_DIR"
        mkdir -p "\$BACKUP_DIR"
        cp -r html "\$BACKUP_DIR/" 2>/dev/null || true
        cp -r certs "\$BACKUP_DIR/" 2>/dev/null || true
        cp -r config "\$BACKUP_DIR/" 2>/dev/null || true
        cp docker-compose.yml "\$BACKUP_DIR/" 2>/dev/null || true
        cp .deploy-config "\$BACKUP_DIR/" 2>/dev/null || true
        echo "✅ 备份完成: \$BACKUP_DIR"
        ;;
    renew)
        echo "手动续期证书..."
        ./renew-cert.sh
        ;;
    shell)
        echo "进入容器Shell..."
        docker-compose exec nginx sh
        ;;
    config)
        echo "重新加载Nginx配置..."
        docker-compose exec nginx nginx -s reload
        echo "✅ 配置已重新加载"
        ;;
    test)
        echo "运行部署测试..."
        echo "1. 测试HTTP重定向..."
        HTTP_STATUS=\$(curl -s -o /dev/null -w "%{http_code}" http://localhost 2>/dev/null || echo "FAIL")
        echo "   HTTP状态: \$HTTP_STATUS"

        echo "2. 测试HTTPS连接..."
        HTTPS_STATUS=\$(curl -s -o /dev/null -w "%{http_code}" -k https://localhost/health 2>/dev/null || echo "FAIL")
        echo "   HTTPS状态: \$HTTPS_STATUS"

        echo "3. 测试容器状态..."
        if docker-compose ps | grep -q "Up"; then
            echo "   容器状态: ✅ 运行正常"
        else
            echo "   容器状态: ❌ 运行异常"
        fi

        $(if [ -n "$API_SERVER" ]; then
  echo "echo '4. 测试API代理...'"
  echo "API_STATUS=\$(curl -s -o /dev/null -w '%{http_code}' -k 'https://localhost$API_PREFIX/health' 2>/dev/null || echo 'FAIL')"
  echo "echo '   API状态: \$API_STATUS'"
fi)

        echo "✅ 测试完成"
        ;;
    api-config)
        ACTION="\${2:-show}"

        case "\$ACTION" in
            show)
                echo "当前API配置:"
                echo "  服务器: $API_SERVER"
                echo "  路径前缀: $API_PREFIX"
                echo "  超时时间: ${API_TIMEOUT}秒"
                echo "  CORS跨域: $(if [ "$ENABLE_CORS" = true ]; then echo '已启用'; else echo '未启用'; fi)"
                ;;
            update)
                NEW_API_SERVER="\${3:-}"
                if [ -z "\$NEW_API_SERVER" ]; then
                    echo "请指定新的API服务器地址: ./manage.sh api-config update http://new-api.example.com"
                    exit 1
                fi

                echo "更新API配置..."
                echo "原服务器: $API_SERVER"
                echo "新服务器: \$NEW_API_SERVER"

                # 更新配置文件
                sed -i "s|proxy_pass $API_SERVER;|proxy_pass \$NEW_API_SERVER;|" config/nginx.conf

                # 更新部署配置
                sed -i "s|API_SERVER=.*|API_SERVER=\"\$NEW_API_SERVER\"|" .deploy-config

                echo "✅ API配置已更新"
                echo "重启服务使配置生效..."
                docker-compose restart
                ;;
            test)
                echo "测试API连接..."
                if [ -n "$API_SERVER" ]; then
                    curl -v -k "https://localhost$API_PREFIX/health"
                else
                    echo "未配置API服务器"
                fi
                ;;
            *)
                echo "API配置管理:"
                echo "  ./manage.sh api-config show         显示当前配置"
                echo "  ./manage.sh api-config update <URL> 更新API服务器地址"
                echo "  ./manage.sh api-config test         测试API连接"
                ;;
        esac
        ;;
    info)
        echo "项目信息:"
        echo "  域名: $DOMAIN"
        echo "  目录: $PROJECT_DIR"
        echo "  部署时间: $(grep '生成时间' .deploy-config 2>/dev/null | head -1 | cut -d: -f2-)"
        echo ""
        echo "目录结构:"
        echo "  html/: \$(find html -type f | wc -l) 个文件, \$(du -sh html | cut -f1)"
        echo "  certs/: \$(find certs -name '*.pem' -type f | wc -l) 个证书文件"
        echo "  logs/: \$(find logs -name '*.log' -type f | wc -l) 个日志文件"
        echo ""
        echo "服务状态:"
        docker-compose ps
        echo ""
        echo "证书信息:"
        if [ -f "certs/fullchain.pem" ]; then
            if command -v openssl > /dev/null 2>&1; then
                openssl x509 -in certs/fullchain.pem -noout -dates | sed 's/notBefore=/生效: /; s/notAfter=/过期: /'
            else
                echo "证书文件存在，但openssl不可用"
            fi
        else
            echo "未找到证书文件"
        fi
        ;;
    *)
        echo "Uniapp项目管理脚本"
        echo ""
        echo "用法: ./manage.sh <命令> [参数]"
        echo ""
        echo "基本命令:"
        echo "  start                    启动服务"
        echo "  stop                     停止服务"
        echo "  restart                  重启服务"
        echo "  status                   查看状态"
        echo "  logs                     查看实时日志"
        echo "  config                   重新加载Nginx配置"
        echo ""
        echo "文件管理:"
        echo "  update <目录>            更新网站文件"
        echo "  backup                   备份当前文件"
        echo ""
        echo "证书管理:"
        echo "  renew                    手动续期证书"
        echo ""
        echo "API管理:"
        echo "  api-config show          显示API配置"
        echo "  api-config update <URL>  更新API服务器地址"
        echo "  api-config test          测试API连接"
        echo ""
        echo "调试命令:"
        echo "  shell                    进入容器Shell"
        echo "  test                     运行部署测试"
        echo "  info                     查看项目信息"
        echo ""
        echo "示例:"
        echo "  ./manage.sh update ~/uniapp/dist"
        echo "  ./manage.sh api-config update http://new-api.example.com"
        echo "  ./manage.sh test"
        ;;
esac
EOF

chmod +x manage.sh

# 创建快速使用指南
echo "📋 创建使用指南..."

cat >README.md <<EOF
# Uniapp项目部署指南

## 项目信息
- **域名**: $DOMAIN
- **部署目录**: $PROJECT_DIR
- **部署时间**: $(date)
$(if [ -n "$API_SERVER" ]; then
  echo "- **API服务器**: $API_SERVER"
  echo "- **API前缀**: $API_PREFIX"
fi)

## 快速开始

### 1. 访问网站
- HTTPS地址: https://$DOMAIN
- HTTP地址会自动重定向到HTTPS

### 2. 更新Uniapp文件
\`\`\`bash
# 在本地构建项目
npm run build:h5

# 上传文件到服务器
scp -r ./dist/* user@server:$PROJECT_DIR/html/

# 或者在服务器上直接更新
./manage.sh update /path/to/dist
\`\`\`

### 3. 管理服务
\`\`\`bash
# 启动/停止
./manage.sh start
./manage.sh stop

# 查看状态
./manage.sh status

# 查看日志
./manage.sh logs

# 测试部署
./manage.sh test
\`\`\`

## 目录结构
\`\`\`
$PROJECT_DIR/
├── docker-compose.yml    # Docker配置
├── manage.sh             # 管理脚本（重要！）
├── renew-cert.sh         # 证书续期脚本
├── README.md             # 本文档
├── .deploy-config        # 部署配置
├── config/               # Nginx配置
│   ├── nginx.conf        # 主配置
│   └── nginx-temp.conf   # 临时配置
├── certs/                # SSL证书
│   ├── fullchain.pem     # 证书链
│   └── privkey.pem       # 私钥
├── html/                 # ★ Uniapp网站文件（重要！）
├── logs/                 # Nginx日志
├── acme-challenge/       # ACME验证文件
└── backups/              # 备份目录
\`\`\`

## API代理配置
$(if [ -n "$API_SERVER" ]; then
  echo "已配置API代理："
  echo "- 前端访问: https://$DOMAIN$API_PREFIX/"
  echo "- 实际转发到: $API_SERVER"
  echo "- 超时时间: ${API_TIMEOUT}秒"
  echo ""
  echo "管理命令："
  echo "\`\`\`bash"
  echo "# 查看配置"
  echo "./manage.sh api-config show"
  echo ""
  echo "# 更新API服务器地址"
  echo "./manage.sh api-config update http://new-api.example.com"
  echo ""
  echo "# 测试API连接"
  echo "./manage.sh api-config test"
  echo "\`\`\`"
else
  echo "未配置API代理。如需添加，请："
  echo "1. 编辑 config/nginx.conf 文件"
  echo "2. 添加相应的location配置"
  echo "3. 运行: ./manage.sh restart"
fi)

## 证书管理
- 证书位置: \`certs/fullchain.pem\` 和 \`certs/privkey.pem\`
- 自动续期: 每天凌晨3点自动执行
- 手动续期: \`./manage.sh renew\`
- 证书有效期: 90天（Let's Encrypt标准）

## 故障排除

### 1. 证书问题
\`\`\`bash
# 检查证书状态
openssl x509 -in certs/fullchain.pem -noout -dates

# 手动申请证书（备用）
sudo certbot certonly --standalone -d $DOMAIN --email $EMAIL --agree-tos
\`\`\`

### 2. 服务问题
\`\`\`bash
# 查看错误日志
docker-compose logs --tail=50

# 测试Nginx配置
docker-compose exec nginx nginx -t

# 检查端口占用
sudo netstat -tlnp | grep ':80\\|:443'
\`\`\`

### 3. API代理问题
\`\`\`bash
# 测试API连接
curl -v https://$DOMAIN$API_PREFIX/health

# 查看代理日志
docker-compose logs nginx | grep "api"
\`\`\`

## 备份与恢复
\`\`\`bash
# 创建备份
./manage.sh backup

# 备份文件位于: ../backup-YYYYMMDD-HHMMSS/
\`\`\`

## 注意事项
1. 首次访问可能需要清除浏览器缓存
2. 更新文件后无需重启容器（除非修改Nginx配置）
3. 证书过期前会自动续期，无需手动干预
4. 所有配置文件都保存在项目目录中，便于迁移

## 技术支持
- 项目目录: $PROJECT_DIR
- 配置文件: config/nginx.conf
- 管理脚本: manage.sh
- 日志文件: logs/ 目录

---
*最后更新: $(date)*
EOF

echo "✅ 使用指南已创建: $PROJECT_DIR/README.md"

# 显示完成信息
echo ""
echo "🎉 部署完成!"
echo "============"
echo ""
echo "✅ 项目已成功部署到: $PROJECT_DIR"
echo ""
echo "🌐 访问地址:"
echo "  🔗 https://$DOMAIN"
echo "  📍 http://$DOMAIN (自动重定向)"
echo ""
echo "📂 重要目录:"
echo "  网站文件: $PROJECT_DIR/html/"
echo "  配置文件: $PROJECT_DIR/config/"
echo "  管理脚本: $PROJECT_DIR/manage.sh"
echo ""
echo "🔗 API配置:"
$(if [ -n "$API_SERVER" ]; then
  echo "  前端访问: https://$DOMAIN$API_PREFIX/"
  echo "  目标服务器: $API_SERVER"
  echo ""
fi)
echo "🛠️ 管理命令:"
echo "  cd $PROJECT_DIR"
echo "  ./manage.sh status     # 查看状态"
echo "  ./manage.sh update <目录>  # 更新网站文件"
echo "  ./manage.sh test       # 运行测试"
echo "  ./manage.sh info       # 查看项目信息"
echo ""
$(if [ -n "$API_SERVER" ]; then
  echo "🔧 API管理:"
  echo "  ./manage.sh api-config show    # 显示API配置"
  echo "  ./manage.sh api-config test    # 测试API连接"
  echo ""
fi)
echo "📝 后续更新流程:"
echo "  1. 本地构建Uniapp: npm run build:h5"
echo "  2. 上传文件到服务器: $PROJECT_DIR/html/"
echo "  3. 无需重启服务，Nginx自动生效"
echo ""
echo "🔐 证书信息:"
echo "  证书有效期: 90天"
echo "  自动续期: 已配置（每天凌晨3点）"
echo "  手动续期: ./manage.sh renew"
echo ""
echo "📋 详细指南请查看: $PROJECT_DIR/README.md"
echo ""
echo "✅ 部署完成时间: $(date)"
