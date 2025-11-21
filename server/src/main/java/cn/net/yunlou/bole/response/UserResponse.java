package cn.net.yunlou.bole.response;

import cn.net.yunlou.bole.entity.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * FileName: UserResponse Description: Created By laughtiger Created At 2025/11/19 13:48 Modified By
 * Modified At
 */
@Data
@Builder
public class UserResponse implements Serializable {

    private String token;

    private User userInfo;

    private List<String> permissions;
}
