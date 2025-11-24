package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: RoleEditRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/25 00:01
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "编辑角色请求")
public class RoleEditRequest extends BaseEditRequest{
}
