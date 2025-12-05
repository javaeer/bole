package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.entity.Banner;
import cn.net.yunlou.bole.model.BannerCreate;
import cn.net.yunlou.bole.model.BannerEdit;
import cn.net.yunlou.bole.model.BannerQuery;
import cn.net.yunlou.bole.model.BannerView;

/**
 * FileName: SystemBannerService Description: Created By laughtiger Created At 2025/11/28 12:09
 * Modified By Modified At
 */
public interface BannerService
        extends IBaseService<Banner, BannerCreate, BannerView, BannerEdit, BannerQuery> {}
