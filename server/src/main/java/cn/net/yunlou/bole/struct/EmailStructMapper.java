package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.entity.Email;
import cn.net.yunlou.bole.model.create.EmailCreate;
import cn.net.yunlou.bole.model.edit.EmailEdit;
import cn.net.yunlou.bole.model.query.EmailQuery;
import cn.net.yunlou.bole.model.view.EmailView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: SmsStructMapper Description: Created By laughtiger Created At 2025/12/25 01:24 Modified
 * By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EmailStructMapper
        extends BaseStructMapper<Email, EmailCreate, EmailView, EmailEdit, EmailQuery> {}
