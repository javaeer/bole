package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.utils.BeanUtils;
import cn.net.yunlou.bole.common.utils.JsonUtils;
import cn.net.yunlou.bole.config.DocumentGeneratorConfig;
import cn.net.yunlou.bole.handler.IDocumentGeneratorStrategy;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.service.ResumesService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public DocumentType getType() {
        return DocumentType.PDF;
    }

    @Override
    public File generate(Long resumesId) {

        //获取数据
        Resumes resumes = resumesService.getById(resumesId);

        Map<String, Object> map = BeanUtils.toMap(resumes);

        log.info(JsonUtils.toJson(map));

        //  渲染HTML
        Context context = new Context();
        context.setVariables(map);


        String processed = templateEngine.process("resumes/resumes", context);

        // 2. 创建PDF渲染器
        ITextRenderer renderer = new ITextRenderer();

        // 3. 设置中文字体
        setupChineseFonts(renderer);

        // 4. 设置文档
        renderer.setDocumentFromString(processed);
        renderer.layout();

        // 5. 生成PDF文件
        String fileName = "resumes_" + resumesId + "_" + System.currentTimeMillis() + ".pdf";
        File outputFile = new File(documentGeneratorConfig.getTempDir(), fileName);

        try (OutputStream os = new FileOutputStream(outputFile)) {
            renderer.createPDF(os);
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }

        log.info("PDF生成成功: {}", outputFile.getAbsolutePath());

        return null;

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

/*    *//**
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
