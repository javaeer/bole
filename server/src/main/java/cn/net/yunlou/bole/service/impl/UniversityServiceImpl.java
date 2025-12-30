package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.University;
import cn.net.yunlou.bole.mapper.UniversityMapper;
import cn.net.yunlou.bole.model.create.UniversityCreate;
import cn.net.yunlou.bole.model.edit.UniversityEdit;
import cn.net.yunlou.bole.model.query.UniversityQuery;
import cn.net.yunlou.bole.model.view.UniversityView;
import cn.net.yunlou.bole.service.UniversityService;
import cn.net.yunlou.bole.struct.UniversityStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: UniversityServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/12/30 17:49
 * Modified By
 * Modified At
 */
@Service
public class UniversityServiceImpl extends BaseService<UniversityMapper, University, UniversityCreate, UniversityView, UniversityEdit, UniversityQuery, UniversityStructMapper> implements UniversityService {
}
