#!/bin/bash

_prof='test'  # 默认值

echo '参数选项：
###########################
0: test
1: prod
###########################'

read -p "请输入选择数字：" num

case "$num" in
0)
    _prof='test'
    ;;
1)
    _prof='prod'
    ;;
*)
    echo "错误：请输入 {0|1}，使用默认值 'test'"
    # 可以选择退出：exit 1
    ;;
esac

echo "切换JDK版本..."

# 方案1：直接指定路径（最可靠）
#export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-amd64"  # 修改为实际路径
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-17.0.12+7/Contents/Home"  # macOS

# 方案2：如果已有JAVA_17_HOME变量
# export JAVA_HOME="$JAVA_17_HOME"

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

echo "开始构建 Spring Boot 应用..."
$MVN_CMD spotless:apply clean package -P${_prof} dockerfile:build dockerfile:push

# 检查构建结果
if [[ $? -eq 0 ]]; then
    echo "构建完成！"
else
    echo "构建失败！"
    exit 1
fi