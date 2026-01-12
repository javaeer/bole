package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.utils.StyleUtils;
import cn.net.yunlou.bole.handler.resumes.ResumesLayoutCalculator;
import cn.net.yunlou.bole.handler.resumes.ResumesStyleCalculator;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import cn.net.yunlou.bole.service.ResumesService;
import com.google.common.collect.Maps;
import com.lowagie.text.DocumentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * PDF文档生成策略
 */
@Slf4j
@Component
public class PdfDocumentGeneratorStrategy extends AbstractDocumentGeneratorStrategy {

    private final TemplateEngine templateEngine;
    private final ResumesLayoutCalculator resumesLayoutCalculator;
    private final ResumesStyleCalculator resumesStyleCalculator;

    // 缓存配置
    private static final String CACHE_NAME = "resumePdfCache";
    private static final long CACHE_TTL_HOURS = 24;

    // PDF配置
    private static final float PDF_DPI = 72f;

    public PdfDocumentGeneratorStrategy(DocumentDirectoryManager directoryManager,
                                        TemplateEngine templateEngine,
                                        ResumesService resumesService,
                                        ResumesLayoutCalculator resumesLayoutCalculator,
                                        ResumesStyleCalculator resumesStyleCalculator) {
        super(directoryManager,resumesService);
        this.templateEngine = templateEngine;
        this.resumesLayoutCalculator = resumesLayoutCalculator;
        this.resumesStyleCalculator = resumesStyleCalculator;

        log.info("PDF文档生成策略初始化完成");
    }

    @Override
    public DocumentType getType() {
        return DocumentType.PDF;
    }

    @Override
    protected String getFileExtension() {
        return "pdf";
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#resumesId + '_' + #device + '_' + #version",
            unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
        // 使用抽象基类的方法简化参数处理
        if (!StringUtils.hasText(device)) {
            device = "desktop";
        }

        if (!StringUtils.hasText(version)) {
            version = "v1";
        }

        log.debug("开始生成PDF简历，简历ID: {}, 设备类型: {}, 版本: {}", resumesId, device, version);

        long startTime = System.nanoTime();
        File htmlTempFile = null;
        File pdfTempFile = null;

        try {
            // 1. 获取简历数据
            Resumes resumes = getResumesWithValidation(resumesId);

            // 2. 准备模板数据并渲染HTML
            String htmlContent = renderHtml(resumes, device);

            // 3. 使用基类方法创建临时HTML文件
            htmlTempFile = createTempFile("pdf_html_", ".html");
            saveHtmlToFile(htmlContent, htmlTempFile);

            // 4. 将HTML转换为PDF到临时文件
            pdfTempFile = createTempFile("pdf_temp_", ".pdf");
            convertHtmlToPdf(htmlTempFile, pdfTempFile);

            // 5. 使用基类方法创建最终输出文件
            File outputFile = createOutputFile(resumesId, getType(), device, version);

            // 6. 复制临时PDF到输出文件
            Files.copy(pdfTempFile.toPath(), outputFile.toPath());

            // 7. 验证文件
            if (!validateOutputFile(outputFile)) {
                throw new IOException("生成的PDF文件验证失败");
            }

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            log.info("PDF简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节",
                    resumesId, duration, outputFile.length());

            return outputFile;

        } catch (Exception e) {
            log.error("PDF简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("PDF简历生成失败: " + e.getMessage(), e);
        } finally {
        //    // 使用基类方法清理临时文件
            cleanupTempFile(htmlTempFile);
            cleanupTempFile(pdfTempFile);
        }
    }

    @Override
    public File generate(Long resumesId) {
        // 使用基类的默认实现
        return generate(resumesId, "desktop", "v1");
    }


    /**
     * 渲染HTML
     */
    private String renderHtml(Resumes resumes, String device) {
        List<ResumesTemplateComponent> components = resumes.getComponents();
        ResumesTemplateStyle globalStyle = resumes.getGlobalStyle();
        ResumesTemplateLayout globalLayout = resumes.getGlobalLayout();

        // 计算布局
        Map<String, Object> layoutData = resumesLayoutCalculator.calculateLayout(
                components, globalLayout, globalStyle);

        // 计算容器样式
        String layoutType = (String) layoutData.get("layoutType");
        Map<String, String> containerStyle = resumesStyleCalculator.getContainerStyle(
                globalStyle, layoutType);

        // 获取响应式样式
        Map<String, String> responsiveStyles = resumesLayoutCalculator.getResponsiveStyles();

        // 创建模板变量
        Map<String, Object> variables = Maps.newHashMap();

        // 添加主要数据
        variables.put("resumes", resumes);
        variables.put("device", device);
        variables.put("components", components);
        variables.put("generationTime", LocalDateTime.now());
        variables.put("forPdf", true);

        // 添加布局相关数据
        variables.put("layoutData", layoutData);
        variables.put("globalStyle", globalStyle);
        variables.put("globalLayout", globalLayout);
        variables.put("responsiveStyles", responsiveStyles);
        variables.put("responsiveStylesCss", StyleUtils.toCss(responsiveStyles));
        variables.put("containerStyle", containerStyle);
        variables.put("containerStyleCss", StyleUtils.toCss(containerStyle));

        // 添加PDF特定样式
        Map<String, String> pdfStyles = Maps.newHashMap();
        pdfStyles.put("page-break-inside", "avoid");
        pdfStyles.put("page-break-before", "auto");
        pdfStyles.put("page-break-after", "auto");
        variables.put("pdfStyles", pdfStyles);
        variables.put("pdfStylesCss", StyleUtils.toCss(pdfStyles));

        // 添加组件样式
        /*if (components != null) {
            components.forEach(component -> {
                Map<String, String> componentStyle = resumesStyleCalculator.getComponentStyle(
                        component, globalStyle);
                componentStyle.put("page-break-inside", "avoid");
                component.setComputedStyle(StyleUtils.toCss(componentStyle));
            });
        }*/

        Context context = new Context();
        context.setVariables(variables);

        String templateName = "resumes/preview-pdf";
        try {
            return templateEngine.process(templateName, context);
        } catch (TemplateProcessingException e) {
            log.error("PDF模板处理失败，设备类型: {}, 模板: {}", device, templateName, e);
            throw new RuntimeException("PDF模板处理失败: " + e.getMessage(), e);
        }
    }

    /**
     * 保存HTML到文件
     */
    private void saveHtmlToFile(String htmlContent, File file) throws IOException {
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            os.write(htmlContent.getBytes("UTF-8"));
            os.flush();
        }

        log.debug("保存HTML文件: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /**
     * 将HTML转换为PDF
     */
    private void convertHtmlToPdf(File htmlFile, File pdfFile) throws IOException, DocumentException {
        log.debug("转换HTML到PDF，HTML文件: {}, PDF文件: {}",
                htmlFile.getAbsolutePath(), pdfFile.getAbsolutePath());

        ITextRenderer renderer = createITextRenderer();

        try {
            renderer.setDocument(htmlFile);
            renderer.layout();

            try (OutputStream os = new FileOutputStream(pdfFile)) {
                renderer.createPDF(os);
                os.flush();
            }

            log.debug("PDF转换完成，文件大小: {}字节", pdfFile.length());
        } finally {
            renderer.finishPDF();
        }
    }

    /**
     * 创建ITextRenderer实例
     */
    private ITextRenderer createITextRenderer() {
        ITextRenderer renderer = new ITextRenderer();
        setupChineseFonts(renderer);
        return renderer;
    }

    /**
     * 设置中文字体
     */
    private void setupChineseFonts(ITextRenderer renderer) {
        try {
            // 使用基类方法检查字体文件
            if (checkFontExists("simsun.ttf")) {
                String fontPath = getFontFile("simsun.ttf").getAbsolutePath();
                renderer.getFontResolver().addFont(
                        fontPath,
                        com.lowagie.text.pdf.BaseFont.IDENTITY_H,
                        com.lowagie.text.pdf.BaseFont.EMBEDDED
                );
                log.debug("加载中文字体: {}", fontPath);
            } else if (checkFontExists("simsun.ttc")) {
                String fontPath = getFontFile("simsun.ttc").getAbsolutePath();
                renderer.getFontResolver().addFont(
                        fontPath,
                        com.lowagie.text.pdf.BaseFont.IDENTITY_H,
                        com.lowagie.text.pdf.BaseFont.EMBEDDED
                );
                log.debug("加载中文字体: {}", fontPath);
            } else {
                log.warn("未找到中文字体文件，PDF中的中文可能无法正确显示");
            }
        } catch (Exception e) {
            log.error("设置中文字体失败", e);
            throw new RuntimeException("设置中文字体失败: " + e.getMessage(), e);
        }
    }

    /**
     * 异步生成PDF简历
     */
    public CompletableFuture<File> generateAsync(Long resumesId, String device, String version) {
        return CompletableFuture.supplyAsync(() -> generate(resumesId, device, version));
    }

    /**
     * 从HTML文件生成PDF
     */
    public File generateFromHtml(File htmlFile, Long resumesId, String device, String version) throws IOException {
        log.debug("从HTML文件生成PDF，HTML文件: {}", htmlFile.getAbsolutePath());

        if (!htmlFile.exists() || !htmlFile.isFile()) {
            throw new IOException("HTML文件不存在或无效");
        }

        long startTime = System.nanoTime();
        File pdfTempFile = null;

        try {
            // 1. 使用基类方法创建临时PDF文件
            pdfTempFile = createTempFile("pdf_temp_", ".pdf");

            // 2. 将HTML转换为PDF
            convertHtmlToPdf(htmlFile, pdfTempFile);

            // 3. 使用基类方法创建输出文件
            File outputFile = createOutputFile(resumesId, getType(), device, version);

            // 4. 复制临时PDF到输出文件
            Files.copy(pdfTempFile.toPath(), outputFile.toPath());

            // 5. 验证文件
            if (!validateOutputFile(outputFile)) {
                throw new IOException("生成的PDF文件验证失败");
            }

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            log.info("从HTML生成PDF完成，简历ID: {}, 耗时: {}ms", resumesId, duration);
            return outputFile;

        } catch (DocumentException e) {
            throw new IOException("HTML转PDF失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFile(pdfTempFile);
        }
    }

    /**
     * 验证PDF文件
     */
    public boolean validatePdfFile(File pdfFile) {
        // 首先使用基类的验证方法
        if (!validateOutputFile(pdfFile)) {
            return false;
        }

        try {
            // 检查PDF文件头
            byte[] header = new byte[5];
            try (java.io.FileInputStream fis = new java.io.FileInputStream(pdfFile)) {
                fis.read(header);
            }

            String headerStr = new String(header, "US-ASCII");
            boolean isValidPdf = headerStr.startsWith("%PDF-");

            if (!isValidPdf) {
                log.warn("文件不是有效的PDF格式: {}", pdfFile.getAbsolutePath());
            }

            return isValidPdf;

        } catch (IOException e) {
            log.error("验证PDF文件失败", e);
            return false;
        }
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return DocumentType.PDF == documentType;
    }
}