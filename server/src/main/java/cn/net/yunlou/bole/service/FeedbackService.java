package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.FeedbackCreate;
import cn.net.yunlou.bole.model.edit.FeedbackEdit;
import cn.net.yunlou.bole.model.entity.Feedback;
import cn.net.yunlou.bole.model.query.FeedbackQuery;
import cn.net.yunlou.bole.model.view.FeedbackView;

/**
 * FileName: FeedbackService Description: Created By laughtiger Created At 2025/12/29 16:56 Modified
 * By Modified At
 */
public interface FeedbackService
        extends IBaseService<Feedback, FeedbackCreate, FeedbackView, FeedbackEdit, FeedbackQuery> {}
