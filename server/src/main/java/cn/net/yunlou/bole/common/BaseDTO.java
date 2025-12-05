package cn.net.yunlou.bole.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Data;

/**
 * FileName: BaseDTO Description: Data Transfer Object 数据传输对象 用于层间数据传输的对象，特别是跨进程/网络传输 Created By
 * laughtiger Created At 2025/12/4 22:48 Modified By Modified At
 */
@Data
@Schema(description = "请求响应基类")
public class BaseDTO implements Serializable {}
