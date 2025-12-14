package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: UserBasicInfoView Description: Created By laughtiger Created At 2025/12/14 02:42
 * Modified By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBasicInfoView extends BaseView {

    private String email;

    private String phone;

    private String name;

    private String avatar;

    private String title;

    private String location;

    private String website;

    private String github;

    private String wechat;

    private Integer workYears;
}
