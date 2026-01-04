package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.model.entity.Region;
import cn.net.yunlou.bole.model.query.RegionQuery;
import cn.net.yunlou.bole.model.view.RegionView;
import cn.net.yunlou.bole.service.RegionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("region")
@Tag(name = "18.行政区划管理", description = "行政区划管理相关接口")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping("/children")
    @Operation(summary = "根据父级编码查询子区域")
    public BusinessResponse<List<RegionView>> getChildren(@RequestBody(required = false) RegionQuery query) {
        List<RegionView> children = regionService.listViewByQuery(query);
        return BusinessResponse.success(children);
    }

    @GetMapping("/code/{id}")
    @Operation(summary = "根据编码查询区域详情")
    public BusinessResponse<RegionView> getByCode(
            @Parameter(description = "行政区划代码", required = true) @PathVariable Long id) {

        return BusinessResponse.success(regionService.getViewById(id));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量查询区域")
    public BusinessResponse<Map<Long, List<Region>>> batchQuery(
            @Parameter(description = "行政区划代码列表", required = true)
            @RequestBody List<Long> list) {
        Map<Long, List<Region>> listMap = regionService.listDirectChildrenByParentIds(list);
        return BusinessResponse.success(listMap);
    }


    @GetMapping("/tree")
    @Operation(summary = "获取区域树")
    public BusinessResponse<Region> getTree(
            @Parameter(description = "根节点编码，不传则返回省级树")
            @RequestParam(required = false) Long parentId) {
        if (parentId == null || parentId == 0L) {
            parentId = 86L;
        }
        Region withSubTree = regionService.getNodeWithSubTree(parentId);
        return BusinessResponse.success(withSubTree);
    }

    @GetMapping("/wholetree")
    @Operation(summary = "获取区域树")
    public BusinessResponse<List<Region>> getWholeTree() {
        return BusinessResponse.success(regionService.listWholeTree());
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门城市")
    public BusinessResponse<List<RegionView>> getHotRegions() {
        List<RegionView> hotRegions = regionService.listView(Region.builder().isHot(1).build());
        return BusinessResponse.success(hotRegions);
    }

    @GetMapping("/nearest")
    @Operation(summary = "根据经纬度查找最近区域")
    public BusinessResponse<RegionView> findNearestRegion(
            @Parameter(description = "经度", required = true)
            @RequestParam Double longitude,

            @Parameter(description = "纬度", required = true)
            @RequestParam Double latitude,

            @Parameter(description = "区域级别：1-省，2-市，3-区县")
            @RequestParam(required = false) Integer level) {

        RegionView region = regionService.findNearestRegion(longitude, latitude, level);
        return BusinessResponse.success(region);
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询区域")
    public BusinessResponse<Page<RegionView>> queryByPage(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody RegionQuery request) {

        return BusinessResponse.success(regionService.pageViewByQuery(page, size, request));
    }

    @GetMapping("/provinces")
    @Operation(summary = "获取所有省份")
    public BusinessResponse<List<RegionView>> getAllProvinces() {
        Region region = new Region();
        region.setLevel(1);
        List<RegionView> provinces = regionService.listView(region);
        return BusinessResponse.success(provinces);
    }

    @GetMapping("/cities/{provinceId}")
    @Operation(summary = "获取省份下的城市")
    public BusinessResponse<List<RegionView>> getCitiesByProvince(
            @PathVariable Long provinceId) {
        Region region = new Region();
        region.setParentId(provinceId);
        region.setLevel(2);
        List<RegionView> cities = regionService.listView(region);
        return BusinessResponse.success(cities);
    }

    @GetMapping("/districts/{cityId}")
    @Operation(summary = "获取城市下的区县")
    public BusinessResponse<List<RegionView>> getDistrictsByCity(@PathVariable Long cityId) {
        Region region = new Region();
        region.setParentId(cityId);
        region.setLevel(3);
        List<RegionView> districts = regionService.listView(region);
        return BusinessResponse.success(districts);
    }
}