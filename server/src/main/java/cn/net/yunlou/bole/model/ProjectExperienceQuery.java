package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ProjectExperienceSearchRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:54 Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "查询项目经历请求")
public class ProjectExperienceQuery extends BaseQuery {}
