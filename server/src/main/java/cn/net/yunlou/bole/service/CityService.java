package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseTreeService;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.model.CityCreate;
import cn.net.yunlou.bole.model.CityEdit;
import cn.net.yunlou.bole.model.CityQuery;
import cn.net.yunlou.bole.model.CityView;

/**
 * FileName: CityService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface CityService
        extends IBaseTreeService<City, CityCreate, CityView, CityEdit, CityQuery> {}
