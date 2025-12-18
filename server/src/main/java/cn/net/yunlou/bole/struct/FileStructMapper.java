package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.model.create.FileCreate;
import cn.net.yunlou.bole.model.edit.FileEdit;
import cn.net.yunlou.bole.model.query.FileQuery;
import cn.net.yunlou.bole.model.view.FileView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: FileStructMapper Description: Created By laughtiger Created At 2025/12/4 01:28 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FileStructMapper
        extends BaseStructMapper<File, FileCreate, FileView, FileEdit, FileQuery> {}
