package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.model.create.SelfEvaluationCreate;
import cn.net.yunlou.bole.model.edit.SelfEvaluationEdit;
import cn.net.yunlou.bole.model.entity.SelfEvaluation;
import cn.net.yunlou.bole.model.query.SelfEvaluationQuery;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;
import cn.net.yunlou.bole.service.SelfEvaluationService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("self/evaluation")
@Tag(name = "14.自我评价管理", description = "自我评价相关接口")
@RequiredArgsConstructor
public class SelfEvaluationController {

    private final SelfEvaluationService selfEvaluationService;

    @PostMapping("add")
    @Operation(summary = "新增自我评价")
    public BusinessResponse<Boolean> add(@RequestBody SelfEvaluationCreate request) {
        return BusinessResponse.success(selfEvaluationService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除自我评价")
    public BusinessResponse<Boolean> del(@RequestParam(value = "id") Long id) {
        return BusinessResponse.success(selfEvaluationService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑自我评价")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid SelfEvaluationEdit request) {
        return BusinessResponse.success(selfEvaluationService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取自我评价信息")
    public BusinessResponse<SelfEvaluation> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(selfEvaluationService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取自我评价列表")
    public BusinessResponse<Page<SelfEvaluationView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody SelfEvaluationQuery request) {
        return BusinessResponse.success(selfEvaluationService.pageViewByQuery(page, size, request));
    }
}
