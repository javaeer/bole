package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.JobIntention;
import cn.net.yunlou.bole.model.create.JobIntentionCreate;
import cn.net.yunlou.bole.model.edit.JobIntentionEdit;
import cn.net.yunlou.bole.model.query.JobIntentionQuery;
import cn.net.yunlou.bole.model.view.JobIntentionView;

/**
 * FileName: JobIntentionService Description: Created By laughtiger Created At 2025/12/13 23:38
 * Modified By Modified At
 */
public interface JobIntentionService
        extends IBaseService<
                JobIntention,
                JobIntentionCreate,
                JobIntentionView,
                JobIntentionEdit,
                JobIntentionQuery> {
}
