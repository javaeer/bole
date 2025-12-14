package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.JobIntention;
import cn.net.yunlou.bole.model.create.JobIntentionCreate;
import cn.net.yunlou.bole.model.edit.JobIntentionEdit;
import cn.net.yunlou.bole.model.query.JobIntentionQuery;
import cn.net.yunlou.bole.model.view.JobIntentionView;
import org.mapstruct.Mapper;

/**
 * FileName: JobIntentionStructMapper Description: Created By laughtiger Created At 2025/12/13 23:44
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface JobIntentionStructMapper
        extends BaseStructMapper<
                JobIntention,
                JobIntentionCreate,
                JobIntentionView,
                JobIntentionEdit,
                JobIntentionQuery> {}
