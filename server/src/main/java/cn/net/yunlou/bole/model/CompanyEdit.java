package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseEdit;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: CompanyEditRequest Description: Created By MR. WANG Created At 2025/11/24 21:50
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "编辑企业请求")
public class CompanyEdit extends BaseEdit {

    private String name;
}
