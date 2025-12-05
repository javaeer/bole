package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.ResumesTemplateView;
import cn.net.yunlou.bole.service.ResumesTemplateService;
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
@RequestMapping("resumes-template")
@Tag(name = "08.简历模板管理", description = "简历模板相关接口")
@RequiredArgsConstructor
public class ResumesTemplateController {

    private final ResumesTemplateService resumesTemplateService;

    @PostMapping("add")
    @Operation(summary = "新增简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody ResumesTemplateCreate request) {
        return BusinessResponse.success(resumesTemplateService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {

        return BusinessResponse.success(resumesTemplateService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid ResumesTemplateEdit request) {

        ResumesTemplate dbResumesTemplate = resumesTemplateService.getById(request.getId());
        if (dbResumesTemplate == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return BusinessResponse.success(resumesTemplateService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取简历模板信息")
    public BusinessResponse<ResumesTemplate> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(resumesTemplateService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取简历模板列表")
    public BusinessResponse<Page<ResumesTemplateView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesTemplateQuery request) {
        return BusinessResponse.success(
                resumesTemplateService.pageViewByQuery(page, size, request));
    }
}
