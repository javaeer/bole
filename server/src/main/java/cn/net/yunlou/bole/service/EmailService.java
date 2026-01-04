package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.EmailCreate;
import cn.net.yunlou.bole.model.edit.EmailEdit;
import cn.net.yunlou.bole.model.entity.Email;
import cn.net.yunlou.bole.model.query.EmailQuery;
import cn.net.yunlou.bole.model.view.EmailView;
import jakarta.validation.Valid;

/**
 * FileName: EmailService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface EmailService
        extends IBaseService<Email, EmailCreate, EmailView, EmailEdit, EmailQuery> {

    boolean send(@Valid EmailCreate create);

    boolean verify(Email entity);
}
