package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.Feedback;
import cn.net.yunlou.bole.mapper.FeedbackMapper;
import cn.net.yunlou.bole.model.create.FeedbackCreate;
import cn.net.yunlou.bole.model.edit.FeedbackEdit;
import cn.net.yunlou.bole.model.query.FeedbackQuery;
import cn.net.yunlou.bole.model.view.FeedbackView;
import cn.net.yunlou.bole.service.FeedbackService;
import cn.net.yunlou.bole.struct.FeedbackStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: FeedbackServiceImpl Description: Created By laughtiger Created At 2025/12/29 16:57
 * Modified By Modified At
 */
@Service
public class FeedbackServiceImpl
        extends BaseService<
                FeedbackMapper,
                Feedback,
                FeedbackCreate,
                FeedbackView,
                FeedbackEdit,
                FeedbackQuery,
                FeedbackStructMapper>
        implements FeedbackService {}
