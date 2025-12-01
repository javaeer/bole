package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.Dict;
import cn.net.yunlou.bole.model.dto.DictDTO;
import cn.net.yunlou.bole.model.query.DictQuery;
import org.mapstruct.Mapper;

/**
 * FileName: DictStructMapper
 * Description:
 * Created By laughtiger
 * Created At 2025/11/30 22:10
 * Modified By
 * Modified At
 */
@Mapper(componentModel = "spring")
public interface DictStructMapper extends BaseStructMapper<Dict, DictDTO, DictQuery> {
}
