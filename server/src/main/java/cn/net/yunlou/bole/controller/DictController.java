package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.Dict;
import cn.net.yunlou.bole.model.dto.DictDTO;
import cn.net.yunlou.bole.model.query.DictQuery;
import cn.net.yunlou.bole.model.request.DictAddRequest;
import cn.net.yunlou.bole.model.request.DictEditRequest;
import cn.net.yunlou.bole.service.DictService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: DictController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified By
 * Modified At
 */
@RestController
@RequestMapping("dict")
@Tag(name = "20.字典管理", description = "字典相关接口")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @PostMapping("add")
    @Operation(summary = "新增字典")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody DictAddRequest request) {
        Dict dict = QueryUtils.modelToBean(request, Dict.class);

        return BusinessResponse.success(dictService.save(dict));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除字典")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(dictService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑字典")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody DictEditRequest request) {
        Dict dict = QueryUtils.modelToBean(request, Dict.class);
        return BusinessResponse.success(dictService.updateById(dict));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取字典信息")
    public BusinessResponse<Dict> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(dictService.getById(id));
    }

    @GetMapping("children")
    @Operation(summary = "获取字典下级信息")
    public BusinessResponse<List<Dict>> getChildren() {
        return BusinessResponse.success(dictService.listAllChildren());
    }

    @GetMapping("children/{id}")
    @Operation(summary = "获取字典下级信息")
    public BusinessResponse<Dict> getChildren(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(dictService.getChildren(id));
    }

    @PostMapping("list")
    @Operation(summary = "获取字典列表")
    public BusinessResponse<List<DictDTO>> list(@RequestBody(required = false) DictQuery request) {
        // Dict dict = QueryUtils.modelToBean(request, Dict.class);
        return BusinessResponse.success(dictService.listDTOByQuery(request));
    }

    @PostMapping("tree")
    @Operation(summary = "获取字典树形列表")
    public BusinessResponse<List<Dict>> tree(@RequestBody(required = false) DictQuery request) {
        Dict dict = QueryUtils.modelToBean(request, Dict.class);
        return BusinessResponse.success(dictService.listAllChildren(dict));
    }

    @PostMapping("page")
    @Operation(summary = "分页获取字典列表")
    public BusinessResponse<Page<DictDTO>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody(required = false) DictQuery request) {
        // Dict dict = QueryUtils.modelToBean(request, Dict.class);

        return BusinessResponse.success(dictService.pageDTOByQuery(page, size, request));
    }
}
