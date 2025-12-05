package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.CompanyExperience;
import cn.net.yunlou.bole.model.CompanyExperienceCreate;
import cn.net.yunlou.bole.model.CompanyExperienceEdit;
import cn.net.yunlou.bole.model.CompanyExperienceQuery;
import cn.net.yunlou.bole.model.CompanyExperienceView;
import cn.net.yunlou.bole.service.CompanyExperienceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("company-experience")
@Tag(name = "13.企业经历管理", description = "企业经历相关接口")
@RequiredArgsConstructor
public class CompanyExperienceController {

    private final CompanyExperienceService companyExperienceService;

    @PostMapping("add")
    @Operation(summary = "新增企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody CompanyExperienceCreate request) {
        return BusinessResponse.success(companyExperienceService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(companyExperienceService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑企业经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid CompanyExperienceEdit request) {
        return BusinessResponse.success(companyExperienceService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取企业经历信息")
    public BusinessResponse<CompanyExperience> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(companyExperienceService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业经历列表")
    public BusinessResponse<Page<CompanyExperienceView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyExperienceQuery request) {
        return BusinessResponse.success(
                companyExperienceService.pageViewByQuery(page, size, request));
    }
}
