package cn.net.yunlou.bole.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: BaseEditRequest Description: Created By MR. WANG Created At 2025/11/24 21:50 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增请求基类")
public class BaseCreate extends BaseDTO {}
