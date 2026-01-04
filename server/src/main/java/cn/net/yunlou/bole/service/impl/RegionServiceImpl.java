package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseTreeService;
import cn.net.yunlou.bole.common.annotation.TreeServiceConfig;
import cn.net.yunlou.bole.common.constant.StatusType;
import cn.net.yunlou.bole.common.utils.RedissonLockUtils;
import cn.net.yunlou.bole.mapper.RegionMapper;
import cn.net.yunlou.bole.model.create.RegionCreate;
import cn.net.yunlou.bole.model.edit.RegionEdit;
import cn.net.yunlou.bole.model.entity.Region;
import cn.net.yunlou.bole.model.query.RegionQuery;
import cn.net.yunlou.bole.model.view.RegionView;
import cn.net.yunlou.bole.service.RegionService;
import cn.net.yunlou.bole.struct.RegionStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

;

/**
 * FileName: RegionServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2026/1/2 23:11
 * Modified By
 * Modified At
 */
@Service
@TreeServiceConfig(cacheName = "regionTree", keyPrefix = "region")
public class RegionServiceImpl
        extends BaseTreeService<RegionMapper,
        Region,
        RegionCreate,
        RegionView,
        RegionEdit,
        RegionQuery,
        RegionStructMapper> implements RegionService {

    public RegionServiceImpl(RedissonLockUtils redissonLockUtils) {
        super(redissonLockUtils);
    }

    @Override
    protected String getTableName() {
        return "t_region";
    }


    @Override
    public RegionView findNearestRegion(Double longitude, Double latitude, Integer level) {
        // 这里可以使用地理空间查询，例如使用MySQL的ST_Distance函数
        // 或者使用Redis GEO命令

        LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Region::getStatus, 1)
                .isNotNull(Region::getLongitude)
                .isNotNull(Region::getLatitude);

        if (level != null) {
            queryWrapper.eq(Region::getLevel, level);
        }

        List<Region> entities = this.list(queryWrapper);

        // 简单计算距离（实际项目应使用更高效的距离算法）
        Region nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Region entity : entities) {
            double distance = calculateDistance(
                    longitude, latitude,
                    entity.getLongitude(), entity.getLatitude()
            );

            if (distance < minDistance) {
                minDistance = distance;
                nearest = entity;
            }
        }

        return nearest != null ? structMapper.toView(nearest) : null;
    }

    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        // 使用Haversine公式计算球面距离
        final int R = 6371; // 地球半径（公里）

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
