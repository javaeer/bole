package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.dto.ResumesDTO;
import cn.net.yunlou.bole.model.dto.ResumesTemplateDTO;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesTemplateStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:16
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface ResumesTemplateStructMapper extends BaseStructMapper<ResumesTemplate, ResumesTemplateDTO, ResumesTemplateQuery> {
}
