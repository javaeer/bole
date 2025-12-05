package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Dict;
import cn.net.yunlou.bole.model.DictCreate;
import cn.net.yunlou.bole.model.DictEdit;
import cn.net.yunlou.bole.model.DictQuery;
import cn.net.yunlou.bole.model.DictView;
import org.mapstruct.Mapper;

/**
 * FileName: DictStructMapper Description: Created By laughtiger Created At 2025/11/30 22:10
 * Modified By Modified At
 */
@Mapper(componentModel = "spring")
public interface DictStructMapper
        extends BaseStructMapper<Dict, DictCreate, DictView, DictEdit, DictQuery> {}
