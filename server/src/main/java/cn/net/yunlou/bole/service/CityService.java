package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseTreeService;
import cn.net.yunlou.bole.model.entity.City;
import cn.net.yunlou.bole.model.create.CityCreate;
import cn.net.yunlou.bole.model.edit.CityEdit;
import cn.net.yunlou.bole.model.query.CityQuery;
import cn.net.yunlou.bole.model.view.CityView;

/**
 * FileName: CityService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface CityService
        extends IBaseTreeService<City, CityCreate, CityView, CityEdit, CityQuery> {}
