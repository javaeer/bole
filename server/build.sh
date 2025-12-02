#!/bin/bash

# 设置变量
IMAGE_NAME="bole-server"
IMAGE_HARBOR="harbor.yunlou.net.cn/bole/"

echo "开始构建 Spring Boot 应用..."
./mvnw clean package -DskipTests

echo "构建 Docker 镜像..."
docker build -t bole-server .

#echo "登录 Harbor 仓库..."
#docker login harbor.yunlou.net.cn

echo "标记镜像到 Harbor..."
docker tag ${IMAGE_NAME} ${IMAGE_HARBOR}${IMAGE_NAME}

echo "推送镜像到 Harbor..."
docker push ${IMAGE_HARBOR}${IMAGE_NAME}

echo "构建完成！"
