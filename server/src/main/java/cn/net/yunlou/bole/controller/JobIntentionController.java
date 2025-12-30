package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.JobIntention;
import cn.net.yunlou.bole.model.create.JobIntentionCreate;
import cn.net.yunlou.bole.model.edit.JobIntentionEdit;
import cn.net.yunlou.bole.model.query.JobIntentionQuery;
import cn.net.yunlou.bole.model.view.JobIntentionView;
import cn.net.yunlou.bole.service.JobIntentionService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 关注的公司 收藏的模板 FileName: CompanyController Description: Created By MR. WANG Created At 2025/11/24
 * 21:27 Modified By Modified At
 */
@RestController
@RequestMapping("job/intention")
@Tag(name = "08.求职意向管理", description = "求职意向相关接口")
@RequiredArgsConstructor
public class JobIntentionController {

    private final JobIntentionService jobIntentionService;

    @PostMapping("add")
    @Operation(summary = "新增求职意向")
    public BusinessResponse<Boolean> add(@RequestBody JobIntentionCreate request) {
        return BusinessResponse.success(jobIntentionService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除求职意向")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(jobIntentionService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑求职意向")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid JobIntentionEdit request) {
        return BusinessResponse.success(jobIntentionService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取求职意向信息")
    public BusinessResponse<JobIntention> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(jobIntentionService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取求职意向列表")
    public BusinessResponse<Page<JobIntentionView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody JobIntentionQuery request) {
        return BusinessResponse.success(jobIntentionService.pageViewByQuery(page, size, request));
    }
}
