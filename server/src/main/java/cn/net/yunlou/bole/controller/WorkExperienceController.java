package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.WorkExperience;
import cn.net.yunlou.bole.model.request.WorkExperienceAddRequest;
import cn.net.yunlou.bole.model.request.WorkExperienceEditRequest;
import cn.net.yunlou.bole.model.request.WorkExperienceSearchRequest;
import cn.net.yunlou.bole.service.WorkExperienceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: WorkExperienceController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("work-experience")
@Tag(name = "11.工作经历管理", description = "工作经历相关接口")
@RequiredArgsConstructor
public class WorkExperienceController {

    private final WorkExperienceService workExperienceService;

    @PostMapping("add")
    @Operation(summary = "新增工作经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(
            @RequestBody WorkExperienceAddRequest request) {
        WorkExperience workExperience = QueryUtils.modelToBean(request, WorkExperience.class);
        return BusinessResponse.success(workExperienceService.save(workExperience));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除工作经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(
            @RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(workExperienceService.removeById(id));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑工作经历")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody WorkExperienceEditRequest request) {
        WorkExperience workExperience = QueryUtils.modelToBean(request, WorkExperience.class);
        return BusinessResponse.success(workExperienceService.updateById(workExperience));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取工作经历信息")
    public BusinessResponse<WorkExperience> get(
            @PathVariable(value = "id") Long id) {
        return BusinessResponse.success(workExperienceService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取工作经历列表")
    public BusinessResponse<Page<WorkExperience>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody WorkExperienceSearchRequest request) {
        WorkExperience workExperience = QueryUtils.modelToBean(request, WorkExperience.class);
        return BusinessResponse.success(workExperienceService.page(page, size, workExperience));
    }

}
