package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.entity.Company;
import cn.net.yunlou.bole.model.entity.FollowCompany;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.create.CompanyCreate;
import cn.net.yunlou.bole.model.edit.CompanyEdit;
import cn.net.yunlou.bole.model.query.CompanyQuery;
import cn.net.yunlou.bole.model.view.CompanyView;
import cn.net.yunlou.bole.service.CompanyService;
import cn.net.yunlou.bole.service.FollowCompanyService;
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
 * FileName: CompanyController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
 */
@RestController
@RequestMapping("company")
@Tag(name = "04.企业管理", description = "企业相关接口")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    private final FollowCompanyService followCompanyService;

    @PostMapping("add")
    @Operation(summary = "新增企业")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody CompanyCreate request) {
        return BusinessResponse.success(companyService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(companyService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑企业")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid CompanyEdit request) {
        return BusinessResponse.success(companyService.updateByEdit(request));
    }

    @PutMapping("follow/{id}")
    @Operation(summary = "关注")
    public BusinessResponse<Boolean> follow(@PathVariable(value = "id") Long id) {
        Company company = new Company();
        company.setId(id);
        return BusinessResponse.success(
                followCompanyService.bind(SecurityContextUtils.getCurrentUser(), company));
    }

    @PutMapping("unfollow/{id}")
    @Operation(summary = "取消关注")
    public BusinessResponse<Boolean> unfollow(@PathVariable(value = "id") Long id) {
        User currentUser = SecurityContextUtils.getCurrentUser();
        Company company = new Company();
        company.setId(id);
        return BusinessResponse.success(followCompanyService.unbind(currentUser, company));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取企业信息")
    public BusinessResponse<CompanyView> get(@PathVariable(value = "id") Long id) {
        CompanyView view = companyService.getViewById(id);
        if (ObjectUtils.isNotEmpty(view)) {
            Long currentUserId = SecurityContextUtils.getCurrentUserId();
            view.setFollowed(
                    followCompanyService.exists(
                            FollowCompany.builder().userId(currentUserId).companyId(id).build()));
        }
        return BusinessResponse.success(view);
    }

    @PostMapping("page")
    @Operation(summary = "获取企业列表")
    public BusinessResponse<Page<CompanyView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanyQuery request) {

        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        Page<CompanyView> viewPage;
        if (request.isFollowed()) {
            viewPage =
                    companyService.toViewPage(
                            followCompanyService.pageRight(
                                    page,
                                    size,
                                    FollowCompany.builder().userId(currentUserId).build()));
        } else {
            viewPage = companyService.pageViewByQuery(page, size, request);
            if (currentUserId != null) {
                Set<Long> followedIds =
                        followCompanyService.listRightIds(
                                FollowCompany.builder().userId(currentUserId).build());
                viewPage.getRecords()
                        .forEach(
                                view -> {
                                    view.setFollowed(followedIds.contains(view.getId()));
                                });
            }
        }

        return BusinessResponse.success(viewPage);
    }
}
