package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.request.*;
import cn.net.yunlou.bole.service.CompanyExperienceService;
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
@RequestMapping("/api/companyExperience")
@Tag(name = "13.企业经历管理", description = "企业经历相关接口")
@RequiredArgsConstructor
public class CompanyExperienceController {

    private final CompanyExperienceService companyExperienceService;

    @PostMapping("add")
    @Operation(summary = "新增企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody CompanyExperienceAddRequest request) {
        CompanyExperience companyExperience = ModelUtils.modelToBean(request, CompanyExperience.class);
        return ResponseEntity.ok(BusinessResponse.success(companyExperienceService.save(companyExperience)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyExperienceService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody CompanyExperienceEditRequest request) {
        CompanyExperience companyExperience = ModelUtils.modelToBean(request, CompanyExperience.class);
        return ResponseEntity.ok(BusinessResponse.success(companyExperienceService.updateById(companyExperience)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取企业经历信息")
    public ResponseEntity<BusinessResponse<CompanyExperience>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyExperienceService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业经历列表")
    public ResponseEntity<BusinessResponse<Page<CompanyExperience>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyExperienceSearchRequest request) {
        CompanyExperience companyExperience = ModelUtils.modelToBean(request, CompanyExperience.class);
        return ResponseEntity.ok(BusinessResponse.success(companyExperienceService.page(page, size, companyExperience)));
    }

}
