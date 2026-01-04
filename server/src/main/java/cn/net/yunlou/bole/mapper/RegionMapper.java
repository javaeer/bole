package cn.net.yunlou.bole.mapper;

import cn.net.yunlou.bole.common.IBaseTreeMapper;
import cn.net.yunlou.bole.model.entity.Region;

/**
 * FileName: CityMapper Description: Created By MR. WANG Created At 2025/11/26 00:31 Modified By
 * Modified At
 */
public interface RegionMapper extends IBaseTreeMapper<Region> {

    @Override
    int updateChildrenPath(String tableName, String oldParentPath, String path);
}
