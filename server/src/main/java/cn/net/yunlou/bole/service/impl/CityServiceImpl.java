package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseTreeService;
import cn.net.yunlou.bole.common.annotation.TreeServiceConfig;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.mapper.CityMapper;
import cn.net.yunlou.bole.model.CityCreate;
import cn.net.yunlou.bole.model.CityEdit;
import cn.net.yunlou.bole.model.CityQuery;
import cn.net.yunlou.bole.model.CityView;
import cn.net.yunlou.bole.service.CityService;
import cn.net.yunlou.bole.struct.CityStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: CityService Description: Created By MR. WANG Created At 2025/11/25 22:04 Modified By
 * Modified At
 */
@Service
@TreeServiceConfig(cacheName = "cityTree", keyPrefix = "city")
public class CityServiceImpl
        extends BaseTreeService<
                CityMapper, City, CityCreate, CityView, CityEdit, CityQuery, CityStructMapper>
        implements CityService {}
