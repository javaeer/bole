package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.mapper.RoleMapper;
import cn.net.yunlou.bole.model.dto.RoleDTO;
import cn.net.yunlou.bole.model.query.RoleQuery;
import cn.net.yunlou.bole.service.RoleService;
import cn.net.yunlou.bole.struct.RoleStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: RoleServiceImpl Description: Created By MR. WANG Created At 2025/11/24 23:59 Modified
 * By Modified At
 */
@Service
public class RoleServiceImpl
        extends BaseService<RoleMapper, Role, RoleDTO, RoleQuery, RoleStructMapper>
        implements RoleService {}
