package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.CompanyKeyField;
import cn.net.yunlou.bole.common.utils.ValueUtils;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.mapper.CompanyMapper;
import cn.net.yunlou.bole.model.CompanyCreate;
import cn.net.yunlou.bole.model.CompanyEdit;
import cn.net.yunlou.bole.model.CompanyQuery;
import cn.net.yunlou.bole.model.CompanyView;
import cn.net.yunlou.bole.service.CompanyService;
import cn.net.yunlou.bole.struct.CompanyStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * FileName: CompanyServiceImpl Description: Created By MR. WANG Created At 2025/11/24 21:29
 * Modified By Modified At
 */
@Slf4j
@Service
public class CompanyServiceImpl
        extends BaseService<
                CompanyMapper,
                Company,
                CompanyCreate,
                CompanyView,
                CompanyEdit,
                CompanyQuery,
                CompanyStructMapper>
        implements CompanyService {

    @Override
    protected QueryWrapper<Company> getKeyFieldQueryWrapper(
            QueryWrapper<Company> queryWrapper, Company entity) {

        CompanyKeyField ckfe =
                ValueUtils.isValid(entity.getKeyField())
                        ? IEnum.getEnumByValue(entity.getKeyField(), CompanyKeyField.class)
                        : CompanyKeyField.ALL;

        if (ckfe == null) {
            log.warn("Unknown key field: {}, using default ALL search", entity.getKeyField());
            ckfe = CompanyKeyField.ALL;
        }

        ckfe.applyQuery(queryWrapper, entity.getKeyWords());
        return queryWrapper;
    }
}
