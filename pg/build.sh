#!/bin/bash

echo "构建 Docker 镜像..."
docker build -t bole-pg .

echo "标记镜像到 Harbor..."
docker tag bole-pg harbor.yunlou.net.cn/bole/bole-pg

echo "推送镜像到 Harbor..."
docker push harbor.yunlou.net.cn/bole/bole-pg

echo "构建完成！"