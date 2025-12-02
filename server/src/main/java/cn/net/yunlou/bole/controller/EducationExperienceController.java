package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.EducationExperience;
import cn.net.yunlou.bole.model.request.EducationExperienceAddRequest;
import cn.net.yunlou.bole.model.request.EducationExperienceEditRequest;
import cn.net.yunlou.bole.model.request.EducationExperienceSearchRequest;
import cn.net.yunlou.bole.service.EducationExperienceService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public BusinessResponse<Boolean> add(@RequestBody EducationExperienceAddRequest request) {
        EducationExperience educationExperience =
                QueryUtils.modelToBean(request, EducationExperience.class);
        return BusinessResponse.success(educationExperienceService.save(educationExperience));
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
    public BusinessResponse<Boolean> edit(@RequestBody EducationExperienceEditRequest request) {
        EducationExperience educationExperience =
                QueryUtils.modelToBean(request, EducationExperience.class);
        return BusinessResponse.success(educationExperienceService.updateById(educationExperience));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取教育经历信息")
    public BusinessResponse<EducationExperience> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(educationExperienceService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取教育经历列表")
    public BusinessResponse<Page<EducationExperience>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody EducationExperienceSearchRequest request) {
        EducationExperience educationExperience =
                QueryUtils.modelToBean(request, EducationExperience.class);
        return BusinessResponse.success(
                educationExperienceService.page(page, size, educationExperience));
    }
}
