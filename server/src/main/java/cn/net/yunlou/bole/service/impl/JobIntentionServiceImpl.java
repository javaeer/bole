package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.mapper.JobIntentionMapper;
import cn.net.yunlou.bole.model.create.JobIntentionCreate;
import cn.net.yunlou.bole.model.edit.JobIntentionEdit;
import cn.net.yunlou.bole.model.entity.JobIntention;
import cn.net.yunlou.bole.model.query.JobIntentionQuery;
import cn.net.yunlou.bole.model.view.JobIntentionView;
import cn.net.yunlou.bole.service.JobIntentionService;
import cn.net.yunlou.bole.struct.JobIntentionStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: JobIntentionServiceImpl Description: Created By laughtiger Created At 2025/12/13 23:42
 * Modified By Modified At
 */
@Service
public class JobIntentionServiceImpl
        extends BaseService<
                JobIntentionMapper,
                JobIntention,
                JobIntentionCreate,
                JobIntentionView,
                JobIntentionEdit,
                JobIntentionQuery,
                JobIntentionStructMapper>
        implements JobIntentionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(JobIntentionCreate create) {
        JobIntention entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        return save(entity);
    }

    @Override
    public QueryWrapper<JobIntention> getBaseQueryWrapper(JobIntention entity) {
        QueryWrapper<JobIntention> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().eq(JobIntention::getUserId, SecurityContextUtils.getCurrentUserId());
        queryWrapper.lambda().orderByDesc(JobIntention::getCreatedAt);
        return queryWrapper;
    }
}
