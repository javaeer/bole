package cn.net.yunlou.bole.model.create;

import cn.net.yunlou.bole.common.BaseCreate;
import lombok.*;

/**
 * FileName: JobIntentionCreate Description: Created By laughtiger Created At 2025/12/13 23:39
 * Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobIntentionCreate extends BaseCreate {

    private String position;

    private String city;

    private String salary;

    private String jobType;
}
