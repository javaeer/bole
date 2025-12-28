package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.MultiService;
import cn.net.yunlou.bole.entity.FavoriteTemplate;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.FavoriteTemplateMapper;
import cn.net.yunlou.bole.service.FavoriteTemplateService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * FileName: CollectionTemplateServiceImpl Description: Created By laughtiger Created At 2025/12/24
 * 20:00 Modified By Modified At
 */
@Service
public class FavoriteTemplateServiceImpl
        extends MultiService<FavoriteTemplateMapper, FavoriteTemplate, User, ResumesTemplate>
        implements FavoriteTemplateService {

    @Override
    public FavoriteTemplate createEntity(User left, ResumesTemplate right) {
        return FavoriteTemplate.builder()
                .userId(
                        (ObjectUtils.isNotEmpty(left) && ObjectUtils.isNotEmpty(left.getId()))
                                ? left.getId()
                                : null)
                .templateId(
                        (ObjectUtils.isNotEmpty(right) && ObjectUtils.isNotEmpty(right.getId()))
                                ? right.getId()
                                : null)
                .build();
    }
}
