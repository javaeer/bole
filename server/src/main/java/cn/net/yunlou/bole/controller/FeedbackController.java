package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.model.create.FeedbackCreate;
import cn.net.yunlou.bole.model.edit.FeedbackEdit;
import cn.net.yunlou.bole.model.query.FeedbackQuery;
import cn.net.yunlou.bole.model.view.FeedbackView;
import cn.net.yunlou.bole.service.FeedbackService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: FeedbackController Description: Created By MR. WANG Created At 2025/11/24 21:27
 * Modified By Modified At
 */
@RestController
@RequestMapping("feedback")
@Tag(name = "25.反馈管理", description = "反馈相关接口")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("add")
    @Operation(summary = "新增反馈")
    public BusinessResponse<Boolean> add(@RequestBody FeedbackCreate request) {
        return BusinessResponse.success(feedbackService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除反馈")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "id") Long id) {
        return BusinessResponse.success(feedbackService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑反馈")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid FeedbackEdit request) {
        return BusinessResponse.success(feedbackService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取反馈信息")
    public BusinessResponse<FeedbackView> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(feedbackService.getViewById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取反馈列表")
    public BusinessResponse<Page<FeedbackView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody FeedbackQuery request) {
        return BusinessResponse.success(feedbackService.pageViewByQuery(page, size, request));
    }
}
