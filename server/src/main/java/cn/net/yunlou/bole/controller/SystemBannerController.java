package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.SystemBanner;
import cn.net.yunlou.bole.model.dto.SystemBannerDTO;
import cn.net.yunlou.bole.model.query.SystemBannerQuery;
import cn.net.yunlou.bole.model.request.SystemBannerAddRequest;
import cn.net.yunlou.bole.model.request.SystemBannerEditRequest;
import cn.net.yunlou.bole.service.SystemBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: SystemBannerController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("system-banner")
@Tag(name = "20.轮播图管理", description = "轮播图相关接口")
@RequiredArgsConstructor
public class SystemBannerController {

    private final SystemBannerService systemBannerService;

    @PostMapping("add")
    @Operation(summary = "新增轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(
            @RequestBody SystemBannerAddRequest request) {
        SystemBanner systemBanner = QueryUtils.modelToBean(request, SystemBanner.class);

        return BusinessResponse.success(systemBannerService.save(systemBanner));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(
            @RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(systemBannerService.removeById(id));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody SystemBannerEditRequest request) {
        SystemBanner systemBanner = QueryUtils.modelToBean(request, SystemBanner.class);
        return BusinessResponse.success(systemBannerService.updateById(systemBanner));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取轮播图信息")
    public BusinessResponse<SystemBanner> get(
            @PathVariable(value = "id") Long id) {
        return BusinessResponse.success(systemBannerService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取轮播图列表")
    public BusinessResponse<Page<SystemBannerDTO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody SystemBannerQuery request) {
        //SystemBanner systemBanner = QueryUtils.modelToBean(request, SystemBanner.class);


        return BusinessResponse.success(systemBannerService.pageDTOByQuery(page, size, request));
    }

}
