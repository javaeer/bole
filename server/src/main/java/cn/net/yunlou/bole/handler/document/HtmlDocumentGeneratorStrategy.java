package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.utils.StyleUtils;
import cn.net.yunlou.bole.config.DocumentGeneratorConfig;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import cn.net.yunlou.bole.handler.resumes.ResumesLayoutCalculator;
import cn.net.yunlou.bole.handler.resumes.ResumesStyleCalculator;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import cn.net.yunlou.bole.service.ResumesService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

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

    private final DocumentGeneratorConfig documentGeneratorConfig;

    private final ResumesLayoutCalculator resumesLayoutCalculator;

    private final ResumesStyleCalculator resumesStyleCalculator;

    @Override
    public DocumentType getType() {
        return DocumentType.HTML;
    }

    @Override
    public File generate(Long resumesId) {

        try {
            // 1. 获取数据
            Resumes resumes = resumesService.getById(resumesId);
            if (resumes == null) {
                throw new ResourceNotFoundException("简历不存在，ID: " + resumesId);
            }

            // 2. 准备数据
            List<ResumesTemplateComponent> components = resumes.getComponents();
            ResumesTemplateStyle globalStyle = resumes.getGlobalStyle();
            ResumesTemplateLayout globalLayout = resumes.getGlobalLayout();

            // 3. 【核心】计算布局
            Map<String, Object> layoutData = resumesLayoutCalculator.calculateLayout(components, globalLayout, globalStyle);

            // 4. 计算容器样式
            String layoutType = (String) layoutData.get("layoutType");
            Map<String, String> containerStyle = resumesStyleCalculator.getContainerStyle(globalStyle, layoutType);

            // 5. 获取响应式样式
            Map<String, String> responsiveStyles = resumesLayoutCalculator.getResponsiveStyles();

            // 6. 创建 Model
            Map<String, Object> variables = Maps.newConcurrentMap();

            // 7. 添加主要数据
            variables.put("resumes", resumes);
            variables.put("device", "desktop");
            variables.put("components", components);

            // 8. 添加布局相关数据
            variables.put("layoutData", layoutData);
            variables.put("globalStyle", globalStyle);
            variables.put("globalLayout", globalLayout);
            variables.put("responsiveStyles", responsiveStyles);
            variables.put("responsiveStylesCss", StyleUtils.toCss(responsiveStyles));
            variables.put("containerStyle", containerStyle);
            variables.put("containerStyleCss", StyleUtils.toCss(containerStyle));


            // 10. 渲染HTML
            Context context = new Context();
            context.setVariables(variables);

            // 11. 创建临时文件
            String fileName = "resumes_" + resumesId + "_" + System.currentTimeMillis() + ".html";
            File tempFile = new File(documentGeneratorConfig.getTempDir(), fileName);
            tempFile.deleteOnExit(); // 程序退出时删除临时文件

            PrintWriter writer = new PrintWriter(tempFile, "UTF-8");
            // 12. 渲染并写入文件
            String renderedHtml = templateEngine.process("resumes/preview", context);
            writer.write(renderedHtml);
            writer.flush();

            // 可选：记录生成日志
            log.info("简历HTML已生成，文件路径: {}, 简历ID: {}", tempFile.getAbsolutePath(), resumesId);

            return tempFile;

        } catch (IOException | TemplateProcessingException e) {
            log.error("模板处理失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("简历生成失败，模板处理错误", e);
        }
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return documentType == DocumentType.HTML;
    }
}
