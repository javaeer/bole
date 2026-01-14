package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.handler.resumes.ResumeDataParser;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.service.ResumesService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** 纯文本文档生成策略 */
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

    private final ResumeDataParser resumeDataParser;

    public TextDocumentGeneratorStrategy(
            DocumentDirectoryManager directoryManager,
            ResumesService resumesService,
            ResumeDataParser resumeDataParser) {
        super(directoryManager, resumesService);
        this.resumeDataParser = resumeDataParser;
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
    @Cacheable(value = CACHE_NAME, key = "#resumesId + '_' + #version", unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
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

            log.info(
                    "纯文本简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节",
                    resumesId,
                    duration,
                    outputFile.length());

            return outputFile;

        } catch (Exception e) {
            log.error("纯文本简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("纯文本简历生成失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFile(tempFile);
        }
    }

    /** 生成文本内容 */
    private String generateTextContent(Resumes resumes) {
        StringBuilder content = new StringBuilder();

        // 1. 标题
        content.append(centerText("个人简历")).append(LINE_SEPARATOR);
        content.append(LINE_SEPARATOR);

        // 2. 简历组件
        List<ResumesTemplateComponent> components = resumes.getComponents();
        if (components != null && !components.isEmpty()) {
            // 按照globalLayout中的componentOrder排序
            List<String> componentOrder = resumes.getGlobalLayout().getComponentOrder();
            components.sort(
                    (c1, c2) -> {
                        int idx1 = componentOrder.indexOf(String.valueOf(c1.getComponentId()));
                        int idx2 = componentOrder.indexOf(String.valueOf(c2.getComponentId()));
                        return Integer.compare(idx1, idx2);
                    });

            for (ResumesTemplateComponent component : components) {
                // 解析组件数据
                Map<String, Object> componentData = resumeDataParser.parseComponentData(component);

                content.append(SUB_SECTION_SEPARATOR).append(LINE_SEPARATOR);
                String componentName = (String) componentData.get("name");
                content.append(centerText(resumeDataParser.fixChineseEncoding(componentName)))
                        .append(LINE_SEPARATOR);
                content.append(SUB_SECTION_SEPARATOR).append(LINE_SEPARATOR);

                // 根据组件类型生成不同的内容
                String componentText = formatComponent(componentData);
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

    /** 格式化组件内容 */
    private String formatComponent(Map<String, Object> componentData) {
        StringBuilder builder = new StringBuilder();

        String componentKey = ((String) componentData.get("key")).toLowerCase();
        Map<String, Object> props = (Map<String, Object>) componentData.get("props");

        if (props == null || props.isEmpty()) {
            return "暂无内容" + LINE_SEPARATOR;
        }

        // 根据组件类型格式化
        switch (componentKey) {
            case "userbasicinfo":
                formatUserBasicInfo(props, builder);
                break;
            case "educationexperience":
                formatEducationExperience(props, builder);
                break;
            case "workexperience":
                formatWorkExperience(props, builder);
                break;
            case "skills":
                formatSkills(props, builder);
                break;
            case "projectexperience":
                formatProjectExperience(props, builder);
                break;
            case "selfevaluation":
                formatSelfEvaluation(props, builder);
                break;
            case "jobintention":
                formatJobIntention(props, builder);
                break;
            default:
                builder.append("暂无内容").append(LINE_SEPARATOR);
        }

        return builder.toString();
    }

    /** 提取基本信息 */
    @SuppressWarnings("unchecked")
    private void formatUserBasicInfo(Map<String, Object> props, StringBuilder builder) {
        // 检查是否有用户数据对象
        Map<String, Object> userData = null;
        if (props.containsKey("id") && props.get("id") instanceof Integer) {
            // props本身就是用户数据
            userData = props;
        }

        if (userData == null) {
            builder.append("暂无基本信息").append(LINE_SEPARATOR);
            return;
        }

        builder.append("姓名: ")
                .append(fixEncoding(getStringValue(userData, "name", "未填写")))
                .append(LINE_SEPARATOR);
        builder.append("职位: ")
                .append(fixEncoding(getStringValue(userData, "title", "未填写")))
                .append(LINE_SEPARATOR);
        builder.append("邮箱: ")
                .append(getStringValue(userData, "email", "未填写"))
                .append(LINE_SEPARATOR);
        builder.append("电话: ")
                .append(getStringValue(userData, "phone", "未填写"))
                .append(LINE_SEPARATOR);
        builder.append("性别: ")
                .append(getGenderValue(userData.get("gender")))
                .append(LINE_SEPARATOR);
        builder.append("所在地: ")
                .append(fixEncoding(getStringValue(userData, "location", "未填写")))
                .append(LINE_SEPARATOR);

        if (userData.containsKey("workYears")) {
            Object workYears = userData.get("workYears");
            String workYearsStr = workYears != null ? workYears.toString() : "0";
            builder.append("工作年限: ").append(workYearsStr).append("年").append(LINE_SEPARATOR);
        }

        if (userData.containsKey("github")) {
            builder.append("GitHub: ").append(userData.get("github")).append(LINE_SEPARATOR);
        }
        if (userData.containsKey("wechat")) {
            builder.append("微信: ").append(userData.get("wechat")).append(LINE_SEPARATOR);
        }
        if (userData.containsKey("website")) {
            builder.append("个人网站: ").append(userData.get("website")).append(LINE_SEPARATOR);
        }
    }

    /** 格式化教育背景 */
    @SuppressWarnings("unchecked")
    private void formatEducationExperience(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("experiences") || !(props.get("experiences") instanceof List)) {
            builder.append("暂无教育背景信息").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> educations = (List<Map<String, Object>>) props.get("experiences");

        // 按毕业时间倒序排序
        educations.sort(
                (e1, e2) -> {
                    String date1 = getStringValue(e2, "endDate", "0000-00-00");
                    String date2 = getStringValue(e1, "endDate", "0000-00-00");
                    return date2.compareTo(date1);
                });

        for (int i = 0; i < educations.size(); i++) {
            Map<String, Object> edu = educations.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(fixEncoding(getStringValue(edu, "university", "未知学校")))
                    .append(LINE_SEPARATOR);
            builder.append("   专业: ")
                    .append(fixEncoding(getStringValue(edu, "major", "未填写")))
                    .append(LINE_SEPARATOR);
            builder.append("   学历: ")
                    .append(fixEncoding(getStringValue(edu, "degree", "未填写")))
                    .append(LINE_SEPARATOR);

            String startDate = getStringValue(edu, "startDate", "");
            String endDate = getStringValue(edu, "endDate", "");
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                builder.append("   时间: ")
                        .append(resumeDataParser.formatDate(startDate))
                        .append(" - ")
                        .append(resumeDataParser.formatDate(endDate))
                        .append(LINE_SEPARATOR);
            }

            if (edu.containsKey("description")) {
                String description = edu.get("description").toString();
                builder.append("   描述: ")
                        .append(fixEncoding(wrapText(description, 6)))
                        .append(LINE_SEPARATOR);
            }

            if (edu.containsKey("achievements")
                    && edu.get("achievements") instanceof List
                    && !((List<?>) edu.get("achievements")).isEmpty()) {
                List<String> achievements = (List<String>) edu.get("achievements");
                builder.append("   成就: ").append(LINE_SEPARATOR);
                for (String achievement : achievements) {
                    builder.append("     - ")
                            .append(fixEncoding(achievement))
                            .append(LINE_SEPARATOR);
                }
            }

            if (i < educations.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 格式化工作经历 */
    @SuppressWarnings("unchecked")
    private void formatWorkExperience(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("experiences") || !(props.get("experiences") instanceof List)) {
            builder.append("暂无工作经历信息").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> experiences =
                (List<Map<String, Object>>) props.get("experiences");

        // 按开始时间倒序排序
        experiences.sort(
                (e1, e2) -> {
                    String date1 = getStringValue(e2, "startDate", "0000-00-00");
                    String date2 = getStringValue(e1, "startDate", "0000-00-00");
                    return date2.compareTo(date1);
                });

        for (int i = 0; i < experiences.size(); i++) {
            Map<String, Object> exp = experiences.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(fixEncoding(getStringValue(exp, "company", "未知公司")))
                    .append(LINE_SEPARATOR);
            builder.append("   职位: ")
                    .append(fixEncoding(getStringValue(exp, "position", "未填写")))
                    .append(LINE_SEPARATOR);

            String startDate = getStringValue(exp, "startDate", "");
            String endDate = getStringValue(exp, "endDate", "");
            Object isCurrentObj = exp.get("isCurrent");
            Boolean isCurrent =
                    isCurrentObj != null ? Boolean.parseBoolean(isCurrentObj.toString()) : false;

            if (!startDate.isEmpty()) {
                builder.append("   时间: ").append(resumeDataParser.formatDate(startDate));
                if (isCurrent) {
                    builder.append(" - 至今");
                } else if (!endDate.isEmpty()) {
                    builder.append(" - ").append(resumeDataParser.formatDate(endDate));
                }
                builder.append(LINE_SEPARATOR);
            }

            if (exp.containsKey("description")) {
                String description = exp.get("description").toString();
                builder.append("   描述: ")
                        .append(LINE_SEPARATOR)
                        .append(fixEncoding(wrapText(description, 6)))
                        .append(LINE_SEPARATOR);
            }

            if (exp.containsKey("achievements")
                    && exp.get("achievements") instanceof List
                    && !((List<?>) exp.get("achievements")).isEmpty()) {
                List<String> achievements = (List<String>) exp.get("achievements");
                builder.append("   业绩: ").append(LINE_SEPARATOR);
                for (String achievement : achievements) {
                    builder.append("     - ")
                            .append(fixEncoding(achievement))
                            .append(LINE_SEPARATOR);
                }
            }

            if (i < experiences.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 格式化技能 */
    @SuppressWarnings("unchecked")
    private void formatSkills(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("skills") || !(props.get("skills") instanceof List)) {
            builder.append("暂无技能信息").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> skills = (List<Map<String, Object>>) props.get("skills");

        // 按熟练度倒序排序
        skills.sort(
                (s1, s2) -> {
                    Integer p1 =
                            s1.get("proficiencyPercent") instanceof Integer
                                    ? (Integer) s1.get("proficiencyPercent")
                                    : 0;
                    Integer p2 =
                            s2.get("proficiencyPercent") instanceof Integer
                                    ? (Integer) s2.get("proficiencyPercent")
                                    : 0;
                    return Integer.compare(p2, p1);
                });

        for (int i = 0; i < skills.size(); i++) {
            Map<String, Object> skill = skills.get(i);
            builder.append("• ").append(fixEncoding(getStringValue(skill, "name", "未命名")));

            // 技能等级显示
            String level = getStringValue(skill, "level", "");
            Object proficiencyObj = skill.get("proficiencyPercent");
            Integer proficiency =
                    proficiencyObj != null
                            ? (proficiencyObj instanceof Integer
                                    ? (Integer) proficiencyObj
                                    : Integer.parseInt(proficiencyObj.toString()))
                            : null;

            if (!level.isEmpty() && proficiency != null) {
                builder.append(" (")
                        .append(fixEncoding(level))
                        .append(" ")
                        .append(proficiency)
                        .append("%)");
            } else if (!level.isEmpty()) {
                builder.append(" (").append(fixEncoding(level)).append(")");
            } else if (proficiency != null) {
                builder.append(" (").append(proficiency).append("%)");
            }

            if (skill.containsKey("category")) {
                builder.append(" [")
                        .append(fixEncoding(skill.get("category").toString()))
                        .append("]");
            }

            if (skill.containsKey("experienceYears")) {
                Object yearsObj = skill.get("experienceYears");
                if (yearsObj != null) {
                    String yearsStr = yearsObj.toString();
                    builder.append(" ").append(yearsStr).append("年经验");
                }
            }

            builder.append(LINE_SEPARATOR);

            if (skill.containsKey("description")) {
                String description = skill.get("description").toString();
                if (!description.trim().isEmpty()) {
                    builder.append("   ")
                            .append(fixEncoding(wrapText(description, 3)))
                            .append(LINE_SEPARATOR);
                }
            }

            if (skill.containsKey("tags")
                    && skill.get("tags") instanceof List
                    && !((List<?>) skill.get("tags")).isEmpty()) {
                List<String> tags = (List<String>) skill.get("tags");
                if (!tags.isEmpty()) {
                    builder.append("   标签: ");
                    for (int j = 0; j < tags.size(); j++) {
                        if (j > 0) builder.append(", ");
                        builder.append(fixEncoding(tags.get(j)));
                    }
                    builder.append(LINE_SEPARATOR);
                }
            }

            if (i < skills.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 格式化项目经验 */
    @SuppressWarnings("unchecked")
    private void formatProjectExperience(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("experiences") || !(props.get("experiences") instanceof List)) {
            builder.append("暂无项目经验信息").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> projects = (List<Map<String, Object>>) props.get("experiences");

        // 按开始时间倒序排序
        projects.sort(
                (p1, p2) -> {
                    String date1 = getStringValue(p2, "startDate", "0000-00-00");
                    String date2 = getStringValue(p1, "startDate", "0000-00-00");
                    return date2.compareTo(date1);
                });

        for (int i = 0; i < projects.size(); i++) {
            Map<String, Object> project = projects.get(i);
            builder.append(i + 1)
                    .append(". ")
                    .append(fixEncoding(getStringValue(project, "name", "未知项目")))
                    .append(LINE_SEPARATOR);

            String startDate = getStringValue(project, "startDate", "");
            String endDate = getStringValue(project, "endDate", "");
            if (!startDate.isEmpty() && !endDate.isEmpty()) {
                builder.append("   时间: ")
                        .append(resumeDataParser.formatDate(startDate))
                        .append(" - ")
                        .append(resumeDataParser.formatDate(endDate))
                        .append(LINE_SEPARATOR);
            }

            Object statusObj = project.get("status");
            if (statusObj != null) {
                String statusText = "未知";
                try {
                    int status = Integer.parseInt(statusObj.toString());
                    switch (status) {
                        case 0:
                            statusText = "未开始";
                            break;
                        case 1:
                            statusText = "进行中";
                            break;
                        case 2:
                            statusText = "已完成";
                            break;
                    }
                } catch (NumberFormatException e) {
                    statusText = statusObj.toString();
                }
                builder.append("   状态: ").append(statusText).append(LINE_SEPARATOR);
            }

            if (project.containsKey("description")) {
                String description = project.get("description").toString();
                builder.append("   描述: ")
                        .append(LINE_SEPARATOR)
                        .append(fixEncoding(wrapText(description, 6)))
                        .append(LINE_SEPARATOR);
            }

            if (project.containsKey("achievements")
                    && project.get("achievements") instanceof List
                    && !((List<?>) project.get("achievements")).isEmpty()) {
                List<String> achievements = (List<String>) project.get("achievements");
                builder.append("   成就: ").append(LINE_SEPARATOR);
                for (String achievement : achievements) {
                    builder.append("     - ")
                            .append(fixEncoding(achievement))
                            .append(LINE_SEPARATOR);
                }
            }

            if (i < projects.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 格式化自我评价 */
    @SuppressWarnings("unchecked")
    private void formatSelfEvaluation(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("evaluations") || !(props.get("evaluations") instanceof List)) {
            builder.append("暂无自我评价").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> evaluations =
                (List<Map<String, Object>>) props.get("evaluations");

        for (int i = 0; i < evaluations.size(); i++) {
            Map<String, Object> evaluation = evaluations.get(i);
            if (evaluation.containsKey("content")) {
                String content = evaluation.get("content").toString();
                builder.append(fixEncoding(wrapText(content, 0))).append(LINE_SEPARATOR);
            }

            if (evaluation.containsKey("highlights")
                    && evaluation.get("highlights") instanceof List
                    && !((List<?>) evaluation.get("highlights")).isEmpty()) {
                List<String> highlights = (List<String>) evaluation.get("highlights");
                builder.append(LINE_SEPARATOR).append("亮点: ").append(LINE_SEPARATOR);
                for (String highlight : highlights) {
                    builder.append("  • ").append(fixEncoding(highlight)).append(LINE_SEPARATOR);
                }
            }

            if (i < evaluations.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 格式化求职意向 */
    @SuppressWarnings("unchecked")
    private void formatJobIntention(Map<String, Object> props, StringBuilder builder) {
        if (!props.containsKey("intentions") || !(props.get("intentions") instanceof List)) {
            builder.append("暂无求职意向信息").append(LINE_SEPARATOR);
            return;
        }

        List<Map<String, Object>> intentions = (List<Map<String, Object>>) props.get("intentions");

        for (int i = 0; i < intentions.size(); i++) {
            Map<String, Object> intention = intentions.get(i);
            builder.append("期望职位: ")
                    .append(fixEncoding(getStringValue(intention, "position", "未填写")))
                    .append(LINE_SEPARATOR);
            builder.append("工作类型: ")
                    .append(fixEncoding(getStringValue(intention, "jobType", "未填写")))
                    .append(LINE_SEPARATOR);
            builder.append("期望城市: ")
                    .append(fixEncoding(getStringValue(intention, "city", "未填写")))
                    .append(LINE_SEPARATOR);

            if (intention.containsKey("salary")) {
                Object salary = intention.get("salary");
                builder.append("期望薪资: ")
                        .append(salary.toString())
                        .append("元/年")
                        .append(LINE_SEPARATOR);
            }

            if (i < intentions.size() - 1) {
                builder.append(LINE_SEPARATOR);
            }
        }
    }

    /** 文本居中 */
    private String centerText(String text) {
        if (text == null) {
            return "";
        }

        // 修复中文编码后再计算宽度
        String fixedText = fixEncoding(text);
        int textLength = getTextWidth(fixedText);
        int totalSpaces = MAX_LINE_LENGTH - textLength;
        if (totalSpaces <= 0) {
            return fixedText;
        }

        int leftSpaces = totalSpaces / 2;
        int rightSpaces = totalSpaces - leftSpaces;

        return " ".repeat(leftSpaces) + fixedText + " ".repeat(rightSpaces);
    }

    /** 计算文本宽度（考虑中英文字符） */
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

    /** 文本换行 */
    private String wrapText(String text, int indentSpaces) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 先修复编码
        String fixedText = fixEncoding(text);

        StringBuilder result = new StringBuilder();
        String indent = " ".repeat(indentSpaces);
        int maxWidth = MAX_LINE_LENGTH - indentSpaces;

        String[] paragraphs = fixedText.split("\n");
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

    /** 保存文本到文件 */
    private void saveTextToFile(String textContent, File file) throws IOException {
        try (PrintWriter writer =
                new PrintWriter(new FileWriter(file, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(textContent);
            writer.flush();
        }

        log.debug("纯文本文件已保存: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /** 异步生成纯文本简历 */
    public CompletableFuture<File> generateAsync(Long resumesId, String version) {
        return CompletableFuture.supplyAsync(() -> generate(resumesId, "text", version));
    }

    /** 获取纯文本内容 */
    public String getTextContent(Long resumesId) throws IOException {
        File textFile = generate(resumesId, "text", null);
        return Files.readString(textFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
    }

    /** 验证文本文件 */
    public boolean validateTextFile(File textFile) {
        if (!validateOutputFile(textFile)) {
            return false;
        }

        try {
            String content =
                    Files.readString(textFile.toPath(), java.nio.charset.StandardCharsets.UTF_8);
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

    // ========== 辅助方法 ==========

    /** 获取字符串值，带默认值 */
    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /** 修复中文编码 */
    private String fixEncoding(String text) {
        if (text == null) return "";
        return resumeDataParser.fixChineseEncoding(text);
    }

    /** 获取性别显示值 */
    private String getGenderValue(Object gender) {
        if (gender == null) return "未填写";
        try {
            int genderValue;
            if (gender instanceof Integer) {
                genderValue = (Integer) gender;
            } else {
                genderValue = Integer.parseInt(gender.toString());
            }
            switch (genderValue) {
                case 0:
                    return "男";
                case 1:
                    return "女";
                default:
                    return "其他";
            }
        } catch (Exception e) {
            return "未填写";
        }
    }
}
