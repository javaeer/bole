package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.RegionCreate;
import cn.net.yunlou.bole.model.edit.RegionEdit;
import cn.net.yunlou.bole.model.entity.Region;
import cn.net.yunlou.bole.model.query.RegionQuery;
import cn.net.yunlou.bole.model.view.RegionView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/** 映射器 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RegionStructMapper
        extends BaseStructMapper<Region, RegionCreate, RegionView, RegionEdit, RegionQuery> {}
