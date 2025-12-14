package cn.net.yunlou.bole.mapper;

import cn.net.yunlou.bole.common.IBaseMapper;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * FileName: ResumesTemplateComponentMapper Description: Created By laughtiger Created At 2025/12/13
 * 21:22 Modified By Modified At
 */
public interface ResumesTemplateComponentMapper extends IBaseMapper<ResumesTemplateComponent> {

    List<ResumesTemplateComponent> selectListByTemplateId(@Param("templateId") Long templateId);
}
