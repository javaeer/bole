package cn.net.yunlou.bole.model.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: UserSearchRequest
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/23 10:54
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchRequest extends BaseSearchRequest{

    private String phone;

    private String email;
}
