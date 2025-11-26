package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.model.dto.ResumesDTO;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:15
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface ResumesStructMapper extends BaseStructMapper<Resumes, ResumesDTO, ResumesQuery> {
}
