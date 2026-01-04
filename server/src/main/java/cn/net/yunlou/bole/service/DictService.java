package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseTreeService;
import cn.net.yunlou.bole.model.create.DictCreate;
import cn.net.yunlou.bole.model.edit.DictEdit;
import cn.net.yunlou.bole.model.entity.Dict;
import cn.net.yunlou.bole.model.query.DictQuery;
import cn.net.yunlou.bole.model.view.DictView;

/**
 * FileName: DictService Description: Created By laughtiger Created At 2025/11/30 22:03 Modified By
 * Modified At
 */
public interface DictService
        extends IBaseTreeService<Dict, DictCreate, DictView, DictEdit, DictQuery> {}
