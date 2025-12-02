package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.CompanyComment;
import cn.net.yunlou.bole.model.dto.CompanyCommentDTO;
import cn.net.yunlou.bole.model.query.CompanyCommentQuery;
import org.mapstruct.Mapper;

/**
 * FileName: CompanyCommentStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:14
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface CompanyCommentStructMapper
        extends BaseStructMapper<CompanyComment, CompanyCommentDTO, CompanyCommentQuery> {}
