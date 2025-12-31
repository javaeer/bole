package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseTreeService;
import cn.net.yunlou.bole.common.annotation.TreeServiceConfig;
import cn.net.yunlou.bole.model.entity.Dict;
import cn.net.yunlou.bole.mapper.DictMapper;
import cn.net.yunlou.bole.model.create.DictCreate;
import cn.net.yunlou.bole.model.edit.DictEdit;
import cn.net.yunlou.bole.model.query.DictQuery;
import cn.net.yunlou.bole.model.view.DictView;
import cn.net.yunlou.bole.service.DictService;
import cn.net.yunlou.bole.struct.DictStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: DictServiceImpl Description: Created By laughtiger Created At 2025/11/30 22:09 Modified
 * By Modified At
 */
@Service
@TreeServiceConfig(cacheName = "dictTree", keyPrefix = "dict")
public class DictServiceImpl
        extends BaseTreeService<
                DictMapper, Dict, DictCreate, DictView, DictEdit, DictQuery, DictStructMapper>
        implements DictService {}
