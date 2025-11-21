package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * FileName: ProjectExperience
 * Description:
 * Created By laughtiger
 * Created At 2025/11/19 13:31
 * Modified By Modified At
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_project_experience")
public class ProjectExperience extends BaseEntity {

    private Long resumeId;
}
