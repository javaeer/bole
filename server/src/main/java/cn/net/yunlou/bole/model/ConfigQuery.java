package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * FileName: SystemEditRequest Description: Created By laughtiger Created At 2025/11/28 11:59
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ConfigQuery extends BaseQuery {

    @NotBlank(message = "键不得为空")
    private String configKey;

    @NotBlank(message = "值不得为空")
    private String configValue;

    @NotBlank(message = "描述不得为空")
    private String configDesc;
}
