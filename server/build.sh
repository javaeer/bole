#!/bin/bash

_prof='test'  # 默认值

echo '参数选项：
###########################
1: test
2: prod
###########################'

read -p "请输入选择数字：" num

case "$num" in
1)
    _prof='test'
    ;;
2)
    _prof='prod'
    ;;
*)
    echo "错误：请输入 {1|2}，使用默认值 'test'"
    ;;
esac

echo "切换JDK版本..."

# 检测操作系统并设置JAVA_HOME
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.0.12+7/Contents/Home"
elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
    # Linux
    export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"
else
    echo "错误：不支持的操作系统"
    exit 1
fi

# 确保PATH设置正确
export PATH="$JAVA_HOME/bin:$PATH"

# 清除命令缓存，确保使用新的java命令
hash -r

echo "当前Java版本："
java -version

# 检查Maven包装器是否存在
if [[ ! -f "./mvnw" ]]; then
    echo "警告: 未找到 ./mvnw，尝试使用系统mvn"
    MVN_CMD="mvn"
else
    MVN_CMD="./mvnw"
    chmod +x ./mvnw 2>/dev/null  # 确保可执行权限
fi

# 构建前清理：确保干净的构建环境
echo "========================================"
echo "开始构建前Docker环境清理..."

# 1. 显示当前Docker状态
echo "当前Docker状态："
docker system df


# 2. 清理未使用的镜像（释放磁盘空间）
echo "清理未使用的镜像..."
docker image prune -f

# 3. 清理悬空镜像（没有标签的中间层镜像）
echo "清理悬空镜像..."
docker images -f "dangling=true" -q | while read image_id; do
    docker rmi "$image_id" 2>/dev/null || true
done


# 4. 清理构建缓存（如果之前构建失败）
echo "清理构建缓存..."
docker builder prune -f 2>/dev/null || true

# 5. 显示清理后的状态
echo "清理后的Docker状态："
docker system df

echo "Docker环境清理完成！"
echo "========================================"

# 开始构建
echo "开始构建 Spring Boot 应用..."
$MVN_CMD spotless:apply clean package -P${_prof} dockerfile:build dockerfile:push

# 检查构建结果
if [[ $? -eq 0 ]]; then
    echo "========================================"
    echo "构建成功！"

    # 显示新构建的镜像信息
    echo "新构建的镜像："
    docker images --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}" | head -5

    echo "========================================"
    echo "建议后续步骤："
    echo "1. 运行容器: docker-compose up -d"
    echo "2. 查看日志: docker-compose logs -f"
    echo "3. 更多清理: docker system prune -af"

else
    echo "========================================"
    echo "构建失败！"
    echo "可能的原因："
    echo "1. Maven依赖下载失败"
    echo "2. Docker服务未启动"
    echo "3. Docker Hub认证失败"
    echo "4. 网络连接问题"

    # 提供调试建议
    echo ""
    echo "调试建议："
    echo "1. 检查Docker状态: docker version"
    echo "2. 单独运行Maven: $MVN_CMD clean package -P${_prof}"
    echo "3. 单独构建Docker镜像: $MVN_CMD dockerfile:build -P${_prof}"

    exit 1
fi