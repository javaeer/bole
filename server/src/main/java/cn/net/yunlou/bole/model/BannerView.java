package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseView;
import lombok.*;

/**
 * FileName: SystemBannerDTO Description: Created By laughtiger Created At 2025/11/28 13:23 Modified
 * By Modified At
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BannerView extends BaseView {

    private String name;

    private String image;

    private String path;

    private Integer sort;
}
