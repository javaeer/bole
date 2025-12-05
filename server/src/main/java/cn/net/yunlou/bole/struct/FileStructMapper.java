package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.File;
import cn.net.yunlou.bole.model.FileCreate;
import cn.net.yunlou.bole.model.FileEdit;
import cn.net.yunlou.bole.model.FileQuery;
import cn.net.yunlou.bole.model.FileView;
import org.mapstruct.Mapper;

/**
 * FileName: FileStructMapper Description: Created By laughtiger Created At 2025/12/4 01:28 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring")
public interface FileStructMapper
        extends BaseStructMapper<File, FileCreate, FileView, FileEdit, FileQuery> {}
