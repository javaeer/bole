package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.Banner;
import cn.net.yunlou.bole.model.BannerCreate;
import cn.net.yunlou.bole.model.BannerEdit;
import cn.net.yunlou.bole.model.BannerQuery;
import cn.net.yunlou.bole.model.BannerView;
import cn.net.yunlou.bole.service.BannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: SystemBannerController Description: Created By MR. WANG Created At 2025/11/24 21:27
 * Modified By Modified At
 */
@RestController
@RequestMapping("banner")
@Tag(name = "05.轮播图管理", description = "轮播图相关接口")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @PostMapping("add")
    @Operation(summary = "新增轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody BannerCreate dto) {
        boolean data = bannerService.saveByCreate(dto);
        return BusinessResponse.success(data);
    }

    @DeleteMapping("del")
    @Operation(summary = "删除轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(bannerService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑轮播图")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid BannerEdit edit) {
        return BusinessResponse.success(bannerService.updateByEdit(edit));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取轮播图信息")
    public BusinessResponse<Banner> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(bannerService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取轮播图列表")
    public BusinessResponse<Page<BannerView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody BannerQuery request) {
        // SystemBanner systemBanner = QueryUtils.modelToBean(request, SystemBanner.class);

        return BusinessResponse.success(bannerService.pageViewByQuery(page, size, request));
    }
}
