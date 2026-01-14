package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.model.create.UniversityCreate;
import cn.net.yunlou.bole.model.edit.UniversityEdit;
import cn.net.yunlou.bole.model.entity.FollowUniversity;
import cn.net.yunlou.bole.model.entity.University;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.model.query.UniversityQuery;
import cn.net.yunlou.bole.model.view.UniversityView;
import cn.net.yunlou.bole.service.FollowUniversityService;
import cn.net.yunlou.bole.service.UniversityService;
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
 * FileName: UniversityController Description: Created By MR. WANG Created At 2025/11/24 21:27
 * Modified By Modified At
 */
@RestController
@RequestMapping("university")
@Tag(name = "26.大学管理", description = "大学相关接口")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    private final FollowUniversityService followUniversityService;

    @PostMapping("add")
    @Operation(summary = "新增大学")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(@RequestBody UniversityCreate request) {
        return BusinessResponse.success(universityService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除大学")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "id") Long id) {
        return BusinessResponse.success(universityService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑大学")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid UniversityEdit request) {
        return BusinessResponse.success(universityService.updateByEdit(request));
    }

    @PutMapping("follow/{id}")
    @Operation(summary = "关注")
    public BusinessResponse<Boolean> follow(@PathVariable(value = "id") Long id) {
        University university = new University();
        university.setId(id);
        return BusinessResponse.success(
                followUniversityService.bind(SecurityContextUtils.getCurrentUser(), university));
    }

    @PutMapping("unfollow/{id}")
    @Operation(summary = "取消关注")
    public BusinessResponse<Boolean> unfollow(@PathVariable(value = "id") Long id) {
        User currentUser = SecurityContextUtils.getCurrentUser();
        University university = new University();
        university.setId(id);
        return BusinessResponse.success(followUniversityService.unbind(currentUser, university));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取大学信息")
    public BusinessResponse<UniversityView> get(@PathVariable(value = "id") Long id) {
        UniversityView view = universityService.getViewById(id);
        if (ObjectUtils.isNotEmpty(view)) {
            Long currentUserId = SecurityContextUtils.getCurrentUserId();
            view.setFollowed(
                    followUniversityService.exists(
                            FollowUniversity.builder()
                                    .userId(currentUserId)
                                    .universityId(id)
                                    .build()));
        }
        return BusinessResponse.success(view);
    }

    @PostMapping("page")
    @Operation(summary = "获取大学列表")
    public BusinessResponse<Page<UniversityView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody UniversityQuery request) {

        Long currentUserId = SecurityContextUtils.getCurrentUserId();
        Page<UniversityView> viewPage;
        if (request.isFollowed()) {
            viewPage =
                    universityService.toViewPage(
                            followUniversityService.pageRight(
                                    page,
                                    size,
                                    FollowUniversity.builder().userId(currentUserId).build()));
        } else {
            viewPage = universityService.pageViewByQuery(page, size, request);
            if (currentUserId != null) {
                Set<Long> followedIds =
                        followUniversityService.listRightIds(
                                FollowUniversity.builder().userId(currentUserId).build());
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
