package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: CompanySearchRequest
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:43
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "查询企业请求")
public class CompanySearchRequest extends BaseSearchRequest {



}
