package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.entity.SelfEvaluation;
import cn.net.yunlou.bole.model.create.SelfEvaluationCreate;
import cn.net.yunlou.bole.model.edit.SelfEvaluationEdit;
import cn.net.yunlou.bole.model.query.SelfEvaluationQuery;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

/**
 * FileName: SelfEvaluationStructMapper Description: Created By laughtiger Created At 2025/12/14
 * 04:13 Modified By Modified At
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface SelfEvaluationStructMapper
        extends BaseStructMapper<
                SelfEvaluation,
                SelfEvaluationCreate,
                SelfEvaluationView,
                SelfEvaluationEdit,
                SelfEvaluationQuery> {}
