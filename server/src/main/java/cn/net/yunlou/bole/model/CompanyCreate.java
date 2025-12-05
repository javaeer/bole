package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseCreate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * FileName: CompanyAddRequest Description: Created By MR. WANG Created At 2025/11/24 21:47 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "新增企业请求")
public class CompanyCreate extends BaseCreate {

    private String name;

    private String email;

    private String holder;

    private String location;

    private String website;

    private String github;

    private String wechat;

    private String bio;
}
