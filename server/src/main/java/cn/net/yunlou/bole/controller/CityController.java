package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.model.dto.CityDTO;
import cn.net.yunlou.bole.model.query.CityQuery;
import cn.net.yunlou.bole.model.request.CityAddRequest;
import cn.net.yunlou.bole.model.request.CityEditRequest;
import cn.net.yunlou.bole.service.impl.CityServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CityController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("city")
@Tag(name = "18.城市管理", description = "城市相关接口")
@RequiredArgsConstructor
public class CityController {

    private final CityServiceImpl cityService;

    @PostMapping("add")
    @Operation(summary = "新增城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(
            @RequestBody CityAddRequest request) {
        City city = QueryUtils.modelToBean(request, City.class);

        return BusinessResponse.success(cityService.save(city));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(
            @RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(cityService.removeById(id));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑城市")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody CityEditRequest request) {
        City city = QueryUtils.modelToBean(request, City.class);
        return BusinessResponse.success(cityService.updateById(city));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取城市信息")
    public BusinessResponse<City> get(
            @PathVariable(value = "id") Long id) {
        return BusinessResponse.success(cityService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取城市列表")
    public BusinessResponse<Page<CityDTO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CityQuery request) {
        //City city = QueryUtils.modelToBean(request, City.class);


        return BusinessResponse.success(cityService.pageDTOByQuery(page, size, request));
    }

}
