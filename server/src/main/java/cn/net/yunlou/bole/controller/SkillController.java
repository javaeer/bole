package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.model.request.SkillAddRequest;
import cn.net.yunlou.bole.model.request.SkillEditRequest;
import cn.net.yunlou.bole.model.request.SkillSearchRequest;
import cn.net.yunlou.bole.service.SkillService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: SkillController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("skill")
@Tag(name = "12.职业技能管理", description = "职业技能相关接口")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @PostMapping("add")
    @Operation(summary = "新增职业技能")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> add(
            @RequestBody SkillAddRequest request) {
        Skill skill = QueryUtils.modelToBean(request, Skill.class);
        return BusinessResponse.success(skillService.save(skill));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除职业技能")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(
            @RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(skillService.removeById(id));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑职业技能")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(
            @RequestBody SkillEditRequest request) {
        Skill skill = QueryUtils.modelToBean(request, Skill.class);
        return BusinessResponse.success(skillService.updateById(skill));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取职业技能信息")
    public BusinessResponse<Skill> get(
            @PathVariable(value = "id") Long id) {
        return BusinessResponse.success(skillService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取职业技能列表")
    public BusinessResponse<Page<Skill>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody SkillSearchRequest request) {
        Skill skill = QueryUtils.modelToBean(request, Skill.class);
        return BusinessResponse.success(skillService.page(page, size, skill));
    }

}
