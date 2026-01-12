package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.model.create.DocumentTaskCreate;
import cn.net.yunlou.bole.model.edit.DocumentTaskEdit;
import cn.net.yunlou.bole.model.query.DocumentTaskQuery;
import cn.net.yunlou.bole.model.view.DocumentTaskView;
import cn.net.yunlou.bole.service.DocumentTaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: DocumentTaskController Description: Created By laughtiger Created At 2026/1/9 21:47
 * Modified By Modified At
 */
@RestController
@RequestMapping("task")
@Tag(name = "27.文档生成任务", description = "文档生成任务相关接口")
@RequiredArgsConstructor
public class DocumentTaskController {

    private final DocumentTaskService documentTaskService;

    @PostMapping("add")
    @Operation(summary = "新增生成任务")
    public BusinessResponse<Boolean> add(@RequestBody DocumentTaskCreate request) {
        return BusinessResponse.success(documentTaskService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除生成任务")
    public BusinessResponse<Boolean> del(@RequestParam(value = "id") Long id) {
        return BusinessResponse.success(documentTaskService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑生成任务")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid DocumentTaskEdit request) {
        return BusinessResponse.success(documentTaskService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取生成任务信息")
    public BusinessResponse<DocumentTaskView> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(documentTaskService.getViewById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取生成任务列表")
    public BusinessResponse<Page<DocumentTaskView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody DocumentTaskQuery request) {
        return BusinessResponse.success(documentTaskService.pageViewByQuery(page, size, request));
    }
}
