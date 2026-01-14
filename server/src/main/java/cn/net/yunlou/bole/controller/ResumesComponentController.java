package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.model.create.ResumesComponentCreate;
import cn.net.yunlou.bole.model.edit.ResumesComponentEdit;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.query.ResumesComponentQuery;
import cn.net.yunlou.bole.model.view.ResumesComponentView;
import cn.net.yunlou.bole.service.ResumesComponentService;
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
@RequestMapping("component")
@Tag(name = "23.简历组件管理", description = "简历组件相关接口")
@RequiredArgsConstructor
public class ResumesComponentController {

    private final ResumesComponentService resumesComponentService;

    @PostMapping("add")
    @Operation(summary = "新增简历组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody ResumesComponentCreate request) {
        return BusinessResponse.success(resumesComponentService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "id") Long id) {

        return BusinessResponse.success(resumesComponentService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑简历组件")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid ResumesComponentEdit request) {

        ResumesComponent dbResumesComponent = resumesComponentService.getById(request.getId());
        if (dbResumesComponent == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return BusinessResponse.success(resumesComponentService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取简历组件信息")
    public BusinessResponse<ResumesComponentView> get(@PathVariable(value = "id") Long id) {
        ResumesComponentView resumesComponent = resumesComponentService.getViewById(id);
        return BusinessResponse.success(resumesComponent);
    }

    @PostMapping("page")
    @Operation(summary = "获取简历组件列表")
    public BusinessResponse<Page<ResumesComponentView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesComponentQuery request) {
        return BusinessResponse.success(
                resumesComponentService.pageViewByQuery(page, size, request));
    }
}
