package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.model.create.RoleCreate;
import cn.net.yunlou.bole.model.edit.RoleEdit;
import cn.net.yunlou.bole.model.query.RoleQuery;
import cn.net.yunlou.bole.model.view.RoleView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: RoleStructMapper Description: Created By MR. WANG Created At 2025/11/26 19:16 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RoleStructMapper
        extends BaseStructMapper<Role, RoleCreate, RoleView, RoleEdit, RoleQuery> {}
