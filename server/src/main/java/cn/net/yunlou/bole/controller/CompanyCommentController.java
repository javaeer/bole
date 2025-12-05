package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.CompanyComment;
import cn.net.yunlou.bole.model.CompanyCommentCreate;
import cn.net.yunlou.bole.model.CompanyCommentEdit;
import cn.net.yunlou.bole.model.CompanyCommentQuery;
import cn.net.yunlou.bole.model.CompanyCommentView;
import cn.net.yunlou.bole.service.CompanyCommentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyCommentController Description: Created By MR. WANG Created At 2025/11/24 21:27
 * Modified By Modified At
 */
@RestController
@RequestMapping("company-comment")
@Tag(name = "06.企业评价管理", description = "企业评价相关接口")
@RequiredArgsConstructor
public class CompanyCommentController {

    private final CompanyCommentService companyCommentService;

    @PostMapping("add")
    @Operation(summary = "新增企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody CompanyCommentCreate request) {
        return BusinessResponse.success(companyCommentService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(companyCommentService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid CompanyCommentEdit request) {
        return BusinessResponse.success(companyCommentService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取企业评价信息")
    public BusinessResponse<CompanyComment> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(companyCommentService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业评价列表")
    public BusinessResponse<Page<CompanyCommentView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyCommentQuery request) {
        return BusinessResponse.success(companyCommentService.pageViewByQuery(page, size, request));
    }
}
