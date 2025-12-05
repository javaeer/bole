package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.*;

/**
 * FileName: UserSearchRequest Description: Created By MR. WANG Created At 2025/11/23 10:54 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserQuery extends BaseQuery {

    private String phone;

    private String email;
}
