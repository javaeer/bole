package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: ResumesAddRequest Description: Created By MR. WANG Created At 2025/11/24 22:18 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增简历请求")
public class ResumesCreate extends BaseCreate {}
