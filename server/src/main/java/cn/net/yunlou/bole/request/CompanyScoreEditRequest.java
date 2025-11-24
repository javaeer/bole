package cn.net.yunlou.bole.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: CompanyScoreEditRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 23:36
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "编辑企业评分请求")
public class CompanyScoreEditRequest extends BaseEditRequest{
}
