package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.entity.Sms;
import cn.net.yunlou.bole.model.create.SmsCreate;
import cn.net.yunlou.bole.model.edit.SmsEdit;
import cn.net.yunlou.bole.model.query.SmsQuery;
import cn.net.yunlou.bole.model.view.SmsView;
import jakarta.validation.Valid;

/**
 * FileName: SmsService Description: Created By MR. WANG Created At 2025/11/26 01:02 Modified By
 * Modified At
 */
public interface SmsService extends IBaseService<Sms, SmsCreate, SmsView, SmsEdit, SmsQuery> {

    boolean send(@Valid SmsCreate create);

    boolean verify(Sms entity);
}
