package cn.net.yunlou.bole.model.edit;

import cn.net.yunlou.bole.common.BaseEdit;
import java.util.List;
import lombok.*;

/**
 * FileName: ResumesTemplateComponentEdit Description: Created By laughtiger Created At 2025/12/13
 * 21:20 Modified By Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumesTemplateComponentEdit extends BaseEdit {

    private Long templateId;

    private List<Long> componentIds;
}
