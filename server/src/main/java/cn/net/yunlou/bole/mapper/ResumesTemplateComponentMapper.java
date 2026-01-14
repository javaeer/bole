package cn.net.yunlou.bole.mapper;

import cn.net.yunlou.bole.common.IMultiMapper;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * FileName: ResumesTemplateComponentMapper Description: Created By laughtiger Created At 2025/12/18
 * 22:03 Modified By Modified At
 */
public interface ResumesTemplateComponentMapper
        extends IMultiMapper<ResumesTemplateComponent, ResumesTemplate, ResumesComponent> {

    List<ResumesTemplateComponent> selectListByTemplateId(@Param("templateId") Long templateId);
}
