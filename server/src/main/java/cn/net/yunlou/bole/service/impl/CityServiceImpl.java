package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.model.dto.CityDTO;
import cn.net.yunlou.bole.struct.CityStructMapper;
import cn.net.yunlou.bole.model.query.CityQuery;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.mapper.CityMapper;
import cn.net.yunlou.bole.service.CityService;
import org.springframework.stereotype.Service;

/**
 * FileName: CityService
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 22:04
 * Modified By
 * Modified At
 */
@Service
public class CityServiceImpl extends BaseService<CityMapper, City, CityDTO, CityQuery, CityStructMapper> implements CityService {
}