package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.service.ResumesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;

/**
 * FileName: HtmlDocumentGeneratorStrategy
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:25
 * Modified By
 * Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlDocumentGeneratorStrategy implements IDocumentGeneratorStrategy {

    private final TemplateEngine templateEngine;

    private final ResumesService resumesService;

    @Override
    public DocumentType getType() {
        return DocumentType.HTML;
    }

    @Override
    public File generate(Long resumesId) {

        //获取数据
        Resumes resumes = resumesService.getById(resumesId);

        //  渲染HTML
        Context context = new Context();
        context.setVariables(BeanUtils.toMap(resumes));

        templateEngine.process("resumes/resumes", context);

        return null;
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType == DocumentType.HTML;
    }
}
