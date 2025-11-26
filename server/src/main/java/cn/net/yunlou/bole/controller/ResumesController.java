package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.model.request.ResumesAddRequest;
import cn.net.yunlou.bole.model.request.ResumesEditRequest;
import cn.net.yunlou.bole.model.request.ResumesSearchRequest;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * FileName: ResumesController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("resumes")
@Tag(name = "07.简历管理", description = "简历相关接口")
@RequiredArgsConstructor
public class ResumesController {

    private final ResumesService resumesService;

    @PostMapping("add")
    @Operation(summary = "新增简历")
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody ResumesAddRequest request) {
        Resumes resumes = QueryUtils.modelToBean(request, Resumes.class);
        resumes.setUserId(SecurityContextUtils.getCurrentUserId());
        return ResponseEntity.ok(BusinessResponse.success(resumesService.save(resumes)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {

        Resumes resumes = resumesService.getById(id);
        if (!Objects.equals(SecurityContextUtils.getCurrentUserId(), resumes.getUserId())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL);
        }

        return ResponseEntity.ok(BusinessResponse.success(resumesService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑简历")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody @Valid ResumesEditRequest request) {
        Resumes resumes = QueryUtils.modelToBean(request, Resumes.class);

        Resumes dbResumes = resumesService.getById(resumes.getId());
        if (dbResumes == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }
        if (!Objects.equals(SecurityContextUtils.getCurrentUserId(), dbResumes.getUserId())) {
            throw new BusinessException(BusinessStatus.REQUEST_PARAM_ILLEGAL);
        }

        return ResponseEntity.ok(BusinessResponse.success(resumesService.updateById(resumes)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取简历信息")
    public ResponseEntity<BusinessResponse<Resumes>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(resumesService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取简历列表")
    public ResponseEntity<BusinessResponse<Page<Resumes>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesSearchRequest request) {
        Resumes resumes = QueryUtils.modelToBean(request, Resumes.class);
        return ResponseEntity.ok(BusinessResponse.success(resumesService.page(page, size, resumes)));
    }

}
