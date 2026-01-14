package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.create.FeedbackCreate;
import cn.net.yunlou.bole.model.edit.FeedbackEdit;
import cn.net.yunlou.bole.model.entity.Feedback;
import cn.net.yunlou.bole.model.query.FeedbackQuery;
import cn.net.yunlou.bole.model.view.FeedbackView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: FeedbackStructMapper Description: Created By laughtiger Created At 2025/12/29 16:59
 * Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FeedbackStructMapper
        extends BaseStructMapper<
                Feedback, FeedbackCreate, FeedbackView, FeedbackEdit, FeedbackQuery> {}
