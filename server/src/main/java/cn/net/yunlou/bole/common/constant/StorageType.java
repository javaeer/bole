package cn.net.yunlou.bole.common.constant;

import cn.net.yunlou.bole.common.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * FileName: StorageType Description: Created By laughtiger Created At 2025/12/3 23:31 Modified By
 * Modified At
 */
@Getter
@AllArgsConstructor
public enum StorageType implements IEnum<String> {
    ALIYUN_OSS("aliyun-oss", "阿里云OSS"),

    TENCENT_COS("tencent-cos", "腾讯云COS"),

    FDFS("fdfs", "FDFS"),

    MINIO("minio", "MINIO"),

    LOCAL("local", "本地存储"),

    AUTO("local", "自动");

    private final String value;

    private final String label;
}
