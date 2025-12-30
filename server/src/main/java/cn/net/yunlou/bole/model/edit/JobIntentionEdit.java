package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import lombok.*;

/**
 * FileName: JobIntentionEdit Description: Created By laughtiger Created At 2025/12/13 23:40
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobIntentionEdit extends BaseEdit {

    private String position;

    private String city;

    private String salary;

    private String jobType;
}
