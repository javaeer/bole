package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.City;
import cn.net.yunlou.bole.model.create.CityCreate;
import cn.net.yunlou.bole.model.edit.CityEdit;
import cn.net.yunlou.bole.model.query.CityQuery;
import cn.net.yunlou.bole.model.view.CityView;
import org.mapstruct.*;

/** 映射器 */
@Mapper(componentModel = "spring")
public interface CityStructMapper
        extends BaseStructMapper<City, CityCreate, CityView, CityEdit, CityQuery> {}
