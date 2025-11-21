#!/bin/bash

docker build -t bole-minio . && \
docker tag bole-minio harbor.yunlou.net.cn/bole/bole-minio && \
docker push harbor.yunlou.net.cn/bole/bole-minio