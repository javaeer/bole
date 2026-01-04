package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.create.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.entity.FavoriteTemplate;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateView;
import cn.net.yunlou.bole.service.FavoriteTemplateService;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: ResumesController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("template")
@Tag(name = "22.简历模板管理", description = "简历模板相关接口")
@RequiredArgsConstructor
public class ResumesTemplateController {

    private final ResumesTemplateService resumesTemplateService;

    private final FavoriteTemplateService favoriteTemplateService;

    @PostMapping("add")
    @Operation(summary = "新增简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody ResumesTemplateCreate request) {

        return BusinessResponse.success(resumesTemplateService.saveByCreate(request));
    }

    @PostMapping("add/draft")
    @Operation(summary = "新增简历模板草稿")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> addDraft(@RequestBody ResumesTemplateCreate request) {
        return BusinessResponse.success(resumesTemplateService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {

        return BusinessResponse.success(resumesTemplateService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑简历模板")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid ResumesTemplateEdit request) {

        ResumesTemplate dbResumesTemplate = resumesTemplateService.getById(request.getId());
        if (dbResumesTemplate == null) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD);
        }

        return BusinessResponse.success(resumesTemplateService.updateByEdit(request));
    }

    @PutMapping("favorite/{id}")
    @Operation(summary = "收藏")
    public BusinessResponse<Boolean> favorite(@PathVariable(value = "id") Long id) {
        ResumesTemplate template = new ResumesTemplate();
        template.setId(id);
        return BusinessResponse.success(
                favoriteTemplateService.bind(SecurityContextUtils.getCurrentUser(), template));
    }

    @PutMapping("unfavorite/{id}")
    @Operation(summary = "取消收藏")
    public BusinessResponse<Boolean> unFavorite(@PathVariable(value = "id") Long id) {
        User currentUser = SecurityContextUtils.getCurrentUser();
        ResumesTemplate template = new ResumesTemplate();
        template.setId(id);
        return BusinessResponse.success(favoriteTemplateService.unbind(currentUser, template));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取简历模板信息")
    public BusinessResponse<ResumesTemplateView> get(@PathVariable(value = "id") Long id) {
        ResumesTemplateView resumesTemplate = resumesTemplateService.getViewById(id);
        if (ObjectUtils.isNotEmpty(resumesTemplate)) {
            resumesTemplate.setCollected(
                    favoriteTemplateService.exists(
                            FavoriteTemplate.builder()
                                    .templateId(id)
                                    .userId(SecurityContextUtils.getCurrentUserId())
                                    .build()));
        }
        return BusinessResponse.success(resumesTemplate);
    }

    @PostMapping("page")
    @Operation(summary = "获取简历模板列表")
    public BusinessResponse<Page<ResumesTemplateView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesTemplateQuery request) {

        Page<ResumesTemplateView> viewPage;
        if (request.isCollected()) {
            viewPage =
                    resumesTemplateService.toViewPage(
                            favoriteTemplateService.pageRight(
                                    page,
                                    size,
                                    FavoriteTemplate.builder()
                                            .userId(SecurityContextUtils.getCurrentUserId())
                                            .build()));
            viewPage.getRecords()
                    .forEach(
                            v -> {
                                v.setCollected(true);
                            });
            return BusinessResponse.success(viewPage);
        } else {
            viewPage = resumesTemplateService.pageViewByQuery(page, size, request);
            Set<Long> templateIds =
                    favoriteTemplateService.listRightIds(
                            FavoriteTemplate.builder()
                                    .userId(SecurityContextUtils.getCurrentUserId())
                                    .build());
            if (ObjectUtils.isNotEmpty(templateIds)) {
                viewPage.getRecords()
                        .forEach(
                                v -> {
                                    v.setCollected(templateIds.contains(v.getId()));
                                });
            }
        }

        return BusinessResponse.success(viewPage);
    }

    @PostMapping("index")
    @Operation(summary = "获取首页列表")
    public BusinessResponse<Page<ResumesTemplateView>> index(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody ResumesTemplateQuery request) {
        return BusinessResponse.success(
                resumesTemplateService.pageViewByQuery(page, size, request));
    }
}
