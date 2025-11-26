package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.BusinessResponse;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.model.request.CompanyAddRequest;
import cn.net.yunlou.bole.model.request.CompanyEditRequest;
import cn.net.yunlou.bole.model.request.CompanySearchRequest;
import cn.net.yunlou.bole.service.CompanyService;
import cn.net.yunlou.bole.common.utils.QueryUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * FileName: CompanyController
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/24 21:27
 * Modified By
 * Modified At
 */

@RestController
@RequestMapping("/api/company")
@Tag(name = "04.企业管理", description = "企业相关接口")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("add")
    @Operation(summary = "新增企业")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> add(
            @RequestBody CompanyAddRequest request) {
        Company company = QueryUtils.modelToBean(request, Company.class);
        return ResponseEntity.ok(BusinessResponse.success(companyService.save(company)));
    }

    @DeleteMapping("del")
    @Operation(summary = "删除企业")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> del(
            @RequestParam(value = "主键") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyService.removeById(id)));
    }


    @PutMapping("edit")
    @Operation(summary = "编辑企业")
    @PreAuthorize("hasAnyRole('SUPER','ADMIN')")
    public ResponseEntity<BusinessResponse<Boolean>> edit(
            @RequestBody CompanyEditRequest request) {
        Company company = QueryUtils.modelToBean(request, Company.class);
        return ResponseEntity.ok(BusinessResponse.success(companyService.updateById(company)));
    }


    @GetMapping("{id}")
    @Operation(summary = "获取企业信息")
    public ResponseEntity<BusinessResponse<Company>> get(
            @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(BusinessResponse.success(companyService.getById(id)));
    }

    @PostMapping("page")
    @Operation(summary = "获取企业列表")
    public ResponseEntity<BusinessResponse<Page<Company>>> page(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestBody CompanySearchRequest request) {
        Company company = QueryUtils.modelToBean(request, Company.class);
        return ResponseEntity.ok(BusinessResponse.success(companyService.page(page, size, company)));
    }

}
