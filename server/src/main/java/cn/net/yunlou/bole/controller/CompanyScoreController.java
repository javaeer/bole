package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.entity.CompanyScore;
import cn.net.yunlou.bole.request.*;
import cn.net.yunlou.bole.service.CompanyScoreService;
import cn.net.yunlou.bole.utils.ModelUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyController
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("/api/companyScore")
@Tag(name = "05.企业评级管理", description = "企业评级相关接口")
@RequiredArgsConstructor
public class CompanyScoreController {

    private final CompanyScoreService companyScoreService;

    @PostMapping("add")
    @Operation(summary = "新增企业评级")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody CompanyScoreAddRequest request) {
        CompanyScore companyScore = ModelUtils.modelToBean(request, CompanyScore.class);
        return ResponseEntity.ok(BusinessResponse.success(companyScoreService.save(companyScore)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业评级")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyScoreService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑企业评级")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody CompanyScoreEditRequest request) {
        CompanyScore companyScore = ModelUtils.modelToBean(request, CompanyScore.class);
        return ResponseEntity.ok(BusinessResponse.success(companyScoreService.updateById(companyScore)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取企业评级信息")
    public ResponseEntity<BusinessResponse<CompanyScore>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyScoreService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业评级列表")
    public ResponseEntity<BusinessResponse<Page<CompanyScore>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyScoreSearchRequest request) {
        CompanyScore companyScore = ModelUtils.modelToBean(request, CompanyScore.class);
        return ResponseEntity.ok(BusinessResponse.success(companyScoreService.page(page, size, companyScore)));
    }

}
