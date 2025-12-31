package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.MultiService;
import cn.net.yunlou.bole.model.entity.Company;
import cn.net.yunlou.bole.model.entity.FollowCompany;
import cn.net.yunlou.bole.model.entity.User;
import cn.net.yunlou.bole.mapper.FollowCompanyMapper;
import cn.net.yunlou.bole.service.FollowCompanyService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * FileName: FollowCompanyServiceImpl Description: Created By laughtiger Created At 2025/12/24 19:45
 * Modified By Modified At
 */
@Service
public class FollowCompanyServiceImpl
        extends MultiService<FollowCompanyMapper, FollowCompany, User, Company>
        implements FollowCompanyService {
    @Override
    public FollowCompany createEntity(User left, Company right) {
        return FollowCompany.builder()
                .userId(
                        ObjectUtils.isNotEmpty(left) && ObjectUtils.isNotEmpty(left.getId())
                                ? left.getId()
                                : null)
                .companyId(
                        ObjectUtils.isNotEmpty(right) && ObjectUtils.isNotEmpty(right.getId())
                                ? right.getId()
                                : null)
                .build();
    }
}
