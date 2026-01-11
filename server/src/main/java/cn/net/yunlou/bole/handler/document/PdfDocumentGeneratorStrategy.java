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
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * FileName: PdfDocumentGeneratorStrategy
 * Description:
 * Created By laughtiger
 * Created At 2026/1/9 22:22
 * Modified By
 * Modified At
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PdfDocumentGeneratorStrategy implements IDocumentGeneratorStrategy {

    private final TemplateEngine templateEngine;

    private final ResumesService resumesService;

    private final DocumentGeneratorConfig documentGeneratorConfig;

    private final ResumesLayoutCalculator resumesLayoutCalculator;

    private final ResumesStyleCalculator resumesStyleCalculator;

    @Override
    public DocumentType getType() {
        return DocumentType.PDF;
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

            String processed = templateEngine.process("resumes/preview", context);

            // 11. 创建PDF渲染器
            ITextRenderer renderer = new ITextRenderer();

            // 12. 设置中文字体
            setupChineseFonts(renderer);

            // 13. 设置文档
            renderer.setDocumentFromString(processed);
            renderer.layout();

            // 14. 生成PDF文件
            String fileName = "resumes_" + resumesId + "_" + System.currentTimeMillis() + ".pdf";
            File outputFile = new File(documentGeneratorConfig.getTempDir(), fileName);
            outputFile.deleteOnExit();

            OutputStream os = new FileOutputStream(outputFile);

            renderer.createPDF(os);

            log.info("简历PDF已生成，文件路径: {}, 简历ID: {}", outputFile.getAbsolutePath(), resumesId);
            return outputFile;

        } catch (DocumentException | IOException e) {
            log.error("PDF生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("简历PDF生成失败", e);
        }


    }

    /**
     * 设置中文字体
     */
    private void setupChineseFonts(ITextRenderer renderer) {
        try {
            // 加载字体文件
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] fontResources = resolver.getResources(documentGeneratorConfig.getFontDir() + "*.ttf");

            for (Resource fontResource : fontResources) {
                String fontPath = fontResource.getURI().toString();
                renderer.getFontResolver().addFont(fontPath,
                        BaseFont.IDENTITY_H,
                        BaseFont.NOT_EMBEDDED);
            }

            // 如果没有找到字体文件，使用默认字体
            if (fontResources.length == 0) {
                log.warn("未找到字体文件，使用默认字体");
                renderer.getFontResolver().addFont(
                        "classpath:/fonts/simsun.ttc",
                        BaseFont.IDENTITY_H,
                        BaseFont.NOT_EMBEDDED
                );
            }
        } catch (Exception e) {
            log.error("设置字体失败", e);
        }
    }

    /*    */

    /**
     * 生成PDF字节数组
     *//*
    public byte[] generatePdfBytes(Long resumeId, GeneratorConfig config) throws Exception {
        File pdfFile = generate(resumeId);
        return Files.readAllBytes(pdfFile.toPath());
    }*/
    @Override
    public boolean supports(DocumentType documentType) {
        return documentType == DocumentType.PDF;
    }
}
