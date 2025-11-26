package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.request.ResumesTemplateAddRequest;
import cn.net.yunlou.bole.model.request.ResumesTemplateEditRequest;
import cn.net.yunlou.bole.model.request.ResumesTemplateSearchRequest;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: ResumesController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
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
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody ResumesTemplateAddRequest request) {
        ResumesTemplate resumesTemplate = QueryUtils.modelToBean(request, ResumesTemplate.class);
        return ResponseEntity.ok(BusinessResponse.success(resumesTemplateService.save(resumesTemplate)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {

        return ResponseEntity.ok(BusinessResponse.success(resumesTemplateService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody @Valid ResumesTemplateEditRequest request) {
        ResumesTemplate resumesTemplate = QueryUtils.modelToBean(request, ResumesTemplate.class);

        ResumesTemplate dbResumesTemplate = resumesTemplateService.getById(resumesTemplate.getId());
        if (dbResumesTemplate == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return ResponseEntity.ok(BusinessResponse.success(resumesTemplateService.updateById(resumesTemplate)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取简历模板信息")
    public ResponseEntity<BusinessResponse<ResumesTemplate>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(resumesTemplateService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取简历模板列表")
    public ResponseEntity<BusinessResponse<Page<ResumesTemplate>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesTemplateSearchRequest request) {
        ResumesTemplate resumesTemplate = QueryUtils.modelToBean(request, ResumesTemplate.class);
        return ResponseEntity.ok(BusinessResponse.success(resumesTemplateService.page(page, size, resumesTemplate)));
    }

}
