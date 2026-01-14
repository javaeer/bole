package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.CompanyCommentCreate;
import cn.net.yunlou.bole.model.edit.CompanyCommentEdit;
import cn.net.yunlou.bole.model.entity.CompanyComment;
import cn.net.yunlou.bole.model.query.CompanyCommentQuery;
import cn.net.yunlou.bole.model.view.CompanyCommentView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: CompanyCommentStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:14
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CompanyCommentStructMapper
        extends BaseStructMapper<
                CompanyComment,
                CompanyCommentCreate,
                CompanyCommentView,
                CompanyCommentEdit,
                CompanyCommentQuery> {}
