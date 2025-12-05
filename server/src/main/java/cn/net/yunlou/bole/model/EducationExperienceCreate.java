package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: EducationExperienceAddRequest Description: Created By MR. WANG Created At 2025/11/24
 * 23:43 Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增教育经历请求")
public class EducationExperienceCreate extends BaseCreate {}
