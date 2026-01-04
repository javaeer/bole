package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.DictCreate;
import cn.net.yunlou.bole.model.edit.DictEdit;
import cn.net.yunlou.bole.model.entity.Dict;
import cn.net.yunlou.bole.model.query.DictQuery;
import cn.net.yunlou.bole.model.view.DictView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: DictStructMapper Description: Created By laughtiger Created At 2025/11/30 22:10
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DictStructMapper
        extends BaseStructMapper<Dict, DictCreate, DictView, DictEdit, DictQuery> {}
