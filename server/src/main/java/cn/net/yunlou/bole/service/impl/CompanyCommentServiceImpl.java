package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.entity.CompanyComment;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.mapper.CompanyCommentMapper;
import cn.net.yunlou.bole.model.create.CompanyCommentCreate;
import cn.net.yunlou.bole.model.edit.CompanyCommentEdit;
import cn.net.yunlou.bole.model.query.CompanyCommentQuery;
import cn.net.yunlou.bole.model.view.CompanyCommentView;
import cn.net.yunlou.bole.service.CompanyCommentService;
import cn.net.yunlou.bole.struct.CompanyCommentStructMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: CompanyCommentServiceImpl Description: Created By MR. WANG Created At 2025/11/24 23:15
 * Modified By Modified At
 */
@Service
public class CompanyCommentServiceImpl
        extends BaseService<
                CompanyCommentMapper,
                CompanyComment,
                CompanyCommentCreate,
                CompanyCommentView,
                CompanyCommentEdit,
                CompanyCommentQuery,
                CompanyCommentStructMapper>
        implements CompanyCommentService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(CompanyCommentCreate create) {
        CompanyComment entity = structMapper.createToEntity(create);

        User currentUser = SecurityContextUtils.getCurrentUser();

        entity.setUserAvatar(currentUser.getAvatar());
        entity.setUserId(currentUser.getId());
        entity.setUserName(currentUser.getName());

        return save(entity);
    }
}
