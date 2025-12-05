package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.Skill;
import cn.net.yunlou.bole.model.SkillCreate;
import cn.net.yunlou.bole.model.SkillEdit;
import cn.net.yunlou.bole.model.SkillQuery;
import cn.net.yunlou.bole.model.SkillView;
import cn.net.yunlou.bole.service.SkillService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: SkillController Description: Created By MR. WANG Created At 2025/11/24 21:27 Modified
 * By Modified At
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
    public BusinessResponse<Boolean> add(@RequestBody SkillCreate request) {
        return BusinessResponse.success(skillService.saveByCreate(request));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除职业技能")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> del(@RequestParam(value = "主键") Long id) {
        return BusinessResponse.success(skillService.removeById(id));
    }

    @PutMapping("edit")
    @Operation(summary = "编辑职业技能")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public BusinessResponse<Boolean> edit(@RequestBody @Valid SkillEdit request) {
        return BusinessResponse.success(skillService.updateByEdit(request));
    }

    @GetMapping("{id}")
    @Operation(summary = "获取职业技能信息")
    public BusinessResponse<Skill> get(@PathVariable(value = "id") Long id) {
        return BusinessResponse.success(skillService.getById(id));
    }

    @PostMapping("page")
    @Operation(summary = "获取职业技能列表")
    public BusinessResponse<Page<SkillView>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody SkillQuery request) {
        return BusinessResponse.success(skillService.pageViewByQuery(page, size, request));
    }
}
