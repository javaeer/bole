package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.Role;
import cn.net.yunlou.bole.model.request.RoleAddRequest;
import cn.net.yunlou.bole.model.request.RoleEditRequest;
import cn.net.yunlou.bole.model.request.RoleSearchRequest;
import cn.net.yunlou.bole.service.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: RoleController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("role")
@Tag(name = "03.角色管理", description = "角色相关接口")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("add")
    @Operation(summary = "新增角色")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(
            @RequestBody RoleAddRequest request) {
        Role role = QueryUtils.modelToBean(request, Role.class);
        return BusinessResponse.success(roleService.save(role));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除角色")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(
            @RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(roleService.removeById(id));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑角色")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody RoleEditRequest request) {
        Role role = QueryUtils.modelToBean(request, Role.class);
        return BusinessResponse.success(roleService.updateById(role));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取角色信息")
    public BusinessResponse<Role> get(
            @PathVariable(value = "id") Long id) {
        return BusinessResponse.success(roleService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取角色列表")
    public BusinessResponse<Page<Role>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody RoleSearchRequest request) {
        Role role = QueryUtils.modelToBean(request, Role.class);
        return BusinessResponse.success(roleService.page(page, size, role));
    }

}
