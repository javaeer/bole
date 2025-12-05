package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.EducationExperience;
import cn.net.yunlou.bole.model.EducationExperienceCreate;
import cn.net.yunlou.bole.model.EducationExperienceEdit;
import cn.net.yunlou.bole.model.EducationExperienceQuery;
import cn.net.yunlou.bole.model.EducationExperienceView;
import cn.net.yunlou.bole.service.EducationExperienceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: EducationExperienceController Description: Created By MR. WANG Created At 2025/11/24
 * 21:27 Modified By Modified At
 */
@RestController
@RequestMapping("education-experience")
@Tag(name = "09.教育经历管理", description = "教育经历相关接口")
@RequiredArgsConstructor
public class EducationExperienceController {

    private final EducationExperienceService educationExperienceService;

    @PostMapping("add")
    @Operation(summary = "新增教育经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody EducationExperienceCreate request) {
        return BusinessResponse.success(educationExperienceService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除教育经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(educationExperienceService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑教育经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid EducationExperienceEdit request) {
        return BusinessResponse.success(educationExperienceService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取教育经历信息")
    public BusinessResponse<EducationExperience> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(educationExperienceService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取教育经历列表")
    public BusinessResponse<Page<EducationExperienceView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody EducationExperienceQuery request) {
        return BusinessResponse.success(
                educationExperienceService.pageViewByQuery(page, size, request));
    }
}
