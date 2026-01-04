package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.BannerCreate;
import cn.net.yunlou.bole.model.edit.BannerEdit;
import cn.net.yunlou.bole.model.entity.Banner;
import cn.net.yunlou.bole.model.query.BannerQuery;
import cn.net.yunlou.bole.model.view.BannerView;

/**
 * FileName: SystemBannerService Description: Created By laughtiger Created At 2025/11/28 12:09
 * Modified By Modified At
 */
public interface BannerService
        extends IBaseService<Banner, BannerCreate, BannerView, BannerEdit, BannerQuery> {}
