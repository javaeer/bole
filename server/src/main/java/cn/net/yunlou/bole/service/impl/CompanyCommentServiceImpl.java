package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.CompanyComment;
import cn.net.yunlou.bole.mapper.CompanyCommentMapper;
import cn.net.yunlou.bole.model.CompanyCommentCreate;
import cn.net.yunlou.bole.model.CompanyCommentEdit;
import cn.net.yunlou.bole.model.CompanyCommentQuery;
import cn.net.yunlou.bole.model.CompanyCommentView;
import cn.net.yunlou.bole.service.CompanyCommentService;
import cn.net.yunlou.bole.struct.CompanyCommentStructMapper;
import org.springframework.stereotype.Service;

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
        implements CompanyCommentService {}
