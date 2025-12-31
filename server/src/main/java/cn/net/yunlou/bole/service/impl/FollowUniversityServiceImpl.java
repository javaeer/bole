package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.MultiService;
import cn.net.yunlou.bole.entity.FollowUniversity;
import cn.net.yunlou.bole.entity.University;
import cn.net.yunlou.bole.entity.User;
import cn.net.yunlou.bole.mapper.FollowUniversityMapper;
import cn.net.yunlou.bole.service.FollowUniversityService;
import org.springframework.stereotype.Service;

/**
 * FileName: FollowUniversityServiceImpl Description: Created By laughtiger Created At 2025/12/30
 * 17:53 Modified By Modified At
 */
@Service
public class FollowUniversityServiceImpl
        extends MultiService<FollowUniversityMapper, FollowUniversity, User, University>
        implements FollowUniversityService {
    @Override
    public FollowUniversity createEntity(User left, University right) {
        return null;
    }
}
