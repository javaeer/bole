package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.model.CityCreate;
import cn.net.yunlou.bole.model.CityEdit;
import cn.net.yunlou.bole.model.CityQuery;
import cn.net.yunlou.bole.model.CityView;
import cn.net.yunlou.bole.service.CityService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CityController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified By
 * Modified At
 */
@RestController
@RequestMapping("city")
@Tag(name = "18.城市管理", description = "城市相关接口")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @PostMapping("add")
    @Operation(summary = "新增城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody @Valid CityCreate create) {

        return BusinessResponse.success(cityService.saveByCreate(create));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(cityService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid CityEdit request) {
        return BusinessResponse.success(cityService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取城市信息")
    public BusinessResponse<City> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(cityService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取城市列表")
    public BusinessResponse<Page<CityView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CityQuery request) {
        return BusinessResponse.success(cityService.pageViewByQuery(page, size, request));
    }

    @PostMapping("tree")
    @Operation(summary = "获取城市树形列表")
    public BusinessResponse<List<City>> tree() {
        return BusinessResponse.success(cityService.listAllChildren());
    }
}
