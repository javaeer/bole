package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.model.dto.RoleDTO;
import cn.net.yunlou.bole.model.query.RoleQuery;
import org.mapstruct.Mapper;

/**
 * FileName: RoleStructMapper
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/26 19:16
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface RoleStructMapper extends BaseStructMapper<Role, RoleDTO, RoleQuery> {
}
