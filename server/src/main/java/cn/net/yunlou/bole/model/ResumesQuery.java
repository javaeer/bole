package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ResumesSearchRequest Description: Created By MR. WANG Created At 2025/11/24 22:25
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "查询简历请求")
public class ResumesQuery extends BaseQuery {}
