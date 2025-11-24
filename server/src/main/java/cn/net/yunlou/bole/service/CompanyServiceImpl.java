package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.SkipInvalidValueLambdaQueryWrapper;
import cn.net.yunlou.bole.constant.CompanyKeyFieldEnum;
import cn.net.yunlou.bole.constant.UserKeyFieldEnum;
import cn.net.yunlou.bole.entity.Company;
import cn.net.yunlou.bole.mapper.CompanyMapper;
import cn.net.yunlou.bole.utils.ValueUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * FileName: CompanyServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/11/24 21:29
 * Modified By
 * Modified At
 */
@Slf4j
@Service
public class CompanyServiceImpl extends BaseService<CompanyMapper, Company> implements CompanyService {



    @Override
    protected SkipInvalidValueLambdaQueryWrapper<Company> getKeyFieldQueryWrapper(SkipInvalidValueLambdaQueryWrapper<Company> queryWrapper, Company entity) {

        CompanyKeyFieldEnum ckfe = ValueUtils.isValid(entity.getKeyField())
                ? IEnum.getEnumByValue(entity.getKeyField(), CompanyKeyFieldEnum.class)
                : CompanyKeyFieldEnum.ALL;

        if (ckfe == null) {
            log.warn("Unknown key field: {}, using default ALL search", entity.getKeyField());
            ckfe = CompanyKeyFieldEnum.ALL;
        }

        ckfe.applyQuery(queryWrapper, entity.getKeyWords());
        return queryWrapper;
    }
}
