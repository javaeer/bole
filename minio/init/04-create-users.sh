#!/bin/bash
set -e

echo "=== 创建用户和访问密钥 ==="

# 等待mc就绪
until mc ready local/minio 2>/dev/null; do
    sleep 2
done

# 配置 alias
mc alias set local http://localhost:9000 $MINIO_ROOT_USER $MINIO_ROOT_PASSWORD

# 用户配置
users=(
    "bole:bole123:bole,temp"
    "backup:backup123:backup"
)

for user_info in "${users[@]}"; do
    IFS=':' read -r username password buckets <<< "$user_info"

    # 创建用户
    if mc admin user info local "$username" >/dev/null 2>&1; then
        echo "✅ 用户 '$username' 已存在"
    else
        mc admin user add local "$username" "$password"
        echo "✅ 创建用户: $username"
    fi

    # 设置用户策略
    IFS=',' read -ra bucket_array <<< "$buckets"
    for bucket in "${bucket_array[@]}"; do
        policy_name="${username}-${bucket}-policy"

        # 创建策略文件
        cat > /tmp/"$policy_name".json <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "s3:GetObject",
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:ListBucket"
            ],
            "Resource": [
                "arn:aws:s3:::${bucket}",
                "arn:aws:s3:::${bucket}/*"
            ]
        }
    ]
}
EOF

        # 创建策略
        mc admin policy create local "$policy_name" /tmp/"$policy_name".json
        mc admin policy attach local "$policy_name" --user "$username"

        echo "✅ 为用户 $username 设置 $bucket 桶权限"

        # 清理临时文件
        rm -f /tmp/"$policy_name".json
    done
done

echo "用户创建和权限设置完成"