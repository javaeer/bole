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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;

/** HTML文档生成策略 */
@Slf4j
@Component
public class HtmlDocumentGeneratorStrategy extends AbstractDocumentGeneratorStrategy {

    // 缓存配置
    private static final String CACHE_NAME = "resumeHtmlCache";
    private static final long CACHE_TTL_MINUTES = 30;
    private final TemplateEngine templateEngine;
    private final ResumesLayoutCalculator resumesLayoutCalculator;
    private final ResumesStyleCalculator resumesStyleCalculator;

    public HtmlDocumentGeneratorStrategy(
            DocumentDirectoryManager directoryManager,
            TemplateEngine templateEngine,
            ResumesService resumesService,
            ResumesLayoutCalculator resumesLayoutCalculator,
            ResumesStyleCalculator resumesStyleCalculator) {
        super(directoryManager, resumesService);
        this.templateEngine = templateEngine;
        this.resumesLayoutCalculator = resumesLayoutCalculator;
        this.resumesStyleCalculator = resumesStyleCalculator;

        log.info("HTML文档生成策略初始化完成");
    }

    @Override
    public DocumentType getType() {
        return DocumentType.HTML;
    }

    @Override
    protected String getFileExtension() {
        return "html";
    }

    @Override
    @Cacheable(
            value = CACHE_NAME,
            key = "#resumesId + '_' + #device + '_' + #version",
            unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
        if (!StringUtils.hasText(device)) {
            device = "desktop";
        }

        if (!StringUtils.hasText(version)) {
            version = "v1";
        }

        log.debug("开始生成HTML简历，简历ID: {}, 设备类型: {}, 版本: {}", resumesId, device, version);

        long startTime = System.nanoTime();
        File tempFile = null;

        try {
            // 1. 获取简历数据
            Resumes resumes = getResumesWithValidation(resumesId);

            // 2. 准备模板数据
            Map<String, Object> templateData = prepareTemplateData(resumes, device);

            // 3. 生成HTML内容
            String htmlContent = renderHtml(templateData, device);

            // 4. 创建输出文件
            File outputFile = createOutputFile(resumesId, getType(), device, version);

            // 5. 保存HTML到文件
            saveHtmlToFile(htmlContent, outputFile);

            // 6. 验证生成的文件
            if (!validateOutputFile(outputFile)) {
                throw new IOException("生成的HTML文件验证失败");
            }

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            log.info(
                    "HTML简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节, 文件路径: {}",
                    resumesId,
                    duration,
                    outputFile.length(),
                    outputFile.getAbsolutePath());

            return outputFile;

        } catch (Exception e) {
            log.error("HTML简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("HTML简历生成失败: " + e.getMessage(), e);
        } finally {
            // 清理临时文件
            cleanupTempFile(tempFile);
        }
    }

    /** 异步生成HTML简历 */
    public CompletableFuture<File> generateAsync(Long resumesId, String device, String version) {
        return CompletableFuture.supplyAsync(() -> generate(resumesId, device, version));
    }

    /** 准备模板数据 */
    private Map<String, Object> prepareTemplateData(Resumes resumes, String device) {
        List<ResumesTemplateComponent> components = resumes.getComponents();
        ResumesTemplateStyle globalStyle = resumes.getGlobalStyle();
        ResumesTemplateLayout globalLayout = resumes.getGlobalLayout();

        // 计算布局
        Map<String, Object> layoutData =
                resumesLayoutCalculator.calculateLayout(components, globalLayout, globalStyle);

        // 计算容器样式
        String layoutType = (String) layoutData.get("layoutType");
        Map<String, String> containerStyle =
                resumesStyleCalculator.getContainerStyle(globalStyle, layoutType);

        // 获取响应式样式
        Map<String, String> responsiveStyles = resumesLayoutCalculator.getResponsiveStyles();

        // 创建模板变量
        Map<String, Object> variables = Maps.newHashMap();

        // 添加主要数据
        variables.put("resumes", resumes);
        variables.put("device", device);
        variables.put("components", components);
        variables.put("generationTime", System.currentTimeMillis());

        // 添加布局相关数据
        variables.put("layoutData", layoutData);
        variables.put("globalStyle", globalStyle);
        variables.put("globalLayout", globalLayout);
        variables.put("responsiveStyles", responsiveStyles);
        variables.put("responsiveStylesCss", StyleUtils.toCss(responsiveStyles));
        variables.put("containerStyle", containerStyle);
        variables.put("containerStyleCss", StyleUtils.toCss(containerStyle));

        //// 添加组件样式
        // if (components != null) {
        //    components.forEach(component -> {
        //        Map<String, String> componentStyle = resumesStyleCalculator.getComponentStyle(
        //                component, globalStyle);
        //        component.setComputedStyle(StyleUtils.toCss(componentStyle));
        //    });
        // }

        return variables;
    }

    /** 渲染HTML */
    private String renderHtml(Map<String, Object> templateData, String device) {
        Context context = new Context();
        context.setVariables(templateData);

        // 根据设备类型选择不同的模板
        String templateName = "resumes/preview";
        if ("mobile".equalsIgnoreCase(device)) {
            templateName = "resumes/preview-mobile";
        } else if ("tablet".equalsIgnoreCase(device)) {
            templateName = "resumes/preview-tablet";
        }

        try {
            return templateEngine.process(templateName, context);
        } catch (TemplateProcessingException e) {
            log.error("模板处理失败，设备类型: {}, 模板: {}", device, templateName, e);
            throw new RuntimeException("模板处理失败: " + e.getMessage(), e);
        }
    }

    /** 保存HTML到文件 */
    private void saveHtmlToFile(String htmlContent, File file) throws IOException {
        // 使用try-with-resources确保资源关闭
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(htmlContent);
            writer.flush();
        }

        // 设置文件权限
        file.setReadable(true, false);
        file.setWritable(true, true);

        log.debug("HTML文件已保存: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /** 获取生成的HTML文件内容 */
    public String getHtmlContent(Long resumesId) throws IOException {
        File htmlFile = generate(resumesId);
        return Files.readString(htmlFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /** 获取生成的HTML文件内容（指定设备） */
    public String getHtmlContent(Long resumesId, String device) throws IOException {
        File htmlFile = generate(resumesId, device, null);
        return Files.readString(htmlFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /** 验证生成的HTML文件 */
    public boolean validateHtmlFile(File htmlFile) {
        if (!validateOutputFile(htmlFile)) {
            return false;
        }

        try {
            String content =
                    Files.readString(htmlFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
            // 基本HTML结构验证
            boolean hasHtmlTag = content.contains("<html") && content.contains("</html>");
            boolean hasBodyTag = content.contains("<body") && content.contains("</body>");

            if (!hasHtmlTag || !hasBodyTag) {
                log.warn("HTML文件结构不完整: {}", htmlFile.getAbsolutePath());
                return false;
            }

            return true;
        } catch (IOException e) {
            log.error("读取HTML文件失败", e);
            return false;
        }
    }

    /** 清理指定简历的所有HTML缓存 */
    public void clearCache(Long resumesId) {
        // 这里可以调用缓存管理器清理相关缓存
        log.info("清理简历HTML缓存，简历ID: {}", resumesId);
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return DocumentType.HTML == documentType;
    }
}
