package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseTreeService;
import cn.net.yunlou.bole.model.create.RegionCreate;
import cn.net.yunlou.bole.model.edit.RegionEdit;
import cn.net.yunlou.bole.model.entity.Region;
import cn.net.yunlou.bole.model.query.RegionQuery;
import cn.net.yunlou.bole.model.view.RegionView;

;

/**
 * FileName: CityService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface RegionService
        extends IBaseTreeService<Region,
        RegionCreate,
        RegionView,
        RegionEdit,
        RegionQuery> {
    RegionView findNearestRegion(Double longitude, Double latitude, Integer level);
}
