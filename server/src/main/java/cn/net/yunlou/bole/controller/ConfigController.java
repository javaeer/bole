package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.entity.Config;
import cn.net.yunlou.bole.model.request.SystemConfigRequest;
import cn.net.yunlou.bole.service.ConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: SystemController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("system-config")
@Tag(name = "19.系统配置管理", description = "系统配置相关接口")
@RequiredArgsConstructor
public class ConfigController {

    private final ConfigService configService;

    @PutMapping("edit")
    @Operation(summary = "编辑系统配置")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody SystemConfigRequest request) {
        Config entity =
                Config.builder()
                        .configKey(request.getConfigKey())
                        .configValue(request.getConfigValue())
                        .configDesc(request.getConfigDesc())
                        .createdById(SecurityContextUtils.getCurrentUserId())
                        .updatedById(SecurityContextUtils.getCurrentUserId())
                        .build();

        return BusinessResponse.success(configService.saveOrUpdate(entity));
    }

    @GetMapping
    @Operation(summary = "获取系统配置列表")
    public BusinessResponse<List<Config>> getAllConfigs() {
        return BusinessResponse.success(configService.list());
    }

    /*@GetMapping("grouped")
    @Operation(summary = "分组获取系统配置列表")
    public BusinessResponse<List<SystemConfigGroupDTO> getGroupedConfigs() {
        List<SystemConfigGroupDTO> groupedConfigs = systemConfigService.getGroupedConfigs();
        return BusinessResponse.success(systemConfigService.list();
    }*/

}
