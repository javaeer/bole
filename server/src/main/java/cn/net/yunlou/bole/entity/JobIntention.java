package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: JobIntention Description: Created By laughtiger Created At 2025/12/13 23:32 Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_job_intention")
public class JobIntention extends BaseEntity {

    private Long userId;

    private String position;

    private String city;

    private String salary;

    private String jobType;
}
