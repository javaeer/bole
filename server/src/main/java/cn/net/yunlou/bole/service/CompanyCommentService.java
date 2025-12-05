package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.CompanyComment;
import cn.net.yunlou.bole.model.CompanyCommentCreate;
import cn.net.yunlou.bole.model.CompanyCommentEdit;
import cn.net.yunlou.bole.model.CompanyCommentQuery;
import cn.net.yunlou.bole.model.CompanyCommentView;

/**
 * FileName: CompanyCommentService Description: Created By MR. WANG Created At 2025/11/24 23:15
 * Modified By Modified At
 */
public interface CompanyCommentService
        extends IBaseService<
                CompanyComment,
                CompanyCommentCreate,
                CompanyCommentView,
                CompanyCommentEdit,
                CompanyCommentQuery> {}
