package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.CompanyComment;
import cn.net.yunlou.bole.request.CompanyCommentAddRequest;
import cn.net.yunlou.bole.request.CompanyCommentEditRequest;
import cn.net.yunlou.bole.request.CompanyCommentSearchRequest;
import cn.net.yunlou.bole.service.CompanyCommentService;
import cn.net.yunlou.bole.utils.ModelUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyCommentController
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("/api/companyComment")
@Tag(name = "06.企业评价管理", description = "企业评价相关接口")
@RequiredArgsConstructor
public class CompanyCommentController {

    private final CompanyCommentService companyCommentCommentService;

    @PostMapping("add")
    @Operation(summary = "新增企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody CompanyCommentAddRequest request) {
        CompanyComment companyComment = ModelUtils.modelToBean(request, CompanyComment.class);
        return ResponseEntity.ok(BusinessResponse.success(companyCommentCommentService.save(companyComment)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyCommentCommentService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑企业评价")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody CompanyCommentEditRequest request) {
        CompanyComment companyComment = ModelUtils.modelToBean(request, CompanyComment.class);
        return ResponseEntity.ok(BusinessResponse.success(companyCommentCommentService.updateById(companyComment)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取企业评价信息")
    public ResponseEntity<BusinessResponse<CompanyComment>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyCommentCommentService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业评价列表")
    public ResponseEntity<BusinessResponse<Page<CompanyComment>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyCommentSearchRequest request) {
        CompanyComment companyComment = ModelUtils.modelToBean(request, CompanyComment.class);
        return ResponseEntity.ok(BusinessResponse.success(companyCommentCommentService.page(page, size, companyComment)));
    }

}
