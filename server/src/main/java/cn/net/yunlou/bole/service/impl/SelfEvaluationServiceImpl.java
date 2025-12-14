package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.SelfEvaluation;
import cn.net.yunlou.bole.mapper.SelfEvaluationMapper;
import cn.net.yunlou.bole.model.create.SelfEvaluationCreate;
import cn.net.yunlou.bole.model.edit.SelfEvaluationEdit;
import cn.net.yunlou.bole.model.query.SelfEvaluationQuery;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;
import cn.net.yunlou.bole.service.SelfEvaluationService;
import cn.net.yunlou.bole.struct.SelfEvaluationStructMapper;
import org.springframework.stereotype.Service;

/**
 * FileName: SelfEvaluationServiceImpl Description: Created By laughtiger Created At 2025/12/14
 * 04:11 Modified By Modified At
 */
@Service
public class SelfEvaluationServiceImpl
        extends BaseService<
                SelfEvaluationMapper,
                SelfEvaluation,
                SelfEvaluationCreate,
                SelfEvaluationView,
                SelfEvaluationEdit,
                SelfEvaluationQuery,
                SelfEvaluationStructMapper>
        implements SelfEvaluationService {}
