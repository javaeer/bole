package cn.net.yunlou.bole.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: CompanyEditRequest
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:50
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "编辑企业请求")
public class CompanyEditRequest extends BaseEditRequest {

    private String name;

}
