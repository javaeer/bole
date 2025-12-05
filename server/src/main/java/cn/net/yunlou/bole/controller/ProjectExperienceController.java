package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.ProjectExperience;
import cn.net.yunlou.bole.model.ProjectExperienceCreate;
import cn.net.yunlou.bole.model.ProjectExperienceEdit;
import cn.net.yunlou.bole.model.ProjectExperienceQuery;
import cn.net.yunlou.bole.model.ProjectExperienceView;
import cn.net.yunlou.bole.service.ProjectExperienceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: ProjectExperienceController Description: Created By MR. WANG Created At 2025/11/24
 * 21:27 Modified By Modified At
 */
@RestController
@RequestMapping("project-experience")
@Tag(name = "10.项目经历管理", description = "项目经历相关接口")
@RequiredArgsConstructor
public class ProjectExperienceController {

    private final ProjectExperienceService projectExperienceService;

    @PostMapping("add")
    @Operation(summary = "新增项目经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody ProjectExperienceCreate create) {
        return BusinessResponse.success(projectExperienceService.saveByCreate(create));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除项目经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(projectExperienceService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑项目经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid ProjectExperienceEdit edit) {
        return BusinessResponse.success(projectExperienceService.updateByEdit(edit));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取项目经历信息")
    public BusinessResponse<ProjectExperience> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(projectExperienceService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取项目经历列表")
    public BusinessResponse<Page<ProjectExperienceView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ProjectExperienceQuery request) {
        return BusinessResponse.success(
                projectExperienceService.pageViewByQuery(page, size, request));
    }
}
