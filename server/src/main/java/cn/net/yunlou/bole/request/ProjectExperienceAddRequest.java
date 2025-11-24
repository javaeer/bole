package cn.net.yunlou.bole.request;

import cn.net.yunlou.bole.common.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: ProjectExperienceAddRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 23:53
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@Schema(description = "新增项目经历请求")
public class ProjectExperienceAddRequest extends BaseModel {
}
