package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.model.CityCreate;
import cn.net.yunlou.bole.model.CityEdit;
import cn.net.yunlou.bole.model.CityQuery;
import cn.net.yunlou.bole.model.CityView;
import org.mapstruct.*;

/** 映射器 */
@Mapper(componentModel = "spring")
public interface CityStructMapper
        extends BaseStructMapper<City, CityCreate, CityView, CityEdit, CityQuery> {}
