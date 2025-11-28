package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.SystemBanner;
import cn.net.yunlou.bole.model.dto.SystemBannerDTO;
import cn.net.yunlou.bole.model.query.SystemBannerQuery;
import org.mapstruct.Mapper;

/**
 * FileName: SystemBannerStructMapper
 * Description:
 * Created By laughtiger
 * Created At 2025/11/28 13:26
 * Modified By
 * Modified At
 */

@Mapper(componentModel = "spring")
public interface SystemBannerStructMapper extends BaseStructMapper<SystemBanner, SystemBannerDTO, SystemBannerQuery> {
}
