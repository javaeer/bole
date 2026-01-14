package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.config.DocumentTaskProperties;
import cn.net.yunlou.bole.mapper.DocumentTaskMapper;
import cn.net.yunlou.bole.model.create.DocumentTaskCreate;
import cn.net.yunlou.bole.model.edit.DocumentTaskEdit;
import cn.net.yunlou.bole.model.entity.DocumentTask;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.query.DocumentTaskQuery;
import cn.net.yunlou.bole.model.view.DocumentTaskView;
import cn.net.yunlou.bole.service.DocumentTaskService;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.struct.DocumentTaskStructMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: DocumentTaskServiceImpl Description: Created By laughtiger Created At 2026/1/9 21:57
 * Modified By Modified At
 */
@Slf4j
@Service
@AllArgsConstructor
public class DocumentTaskServiceImpl
        extends BaseService<
                DocumentTaskMapper,
                DocumentTask,
                DocumentTaskCreate,
                DocumentTaskView,
                DocumentTaskEdit,
                DocumentTaskQuery,
                DocumentTaskStructMapper>
        implements DocumentTaskService {

    private final RabbitTemplate rabbitTemplate;

    private final DocumentTaskProperties documentTaskProperties;

    private final ResumesService resumesService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(DocumentTaskCreate create) {
        DocumentTask entity = structMapper.createToEntity(create);

        Resumes resumes = resumesService.getById(entity.getResumesId());
        if (ObjectUtils.isEmpty(resumes)) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "简历不存在");
        }
        entity.setFileName(resumes.getName());

        entity.setUserId(SecurityContextUtils.getCurrentUserId());

        boolean b = save(entity);
        if (b) {
            sendTaskToQueue(entity.getId());
        }
        return b;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByEdit(DocumentTaskEdit edit) {
        boolean b = super.updateByEdit(edit);
        if (b) {
            sendTaskToQueue(edit.getId());
        }
        return b;
    }

    /** 发送任务到队列 */
    private void sendTaskToQueue(Long taskId) {
        rabbitTemplate.convertAndSend(
                documentTaskProperties.getExchangeName(),
                documentTaskProperties.getRoutingKey(),
                taskId);
    }
}
