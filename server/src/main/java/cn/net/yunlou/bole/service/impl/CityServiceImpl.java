package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseTreeService;
import cn.net.yunlou.bole.common.TreeServiceConfig;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.mapper.CityMapper;
import cn.net.yunlou.bole.model.dto.CityDTO;
import cn.net.yunlou.bole.model.query.CityQuery;
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
        extends BaseTreeService<CityMapper, City, CityDTO, CityQuery, CityStructMapper>
        implements CityService {}
