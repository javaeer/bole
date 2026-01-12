package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.DocumentTaskCreate;
import cn.net.yunlou.bole.model.edit.DocumentTaskEdit;
import cn.net.yunlou.bole.model.entity.DocumentTask;
import cn.net.yunlou.bole.model.query.DocumentTaskQuery;
import cn.net.yunlou.bole.model.view.DocumentTaskView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: DocumentTaskStructMapper Description: Created By laughtiger Created At 2026/1/9 21:59
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DocumentTaskStructMapper
        extends BaseStructMapper<
                DocumentTask,
                DocumentTaskCreate,
                DocumentTaskView,
                DocumentTaskEdit,
                DocumentTaskQuery> {}
