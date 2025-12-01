package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseTreeService;
import cn.net.yunlou.bole.common.TreeServiceConfig;
import cn.net.yunlou.bole.entity.Dict;
import cn.net.yunlou.bole.mapper.DictMapper;
import cn.net.yunlou.bole.model.dto.DictDTO;
import cn.net.yunlou.bole.model.query.DictQuery;
import cn.net.yunlou.bole.service.DictService;
import cn.net.yunlou.bole.struct.DictStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: DictServiceImpl
 * Description:
 * Created By laughtiger
 * Created At 2025/11/30 22:09
 * Modified By
 * Modified At
 */
@Service
@TreeServiceConfig(cacheName = "dictTree", keyPrefix = "dict")
public class DictServiceImpl extends BaseTreeService<DictMapper, Dict, DictDTO, DictQuery, DictStructMapper> implements DictService {
}
