package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Banner;
import cn.net.yunlou.bole.mapper.BannerMapper;
import cn.net.yunlou.bole.model.BannerCreate;
import cn.net.yunlou.bole.model.BannerEdit;
import cn.net.yunlou.bole.model.BannerQuery;
import cn.net.yunlou.bole.model.BannerView;
import cn.net.yunlou.bole.service.BannerService;
import cn.net.yunlou.bole.struct.BannerStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * FileName: SystemBannerServiceImpl Description: Created By laughtiger Created At 2025/11/28 13:24
 * Modified By Modified At
 */
@Service
public class BannerServiceImpl
        extends BaseService<
                BannerMapper,
                Banner,
                BannerCreate,
                BannerView,
                BannerEdit,
                BannerQuery,
                BannerStructMapper>
        implements BannerService {
    @Override
    public QueryWrapper<Banner> getBaseQueryWrapper(Banner entity) {
        QueryWrapper<Banner> queryWrapper = super.getBaseQueryWrapper(entity);
        if (ObjectUtils.isNotEmpty(entity.getName())) {
            queryWrapper.lambda().like(Banner::getName, entity.getName());
        }
        return queryWrapper;
    }
}
