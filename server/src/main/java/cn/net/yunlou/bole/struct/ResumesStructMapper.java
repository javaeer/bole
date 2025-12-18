package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.model.create.ResumesCreate;
import cn.net.yunlou.bole.model.edit.ResumesEdit;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import cn.net.yunlou.bole.model.view.ResumesView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: ResumesStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:15
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ResumesStructMapper
        extends BaseStructMapper<Resumes, ResumesCreate, ResumesView, ResumesEdit, ResumesQuery> {}
