package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Banner;
import cn.net.yunlou.bole.model.BannerCreate;
import cn.net.yunlou.bole.model.BannerEdit;
import cn.net.yunlou.bole.model.BannerQuery;
import cn.net.yunlou.bole.model.BannerView;
import org.mapstruct.Mapper;

/**
 * FileName: SystemBannerStructMapper Description: Created By laughtiger Created At 2025/11/28 13:26
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface BannerStructMapper
        extends BaseStructMapper<Banner, BannerCreate, BannerView, BannerEdit, BannerQuery> {}
