package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: EducationExperienceSearchRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:45 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "查询教育经历请求")
public class EducationExperienceQuery extends BaseQuery {}
