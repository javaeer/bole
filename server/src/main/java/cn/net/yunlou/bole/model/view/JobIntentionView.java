package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseView;
import lombok.*;

/**
 * FileName: JobIntentionView Description: Created By laughtiger Created At 2025/12/13 23:39
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobIntentionView extends BaseView {

    private String position;

    private String city;

    private String salary;

    private String jobType;
}
