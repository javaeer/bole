package cn.net.yunlou.bole.service;

import cn.net.yunlou.bole.common.IBaseService;
import cn.net.yunlou.bole.model.create.SelfEvaluationCreate;
import cn.net.yunlou.bole.model.edit.SelfEvaluationEdit;
import cn.net.yunlou.bole.model.entity.SelfEvaluation;
import cn.net.yunlou.bole.model.query.SelfEvaluationQuery;
import cn.net.yunlou.bole.model.view.SelfEvaluationView;

/**
 * FileName: SystemBannerService Description: Created By laughtiger Created At 2025/11/28 12:09
 * Modified By Modified At
 */
public interface SelfEvaluationService
        extends IBaseService<
                SelfEvaluation,
                SelfEvaluationCreate,
                SelfEvaluationView,
                SelfEvaluationEdit,
                SelfEvaluationQuery> {}
