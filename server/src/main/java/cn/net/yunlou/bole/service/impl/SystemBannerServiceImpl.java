package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.SystemBanner;
import cn.net.yunlou.bole.mapper.SystemBannerMapper;
import cn.net.yunlou.bole.model.dto.SystemBannerDTO;
import cn.net.yunlou.bole.model.query.SystemBannerQuery;
import cn.net.yunlou.bole.service.SystemBannerService;
import cn.net.yunlou.bole.struct.SystemBannerStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: SystemBannerServiceImpl Description: Created By laughtiger Created At 2025/11/28 13:24
 * Modified By Modified At
 */
@Service
public class SystemBannerServiceImpl
        extends BaseService<
                SystemBannerMapper,
                SystemBanner,
                SystemBannerDTO,
                SystemBannerQuery,
                SystemBannerStructMapper>
        implements SystemBannerService {}
