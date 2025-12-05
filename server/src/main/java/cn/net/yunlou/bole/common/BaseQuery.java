package cn.net.yunlou.bole.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * FileName: BaseSearchRequest Description: Created By MR. WANG Created At 2025/11/23 10:54 Modified
 * By Modified At
 */
@Data
@Schema(description = "查询实体")
public class BaseQuery implements Serializable {

    @Schema(description = "查询开始于", requiredMode = Schema.RequiredMode.AUTO)
    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS", timezone = "GMT+8")
    protected Date queryStartAt;

    @Schema(description = "查询止于", requiredMode = Schema.RequiredMode.AUTO)
    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS", timezone = "GMT+8")
    protected Date queryStopAt;

    /** 关键字 */
    @Schema(description = "关键字", requiredMode = Schema.RequiredMode.AUTO)
    protected String keyWords;

    /** 查询 字段 */
    @Schema(description = "查询行", requiredMode = Schema.RequiredMode.AUTO)
    protected String keyField = "name";
}
