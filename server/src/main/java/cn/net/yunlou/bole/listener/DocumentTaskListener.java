package cn.net.yunlou.bole.listener;

import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.constant.TaskStatus;
import cn.net.yunlou.bole.handler.DocumentGeneratorStrategyFactory;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import cn.net.yunlou.bole.model.entity.DocumentTask;
import cn.net.yunlou.bole.model.entity.File;
import cn.net.yunlou.bole.service.DocumentTaskService;
import cn.net.yunlou.bole.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentTaskListener {

    private final DocumentTaskService documentTaskService;

    private final DocumentGeneratorStrategyFactory documentGeneratorStrategyFactory;

    private final FileService fileService;


    @RabbitListener(queues = "${document.task.queue-name}")
    public void handleTask(@Payload Long taskId) {
        log.info("开始处理文档生成任务: {}", taskId);

        DocumentTask task = documentTaskService.getById(taskId);
        if (ObjectUtils.isEmpty(task)) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "任务不存在: " + taskId);
        }

        // 更新为处理中状态
        task.setStatus(TaskStatus.PROCESSING);
        task.setStartAt(LocalDateTime.now());
        documentTaskService.updateById(task);

        try {
            // 根据文档类型选择生成器

            IDocumentGeneratorStrategy documentGeneratorStrategy = documentGeneratorStrategyFactory.getDocumentGeneratorStrategy(task.getDocumentType());
            java.io.File file = documentGeneratorStrategy.generate(task.getResumesId());

            // 上传到文件存储
            String fileName = task.getFileName() != null ?
                    task.getFileName() : generateFileName(task);


            // 上传到文件存储
            File uploaded = fileService.uploadLocalFile(file, fileName);

            // 更新任务状态
            task.setStatus(TaskStatus.SUCCESS);
            task.setFileUrl(uploaded.getAccessUrl());
            task.setFileName(uploaded.getFileName());
            task.setFileSize(uploaded.getFileSizeBytes());
            task.setEndAt(LocalDateTime.now());
            documentTaskService.updateById(task);

            log.info("文档生成任务完成: taskId={}, fileUrl={}", taskId, uploaded.getAccessUrl());

            // 清理临时文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            log.error("文档生成任务失败: {}", taskId, e);
            task.setStatus(TaskStatus.FAILED);
            task.setErrorMessage(e.getMessage());
            task.setEndAt(LocalDateTime.now());
            documentTaskService.updateById(task);
        }
    }

    private String generateFileName(DocumentTask task) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date());

        String extension = task.getDocumentType().name().toLowerCase();
        if (extension.equals("word")) {
            extension = "docx";
        }

        return String.format("resume_%s_%s.%s",
                task.getResumesId(), dateStr, extension);
    }
}