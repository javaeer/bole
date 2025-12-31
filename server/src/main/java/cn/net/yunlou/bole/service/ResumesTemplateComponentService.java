package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IMultiService;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.ResumesTemplateComponentDTO;
import java.util.List;

/**
 * FileName: ResumesComponentService Description: Created By laughtiger Created At 2025/12/17 16:20
 * Modified By Modified At
 */
public interface ResumesTemplateComponentService
        extends IMultiService<ResumesTemplateComponent, ResumesTemplate, ResumesComponent> {

    void bindBatch(ResumesTemplate left, List<ResumesTemplateComponentDTO> dtoList);

    void syncBatch(ResumesTemplate left, List<ResumesTemplateComponentDTO> dtoList);
}
