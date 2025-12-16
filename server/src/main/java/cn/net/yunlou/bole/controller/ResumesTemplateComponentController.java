package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.create.ResumesTemplateComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateComponentEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateComponentView;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: ResumesController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("template-component")
@Tag(name = "08.简历模板组件管理", description = "简历模板组件相关接口")
@RequiredArgsConstructor
public class ResumesTemplateComponentController {

    private final ResumesTemplateComponentService resumesTemplateComponentService;

    @PostMapping("add")
    @Operation(summary = "新增简历模板组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody ResumesTemplateComponentCreate request) {
        return BusinessResponse.success(resumesTemplateComponentService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历模板组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {

        return BusinessResponse.success(resumesTemplateComponentService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑简历模板组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody @Valid ResumesTemplateComponentEdit request) {

        ResumesTemplateComponent dbResumesTemplateComponent =
                resumesTemplateComponentService.getById(request.getId());
        if (dbResumesTemplateComponent == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return BusinessResponse.success(resumesTemplateComponentService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取简历模板组件信息")
    public BusinessResponse<ResumesTemplateComponent> get(@PathVariable(value = "id") Long id) {
        ResumesTemplateComponent resumesTemplate = resumesTemplateComponentService.getById(id);
        return BusinessResponse.success(resumesTemplate);
    }

    @PostMapping("page")
    @Operation(summary = "获取简历模板组件列表")
    public BusinessResponse<Page<ResumesTemplateComponentView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesTemplateComponentQuery request) {
        return BusinessResponse.success(
                resumesTemplateComponentService.pageViewByQuery(page, size, request));
    }
}
