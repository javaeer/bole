package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.model.ResumesCreate;
import cn.net.yunlou.bole.model.ResumesEdit;
import cn.net.yunlou.bole.model.ResumesQuery;
import cn.net.yunlou.bole.model.ResumesView;
import cn.net.yunlou.bole.service.ResumesService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: ResumesController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("resumes")
@Tag(name = "07.简历管理", description = "简历相关接口")
@RequiredArgsConstructor
public class ResumesController {

    private final ResumesService resumesService;

    @PostMapping("add")
    @Operation(summary = "新增简历")
    public BusinessResponse<Boolean> add(@RequestBody ResumesCreate request) {
        return BusinessResponse.success(resumesService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {

        Resumes resumes = resumesService.getById(id);
        if (!Objects.equals(SecurityContextUtils.getCurrentUserId(), resumes.getUserId())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL);
        }

        return BusinessResponse.success(resumesService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑简历")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid ResumesEdit request) {

        Resumes dbResumes = resumesService.getById(request.getId());
        if (dbResumes == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }
        if (!Objects.equals(SecurityContextUtils.getCurrentUserId(), dbResumes.getUserId())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL);
        }

        return BusinessResponse.success(resumesService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取简历信息")
    public BusinessResponse<Resumes> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(resumesService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取简历列表")
    public BusinessResponse<Page<ResumesView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesQuery request) {
        return BusinessResponse.success(resumesService.pageViewByQuery(page, size, request));
    }
}
