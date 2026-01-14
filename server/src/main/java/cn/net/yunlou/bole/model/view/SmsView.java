package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: SmsView Description: Created By laughtiger Created At 2025/12/25 01:15 Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SmsView extends BaseView {

    private Integer state;

    private String areaCode;

    private String phone;

    private Long templateId;
}
