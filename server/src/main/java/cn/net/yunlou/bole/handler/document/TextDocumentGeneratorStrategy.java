package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.service.ResumesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 纯文本文档生成策略
 */
@Slf4j
@Component
public class TextDocumentGeneratorStrategy extends AbstractDocumentGeneratorStrategy {

    private static final String CACHE_NAME = "resumeTextCache";
    private static final int MAX_LINE_LENGTH = 80;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    // 文本分隔符
    private static final String SECTION_SEPARATOR = "=".repeat(MAX_LINE_LENGTH);
    private static final String SUB_SECTION_SEPARATOR = "-".repeat(MAX_LINE_LENGTH);
    private static final String ITEM_SEPARATOR = "*".repeat(30);

    public TextDocumentGeneratorStrategy(DocumentDirectoryManager directoryManager,
                                         ResumesService resumesService) {
        super(directoryManager,resumesService);
        log.info("纯文本文档生成策略初始化完成");
    }

    @Override
    public DocumentType getType() {
        return DocumentType.TEXT;
    }

    @Override
    protected String getFileExtension() {
        return "txt";
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#resumesId + '_' + #version",
            unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
        // Text 文档通常不需要设备类型，但保持接口一致性
        if (!StringUtils.hasText(device)) {
            device = "desktop";
        }
        if (!StringUtils.hasText(version)) {
            version = "v1";
        }

        log.debug("开始生成纯文本简历，简历ID: {}, 版本: {}", resumesId, version);

        long startTime = System.nanoTime();
        File tempFile = null;

        try {
            // 1. 获取简历数据
            Resumes resumes = getResumesWithValidation(resumesId);

            // 2. 生成文本内容
            String textContent = generateTextContent(resumes);

            // 3. 使用基类方法创建输出文件
            File outputFile = createOutputFile(resumesId, getType(), device, version);

            // 4. 保存文本到文件
            saveTextToFile(textContent, outputFile);

            // 5. 验证文件
            if (!validateOutputFile(outputFile)) {
                throw new IOException("生成的文本文件验证失败");
            }

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            log.info("纯文本简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节",
                    resumesId, duration, outputFile.length());

            return outputFile;

        } catch (Exception e) {
            log.error("纯文本简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("纯文本简历生成失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFile(tempFile);
        }
    }

    /**
     * 生成文本内容
     */
    private String generateTextContent(Resumes resumes) {
        StringBuilder content = new StringBuilder();

        // 1. 标题
        content.append(centerText("个人简历")).append(LINE_SEPARATOR);
        content.append(LINE_SEPARATOR);

        // 2. . 简历组件
        List<ResumesTemplateComponent> components = resumes.getComponents();
        if (components != null && !components.isEmpty()) {
            for (ResumesTemplateComponent component : components) {
                content.append(SUB_SECTION_SEPARATOR).append(LINE_SEPARATOR);
                content.append(centerText(component.getName())).append(LINE_SEPARATOR);
                content.append(SUB_SECTION_SEPARATOR).append(LINE_SEPARATOR);

                // 根据组件类型生成不同的内容
                String componentText = formatComponent(component);
                content.append(componentText).append(LINE_SEPARATOR);
            }
        }

        // 3. 页脚
        content.append(LINE_SEPARATOR).append(ITEM_SEPARATOR).append(LINE_SEPARATOR);
        content.append(centerText("生成时间: " + LocalDateTime.now().format(DATE_FORMATTER)))
                .append(LINE_SEPARATOR);
        content.append(centerText("简历编号: " + resumes.getId())).append(LINE_SEPARATOR);
        content.append(ITEM_SEPARATOR).append(LINE_SEPARATOR);

        return content.toString();
    }



    /**
     * 格式化组件内容
     */
    private String formatComponent(ResumesTemplateComponent component) {
        StringBuilder builder = new StringBuilder();

        if (component.getProps() == null) {
            return "暂无内容";
        }

        // 根据组件类型格式化
        switch (component.getKey()) {
            case "userBasicInfo":
                formatUserBasicInfo(component,builder);
            case "education":
                formatEducation(component, builder);
                break;
            case "work_experience":
                formatWorkExperience(component, builder);
                break;
            case "skills":
                formatSkills(component, builder);
                break;
            case "projects":
                formatProjects(component, builder);
                break;
            case "self_evaluation":
                formatSelfEvaluation(component, builder);
                break;
            default:
                formatDefault(component, builder);
        }

        return builder.toString();
    }

    /**
     * 提取基本信息
     */
    private void formatUserBasicInfo(ResumesTemplateComponent component, StringBuilder builder) {

    }

    /**
     * 格式化教育背景
     */
    private void formatEducation(ResumesTemplateComponent component, StringBuilder builder) {
        List<Map<String, Object>> educations = (List<Map<String, Object>>)
                component.getProps().get("educations");

        if (educations == null || educations.isEmpty()) {
            builder.append("暂无教育背景信息").append(LINE_SEPARATOR);
            return;
        }

        for (int i = 0; i < educations.size(); i++) {
            Map<String, Object> edu = educations.get(i);
            builder.append(i + 1).append(". ").append(edu.get("school")).append(LINE_SEPARATOR);
            builder.append("   专业: ").append(edu.get("major")).append(LINE_SEPARATOR);
            builder.append("   学历: ").append(edu.get("degree")).append(LINE_SEPARATOR);
            builder.append("   时间: ").append(edu.get("startDate"))
                    .append(" - ").append(edu.get("endDate")).append(LINE_SEPARATOR);

            if (edu.get("description") != null) {
                String description = edu.get("description").toString();
                builder.append("   描述: ").append(wrapText(description, 6)).append(LINE_SEPARATOR);
            }

            if (i < educations.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /**
     * 格式化工作经历
     */
    private void formatWorkExperience(ResumesTemplateComponent component, StringBuilder builder) {
        List<Map<String, Object>> experiences = (List<Map<String, Object>>)
                component.getProps().get("experiences");

        if (experiences == null || experiences.isEmpty()) {
            builder.append("暂无工作经历信息").append(LINE_SEPARATOR);
            return;
        }

        for (int i = 0; i < experiences.size(); i++) {
            Map<String, Object> exp = experiences.get(i);
            builder.append(i + 1).append(". ").append(exp.get("company")).append(LINE_SEPARATOR);
            builder.append("   职位: ").append(exp.get("position")).append(LINE_SEPARATOR);
            builder.append("   时间: ").append(exp.get("startDate"))
                    .append(" - ").append(exp.get("endDate")).append(LINE_SEPARATOR);

            if (exp.get("department") != null) {
                builder.append("   部门: ").append(exp.get("department")).append(LINE_SEPARATOR);
            }

            if (exp.get("description") != null) {
                String description = exp.get("description").toString();
                builder.append("   描述: ").append(LINE_SEPARATOR)
                        .append(wrapText(description, 6)).append(LINE_SEPARATOR);
            }

            if (exp.get("achievements") != null) {
                List<String> achievements = (List<String>) exp.get("achievements");
                if (!achievements.isEmpty()) {
                    builder.append("   业绩: ").append(LINE_SEPARATOR);
                    for (String achievement : achievements) {
                        builder.append("     - ").append(achievement).append(LINE_SEPARATOR);
                    }
                }
            }

            if (i < experiences.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /**
     * 格式化技能
     */
    private void formatSkills(ResumesTemplateComponent component, StringBuilder builder) {
        List<Map<String, Object>> skills = (List<Map<String, Object>>)
                component.getProps().get("skills");

        if (skills == null || skills.isEmpty()) {
            builder.append("暂无技能信息").append(LINE_SEPARATOR);
            return;
        }

        for (int i = 0; i < skills.size(); i++) {
            Map<String, Object> skill = skills.get(i);
            builder.append("• ").append(skill.get("name")).append(": ");

            // 技能等级显示
            if (skill.get("level") != null) {
                int level = Integer.parseInt(skill.get("level").toString());
                builder.append("[");
                for (int j = 0; j < 5; j++) {
                    builder.append(j < level ? "★" : "☆");
                }
                builder.append("]");
            }

            if (skill.get("description") != null) {
                builder.append(" - ").append(skill.get("description"));
            }

            builder.append(LINE_SEPARATOR);
        }
    }

    /**
     * 格式化项目经验
     */
    private void formatProjects(ResumesTemplateComponent component, StringBuilder builder) {
        List<Map<String, Object>> projects = (List<Map<String, Object>>)
                component.getProps().get("projects");

        if (projects == null || projects.isEmpty()) {
            builder.append("暂无项目经验信息").append(LINE_SEPARATOR);
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            Map<String, Object> project = projects.get(i);
            builder.append(i + 1).append(". ").append(project.get("name")).append(LINE_SEPARATOR);
            builder.append("   角色: ").append(project.get("role")).append(LINE_SEPARATOR);
            builder.append("   时间: ").append(project.get("startDate"))
                    .append(" - ").append(project.get("endDate")).append(LINE_SEPARATOR);

            if (project.get("description") != null) {
                String description = project.get("description").toString();
                builder.append("   描述: ").append(LINE_SEPARATOR)
                        .append(wrapText(description, 6)).append(LINE_SEPARATOR);
            }

            if (project.get("technologies") != null) {
                List<String> technologies = (List<String>) project.get("technologies");
                if (!technologies.isEmpty()) {
                    builder.append("   技术栈: ").append(String.join(", ", technologies))
                            .append(LINE_SEPARATOR);
                }
            }

            if (project.get("responsibilities") != null) {
                List<String> responsibilities = (List<String>) project.get("responsibilities");
                if (!responsibilities.isEmpty()) {
                    builder.append("   职责: ").append(LINE_SEPARATOR);
                    for (String responsibility : responsibilities) {
                        builder.append("     - ").append(responsibility).append(LINE_SEPARATOR);
                    }
                }
            }

            if (i < projects.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /**
     * 格式化自我评价
     */
    private void formatSelfEvaluation(ResumesTemplateComponent component, StringBuilder builder) {
        String content = (String) component.getProps().get("content");
        if (StringUtils.hasText(content)) {
            builder.append(wrapText(content, 0)).append(LINE_SEPARATOR);
        } else {
            builder.append("暂无自我评价").append(LINE_SEPARATOR);
        }
    }

    /**
     * 格式化默认组件
     */
    private void formatDefault(ResumesTemplateComponent component, StringBuilder builder) {
        builder.append("暂无内容").append(LINE_SEPARATOR);
    }

    /**
     * 文本居中
     */
    private String centerText(String text) {
        if (text == null) {
            return "";
        }

        int textLength = getTextWidth(text);
        int totalSpaces = MAX_LINE_LENGTH - textLength;
        if (totalSpaces <= 0) {
            return text;
        }

        int leftSpaces = totalSpaces / 2;
        int rightSpaces = totalSpaces - leftSpaces;

        return " ".repeat(leftSpaces) + text + " ".repeat(rightSpaces);
    }

    /**
     * 计算文本宽度（考虑中英文字符）
     */
    private int getTextWidth(String text) {
        if (text == null) {
            return 0;
        }

        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // 中文字符宽度为2，英文字符宽度为1
            width += (c >= 0x4E00 && c <= 0x9FA5) ? 2 : 1;
        }
        return width;
    }

    /**
     * 文本换行
     */
    private String wrapText(String text, int indentSpaces) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSpaces);
        int maxWidth = MAX_LINE_LENGTH - indentSpaces;

        String[] paragraphs = text.split("\n");
        for (int p = 0; p < paragraphs.length; p++) {
            String paragraph = paragraphs[p].trim();
            if (paragraph.isEmpty()) {
                continue;
            }

            StringBuilder line = new StringBuilder();
            int lineWidth = 0;

            String[] words = paragraph.split("\\s+");
            for (String word : words) {
                int wordWidth = getTextWidth(word);

                if (lineWidth + wordWidth + (line.length() > 0 ? 1 : 0) > maxWidth) {
                    result.append(indent).append(line).append(LINE_SEPARATOR);
                    line = new StringBuilder(word);
                    lineWidth = wordWidth;
                } else {
                    if (line.length() > 0) {
                        line.append(" ");
                        lineWidth++;
                    }
                    line.append(word);
                    lineWidth += wordWidth;
                }
            }

            if (line.length() > 0) {
                result.append(indent).append(line).append(LINE_SEPARATOR);
            }

            if (p < paragraphs.length - 1) {
                result.append(LINE_SEPARATOR);
            }
        }

        return result.toString();
    }

    /**
     * 保存文本到文件
     */
    private void saveTextToFile(String textContent, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(textContent);
            writer.flush();
        }

        log.debug("纯文本文件已保存: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /**
     * 异步生成纯文本简历
     */
    public CompletableFuture<File> generateAsync(Long resumesId, String version) {
        return CompletableFuture.supplyAsync(() -> generate(resumesId, "text", version));
    }

    /**
     * 获取纯文本内容
     */
    public String getTextContent(Long resumesId) throws IOException {
        File textFile = generate(resumesId, "text", null);
        return Files.readString(textFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * 验证文本文件
     */
    public boolean validateTextFile(File textFile) {
        if (!validateOutputFile(textFile)) {
            return false;
        }

        try {
            String content = Files.readString(textFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
            // 简单验证：文件非空且包含一些文本
            return !content.trim().isEmpty() && content.length() > 10;
        } catch (IOException e) {
            log.error("读取文本文件失败", e);
            return false;
        }
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return DocumentType.TEXT == documentType;
    }
}