package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import lombok.*;

/**
 * FileName: RoleDTO Description: Created By MR. WANG Created At 2025/11/26 17:47 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class RoleView extends BaseView {

    private String name;

    private String code;

    private String description;
}
