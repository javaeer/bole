package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.common.utils.JsonUtils;
import cn.net.yunlou.bole.handler.resumes.ResumeDataParser;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.service.ResumesService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/** Word文档生成策略（基于ResumeDataParser优化版） */
@Slf4j
@Component
public class WordDocumentGeneratorStrategy extends AbstractDocumentGeneratorStrategy {

    private static final String CACHE_NAME = "resumeWordCache";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    private final ResumeDataParser resumeDataParser;

    // 字体配置 - 使用系统字体避免乱码
    private static final String FONT_FAMILY_SIMSUN = "SimSun";
    private static final String FONT_FAMILY_MICROSOFT_YAHEI = "Microsoft YaHei";
    private static final String FONT_FAMILY_HEITI = "SimHei";
    private static final int DEFAULT_FONT_SIZE = 10;
    private static final int TITLE_FONT_SIZE = 18;
    private static final int SECTION_FONT_SIZE = 14;
    private static final int SUB_SECTION_FONT_SIZE = 12;

    public WordDocumentGeneratorStrategy(
            DocumentDirectoryManager directoryManager,
            ResumesService resumesService,
            ResumeDataParser resumeDataParser) {
        super(directoryManager, resumesService);
        this.resumeDataParser = resumeDataParser;
        log.info("Word文档生成策略初始化完成");
    }

    @Override
    public DocumentType getType() {
        return DocumentType.WORD;
    }

    @Override
    protected String getFileExtension() {
        return "docx";
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "#resumesId + '_' + #version", unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
        if (!StringUtils.hasText(version)) {
            version = "v1";
        }

        log.debug("开始生成Word简历，简历ID: {}, 版本: {}", resumesId, version);

        long startTime = System.nanoTime();
        File tempFile = null;

        try {
            // 1. 获取简历数据
            Resumes resumes = getResumesWithValidation(resumesId);

            // 2. 创建Word文档
            XWPFDocument document = createWordDocument(resumes);

            // 3. 使用基类方法创建输出文件
            File outputFile = createOutputFile(resumesId, getType(), "word", version);

            // 4. 保存Word文档
            saveWordDocument(document, outputFile);

            // 5. 验证文件
            if (!validateOutputFile(outputFile)) {
                throw new IOException("生成的Word文件验证失败");
            }

            long endTime = System.nanoTime();
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

            log.info(
                    "Word简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节",
                    resumesId,
                    duration,
                    outputFile.length());

            return outputFile;

        } catch (Exception e) {
            log.error("Word简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("Word简历生成失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFile(tempFile);
        }
    }

    /** 创建Word文档 */
    private XWPFDocument createWordDocument(Resumes resumes) throws Exception {
        XWPFDocument document = new XWPFDocument();

        // 设置页面属性
        setPageProperties(document);

        // 1. 解析全局样式
        Map<String, Object> globalStyle = parseGlobalStyle(resumes);

        // 2. 创建标题（使用全局样式中的主题色）
        String primaryColor = getColorFromStyle(globalStyle, "primaryColor", "#333333");
        createTitle(document, "个人简历", primaryColor);

        // 3. 按照模板中的顺序渲染组件
        Map<String, Object> resumeData = parseResumeData(resumes);

        // 获取组件顺序
        List<String> componentOrder = getComponentOrder(resumes);
        List<ResumesTemplateComponent> components = resumes.getComponents();

        if (componentOrder != null && !componentOrder.isEmpty()) {
            for (String componentId : componentOrder) {
                ResumesTemplateComponent component = findComponentById(components, componentId);
                if (component != null) {
                    createComponent(document, component, globalStyle);
                }
            }
        } else {
            // 如果没有指定顺序，按默认顺序渲染
            for (ResumesTemplateComponent component : components) {
                createComponent(document, component, globalStyle);
            }
        }

        // 4. 页脚
        createFooter(document, resumes);

        return document;
    }

    /** 解析全局样式 */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseGlobalStyle(Resumes resumes) {
        try {
            return resumeDataParser.parseGlobalStyle(JsonUtils.toJson(resumes.getGlobalStyle()));
        } catch (Exception e) {
            log.error("解析全局样式失败", e);
            return Collections.emptyMap();
        }
    }

    /** 解析简历数据 */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseResumeData(Resumes resumes) {
        try {
            // 简单的数据转换
            Map<String, Object> result = new HashMap<>();
            result.put("id", resumes.getId());
            result.put("globalStyle", parseGlobalStyle(resumes));
            result.put("globalLayout", resumes.getGlobalLayout());
            return result;
        } catch (Exception e) {
            log.error("解析简历数据失败", e);
            return new HashMap<>();
        }
    }

    /** 获取组件顺序 */
    @SuppressWarnings("unchecked")
    private List<String> getComponentOrder(Resumes resumes) {
        try {
            ResumesTemplateLayout globalLayout = resumes.getGlobalLayout();
            if (globalLayout != null) {
                return globalLayout.getComponentOrder();
            }
        } catch (Exception e) {
            log.warn("获取组件顺序失败", e);
        }
        return null;
    }

    /** 根据ID查找组件 */
    private ResumesTemplateComponent findComponentById(
            List<ResumesTemplateComponent> components, String componentId) {
        if (components == null) return null;

        return components.stream()
                .filter(c -> String.valueOf(c.getComponentId()).equals(componentId))
                .findFirst()
                .orElse(null);
    }

    /** 创建组件内容 */
    private void createComponent(
            XWPFDocument document,
            ResumesTemplateComponent component,
            Map<String, Object> globalStyle)
            throws Exception {
        // 解析组件数据
        Map<String, Object> componentData = resumeDataParser.parseComponentData(component);
        if (componentData.isEmpty()) {
            return;
        }

        // 获取组件名称（修复中文编码）
        String componentName =
                resumeDataParser.fixChineseEncoding((String) componentData.get("name"));

        // 创建组件标题
        String titleColor = getColorFromStyle(globalStyle, "headerColor", "#333333");
        createSubSection(document, componentName, titleColor);

        // 根据组件类型创建不同的内容
        String componentKey = (String) componentData.get("key");
        Map<String, Object> props = (Map<String, Object>) componentData.get("props");
        Map<String, Object> styles = (Map<String, Object>) componentData.get("styles");

        if (props == null) {
            createEmptyContent(document);
            return;
        }

        switch (componentKey) {
            case "UserBasicInfo":
                createUserBasicInfo(document, props, styles);
                break;
            case "Skills":
                createSkillsContent(document, props, styles);
                break;
            case "SelfEvaluation":
                createSelfEvaluationContent(document, props, styles);
                break;
            case "WorkExperience":
                createWorkExperienceContent(document, props, styles);
                break;
            case "EducationExperience":
                createEducationContent(document, props, styles);
                break;
            case "ProjectExperience":
                createProjectsContent(document, props, styles);
                break;
            case "JobIntention":
                createJobIntentionContent(document, props, styles);
                break;
            default:
                createDefaultContent(document, props, styles);
        }

        // 添加间距
        document.createParagraph();
    }

    /** 设置页面属性 */
    private void setPageProperties(XWPFDocument document) {
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSz = sectPr.addNewPgSz();
        pageSz.setW(BigInteger.valueOf(11906)); // A4纸宽度 (21cm)
        pageSz.setH(BigInteger.valueOf(16838)); // A4纸高度 (29.7cm)

        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(1701)); // 左边距 3cm
        pageMar.setRight(BigInteger.valueOf(1701)); // 右边距 3cm
        pageMar.setTop(BigInteger.valueOf(1417)); // 上边距 2.5cm
        pageMar.setBottom(BigInteger.valueOf(1417)); // 下边距 2.5cm
        pageMar.setHeader(BigInteger.valueOf(851)); // 页眉边距 1.5cm
        pageMar.setFooter(BigInteger.valueOf(851)); // 页脚边距 1.5cm
    }

    /** 创建标题 */
    private void createTitle(XWPFDocument document, String title, String color) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        titleParagraph.setSpacingAfter(400); // 增加标题后的间距

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(resumeDataParser.fixChineseEncoding(title));
        titleRun.setFontFamily(FONT_FAMILY_MICROSOFT_YAHEI);
        titleRun.setFontSize(TITLE_FONT_SIZE);
        titleRun.setBold(true);
        titleRun.setColor(color != null ? color.replace("#", "") : "000000");

        // 添加空行
        document.createParagraph();
    }

    /** 创建子章节标题 */
    private void createSubSection(XWPFDocument document, String subSectionTitle, String color) {
        XWPFParagraph sectionParagraph = document.createParagraph();
        sectionParagraph.setAlignment(ParagraphAlignment.LEFT);
        sectionParagraph.setSpacingBefore(200); // 段前间距
        sectionParagraph.setSpacingAfter(100); // 段后间距

        XWPFRun sectionRun = sectionParagraph.createRun();
        sectionRun.setText(resumeDataParser.fixChineseEncoding(subSectionTitle));
        sectionRun.setFontFamily(FONT_FAMILY_MICROSOFT_YAHEI);
        sectionRun.setFontSize(SECTION_FONT_SIZE);
        sectionRun.setBold(true);
        sectionRun.setColor(color != null ? color.replace("#", "") : "333333");

        // 添加下边框线
        addBottomBorder(sectionParagraph, color);
    }

    /** 创建基本信息 */
    @SuppressWarnings("unchecked")
    private void createUserBasicInfo(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        if (props.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String backgroundColor = getColorFromStyle(styles, "backgroundColor", "#FFFFFF");
        String titleColor = getColorFromStyle(styles, "titleColor", "#333333");
        String fieldColor = getColorFromStyle(styles, "fieldColor", "#666666");
        String padding = getStyleValue(styles, "padding", "20px");

        // 基本信息字段
        List<FieldInfo> fields = new ArrayList<>();

        // 姓名
        if (props.get("name") != null) {
            fields.add(new FieldInfo("姓名", props.get("name").toString()));
        }

        // 性别
        if (props.get("gender") != null) {
            String genderText = "男";
            try {
                int gender = Integer.parseInt(props.get("gender").toString());
                genderText = gender == 0 ? "男" : (gender == 1 ? "女" : "未知");
            } catch (Exception e) {
                genderText = props.get("gender").toString();
            }
            fields.add(new FieldInfo("性别", genderText));
        }

        // 邮箱
        if (props.get("email") != null) {
            fields.add(new FieldInfo("邮箱", props.get("email").toString()));
        }

        // 电话
        if (props.get("phone") != null) {
            fields.add(new FieldInfo("电话", props.get("phone").toString()));
        }

        // 所在地
        if (props.get("location") != null) {
            fields.add(new FieldInfo("所在地", props.get("location").toString()));
        }

        // 工作年限
        if (props.get("workYears") != null) {
            fields.add(new FieldInfo("工作年限", props.get("workYears") + "年"));
        }

        // 职位
        if (props.get("title") != null) {
            fields.add(new FieldInfo("职位", props.get("title").toString()));
        }

        // 创建表格布局
        int rows = (int) Math.ceil(fields.size() / 2.0);
        XWPFTable table = document.createTable(rows, 4);
        table.setWidth("100%");

        // 填充表格
        for (int i = 0; i < fields.size(); i++) {
            FieldInfo field = fields.get(i);
            int row = i / 2;
            int col = (i % 2) * 2;

            // 字段名单元格
            XWPFTableCell labelCell = table.getRow(row).getCell(col);
            labelCell.setWidth("20%");
            XWPFParagraph labelPara = labelCell.getParagraphs().get(0);
            labelPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun labelRun = labelPara.createRun();
            labelRun.setText(resumeDataParser.fixChineseEncoding(field.label + ":"));
            labelRun.setFontFamily(FONT_FAMILY_SIMSUN);
            labelRun.setFontSize(DEFAULT_FONT_SIZE);
            labelRun.setBold(true);
            labelRun.setColor(fieldColor.replace("#", ""));

            // 字段值单元格
            XWPFTableCell valueCell = table.getRow(row).getCell(col + 1);
            valueCell.setWidth("30%");
            XWPFParagraph valuePara = valueCell.getParagraphs().get(0);
            valuePara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun valueRun = valuePara.createRun();
            valueRun.setText(resumeDataParser.fixChineseEncoding(field.value));
            valueRun.setFontFamily(FONT_FAMILY_SIMSUN);
            valueRun.setFontSize(DEFAULT_FONT_SIZE);
            valueRun.setColor("000000");
        }
    }

    /** 创建技能内容 */
    @SuppressWarnings("unchecked")
    private void createSkillsContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> skills = (List<Map<String, Object>>) props.get("skills");
        if (skills == null || skills.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String skillNameColor = getColorFromStyle(styles, "skillNameColor", "#555555");
        String progressColor = getColorFromStyle(styles, "progressColor", "#1890ff");

        // 按类别分组技能
        Map<String, List<Map<String, Object>>> categorizedSkills = new LinkedHashMap<>();
        for (Map<String, Object> skill : skills) {
            String category = (String) skill.getOrDefault("category", "其他");
            categorizedSkills.computeIfAbsent(category, k -> new ArrayList<>()).add(skill);
        }

        // 为每个类别创建内容
        for (Map.Entry<String, List<Map<String, Object>>> entry : categorizedSkills.entrySet()) {
            // 类别标题
            XWPFParagraph categoryPara = document.createParagraph();
            categoryPara.setAlignment(ParagraphAlignment.LEFT);
            categoryPara.setSpacingBefore(100);

            XWPFRun categoryRun = categoryPara.createRun();
            categoryRun.setText(resumeDataParser.fixChineseEncoding(entry.getKey() + "："));
            categoryRun.setFontFamily(FONT_FAMILY_HEITI);
            categoryRun.setFontSize(SUB_SECTION_FONT_SIZE);
            categoryRun.setBold(true);
            categoryRun.setColor(skillNameColor.replace("#", ""));

            // 技能列表
            for (Map<String, Object> skill : entry.getValue()) {
                XWPFParagraph skillPara = document.createParagraph();
                skillPara.setAlignment(ParagraphAlignment.LEFT);
                skillPara.setIndentationLeft(200); // 缩进

                XWPFRun skillRun = skillPara.createRun();

                // 构建技能文本
                StringBuilder skillText = new StringBuilder();
                skillText.append("• ");
                skillText.append(resumeDataParser.fixChineseEncoding(skill.get("name").toString()));

                // 技能等级
                if (skill.get("level") != null) {
                    skillText.append("（");
                    skillText.append(
                            resumeDataParser.fixChineseEncoding(skill.get("level").toString()));
                    skillText.append("）");
                }

                // 经验年限
                if (skill.get("experienceYears") != null) {
                    skillText.append(" - ");
                    skillText.append(skill.get("experienceYears"));
                    skillText.append("年经验");
                }

                skillRun.setText(skillText.toString());
                skillRun.setFontFamily(FONT_FAMILY_SIMSUN);
                skillRun.setFontSize(DEFAULT_FONT_SIZE);

                // 技能描述
                if (skill.get("description") != null) {
                    String description =
                            resumeDataParser.fixChineseEncoding(
                                    skill.get("description").toString());
                    if (!description.trim().isEmpty()) {
                        XWPFParagraph descPara = document.createParagraph();
                        descPara.setAlignment(ParagraphAlignment.LEFT);
                        descPara.setIndentationLeft(400);

                        XWPFRun descRun = descPara.createRun();
                        descRun.setText("  " + description);
                        descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        descRun.setFontSize(DEFAULT_FONT_SIZE - 1);
                        descRun.setColor("666666");
                    }
                }
            }
        }
    }

    /** 创建自我评价内容 */
    @SuppressWarnings("unchecked")
    private void createSelfEvaluationContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> evaluations =
                (List<Map<String, Object>>) props.get("evaluations");
        if (evaluations == null || evaluations.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String contentColor = getColorFromStyle(styles, "contentColor", "#555555");
        String backgroundColor = getColorFromStyle(styles, "backgroundColor", "#fafafa");

        for (Map<String, Object> evaluation : evaluations) {
            if (evaluation.get("content") != null) {
                String content =
                        resumeDataParser.fixChineseEncoding(evaluation.get("content").toString());
                if (!content.trim().isEmpty()) {
                    XWPFParagraph evalPara = document.createParagraph();
                    evalPara.setAlignment(ParagraphAlignment.LEFT);
                    evalPara.setSpacingBefore(50);
                    evalPara.setSpacingAfter(50);
                    evalPara.setIndentationLeft(50);
                    evalPara.setIndentationRight(50);

                    XWPFRun evalRun = evalPara.createRun();
                    evalRun.setText(content);
                    evalRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    evalRun.setFontSize(DEFAULT_FONT_SIZE);
                    evalRun.setColor(contentColor.replace("#", ""));
                }
            }
        }
    }

    /** 创建工作经历内容 */
    @SuppressWarnings("unchecked")
    private void createWorkExperienceContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> experiences =
                (List<Map<String, Object>>) props.get("experiences");
        if (experiences == null || experiences.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String companyColor = getColorFromStyle(styles, "companyColor", "#1890ff");
        String periodColor = getColorFromStyle(styles, "periodColor", "#999999");

        for (Map<String, Object> exp : experiences) {
            // 公司名称和职位
            XWPFParagraph companyPara = document.createParagraph();
            companyPara.setAlignment(ParagraphAlignment.LEFT);
            companyPara.setSpacingBefore(80);

            XWPFRun companyRun = companyPara.createRun();

            String company = resumeDataParser.fixChineseEncoding(exp.get("company").toString());
            String position = resumeDataParser.fixChineseEncoding(exp.get("position").toString());

            String startDate = resumeDataParser.formatDate(exp.get("startDate"));
            String endDate;
            if (exp.get("isCurrent") != null
                    && Boolean.parseBoolean(exp.get("isCurrent").toString())) {
                endDate = "至今";
            } else {
                endDate = resumeDataParser.formatDate(exp.get("endDate"));
            }

            companyRun.setText(company + " - " + position);
            companyRun.setFontFamily(FONT_FAMILY_HEITI);
            companyRun.setFontSize(SUB_SECTION_FONT_SIZE);
            companyRun.setBold(true);
            companyRun.setColor(companyColor.replace("#", ""));

            // 工作时间
            XWPFParagraph periodPara = document.createParagraph();
            periodPara.setAlignment(ParagraphAlignment.LEFT);
            periodPara.setIndentationLeft(200);

            XWPFRun periodRun = periodPara.createRun();
            periodRun.setText(startDate + " - " + endDate);
            periodRun.setFontFamily(FONT_FAMILY_SIMSUN);
            periodRun.setFontSize(DEFAULT_FONT_SIZE);
            periodRun.setColor(periodColor.replace("#", ""));

            // 工作描述
            if (exp.get("description") != null) {
                String description =
                        resumeDataParser.fixChineseEncoding(exp.get("description").toString());
                if (!description.trim().isEmpty()) {
                    XWPFParagraph descPara = document.createParagraph();
                    descPara.setAlignment(ParagraphAlignment.LEFT);
                    descPara.setIndentationLeft(200);

                    XWPFRun descRun = descPara.createRun();
                    descRun.setText("工作内容：" + description);
                    descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    descRun.setFontSize(DEFAULT_FONT_SIZE);
                    descRun.setColor("555555");
                }
            }

            // 工作业绩
            List<String> achievements = (List<String>) exp.get("achievements");
            if (achievements != null && !achievements.isEmpty()) {
                XWPFParagraph achieveTitlePara = document.createParagraph();
                achieveTitlePara.setAlignment(ParagraphAlignment.LEFT);
                achieveTitlePara.setIndentationLeft(200);

                XWPFRun achieveTitleRun = achieveTitlePara.createRun();
                achieveTitleRun.setText("主要业绩：");
                achieveTitleRun.setFontFamily(FONT_FAMILY_SIMSUN);
                achieveTitleRun.setFontSize(DEFAULT_FONT_SIZE);
                achieveTitleRun.setBold(true);

                for (String achievement : achievements) {
                    String achievementText = resumeDataParser.fixChineseEncoding(achievement);
                    if (!achievementText.trim().isEmpty()) {
                        XWPFParagraph achievePara = document.createParagraph();
                        achievePara.setAlignment(ParagraphAlignment.LEFT);
                        achievePara.setIndentationLeft(400);

                        XWPFRun achieveRun = achievePara.createRun();
                        achieveRun.setText("• " + achievementText);
                        achieveRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        achieveRun.setFontSize(DEFAULT_FONT_SIZE);
                        achieveRun.setColor("666666");
                    }
                }
            }

            // 添加分隔线（除了最后一个）
            if (experiences.indexOf(exp) < experiences.size() - 1) {
                addSeparatorLine(document);
            }
        }
    }

    /** 创建教育背景内容 */
    @SuppressWarnings("unchecked")
    private void createEducationContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> experiences =
                (List<Map<String, Object>>) props.get("experiences");
        if (experiences == null || experiences.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String universityColor = getColorFromStyle(styles, "universityColor", "#52c41a");
        String majorColor = getColorFromStyle(styles, "majorColor", "#666666");

        for (Map<String, Object> edu : experiences) {
            // 学校名称和学位
            XWPFParagraph schoolPara = document.createParagraph();
            schoolPara.setAlignment(ParagraphAlignment.LEFT);
            schoolPara.setSpacingBefore(80);

            XWPFRun schoolRun = schoolPara.createRun();

            String university =
                    resumeDataParser.fixChineseEncoding(edu.get("university").toString());
            String degree = resumeDataParser.fixChineseEncoding(edu.get("degree").toString());
            String major = resumeDataParser.fixChineseEncoding(edu.get("major").toString());

            String startDate = resumeDataParser.formatDate(edu.get("startDate"));
            String endDate = resumeDataParser.formatDate(edu.get("endDate"));

            schoolRun.setText(university + " - " + degree);
            schoolRun.setFontFamily(FONT_FAMILY_HEITI);
            schoolRun.setFontSize(SUB_SECTION_FONT_SIZE);
            schoolRun.setBold(true);
            schoolRun.setColor(universityColor.replace("#", ""));

            // 专业和时间
            XWPFParagraph detailPara = document.createParagraph();
            detailPara.setAlignment(ParagraphAlignment.LEFT);
            detailPara.setIndentationLeft(200);

            XWPFRun detailRun = detailPara.createRun();
            detailRun.setText(major + " | " + startDate + " - " + endDate);
            detailRun.setFontFamily(FONT_FAMILY_SIMSUN);
            detailRun.setFontSize(DEFAULT_FONT_SIZE);
            detailRun.setColor(majorColor.replace("#", ""));

            // 描述
            if (edu.get("description") != null) {
                String description =
                        resumeDataParser.fixChineseEncoding(edu.get("description").toString());
                if (!description.trim().isEmpty()) {
                    XWPFParagraph descPara = document.createParagraph();
                    descPara.setAlignment(ParagraphAlignment.LEFT);
                    descPara.setIndentationLeft(200);

                    XWPFRun descRun = descPara.createRun();
                    descRun.setText("研究方向：" + description);
                    descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    descRun.setFontSize(DEFAULT_FONT_SIZE);
                    descRun.setColor("555555");
                }
            }

            // 荣誉/成就
            List<String> achievements = (List<String>) edu.get("achievements");
            if (achievements != null && !achievements.isEmpty()) {
                for (String achievement : achievements) {
                    String achievementText = resumeDataParser.fixChineseEncoding(achievement);
                    if (!achievementText.trim().isEmpty()) {
                        XWPFParagraph honorPara = document.createParagraph();
                        honorPara.setAlignment(ParagraphAlignment.LEFT);
                        honorPara.setIndentationLeft(200);

                        XWPFRun honorRun = honorPara.createRun();
                        honorRun.setText("• " + achievementText);
                        honorRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        honorRun.setFontSize(DEFAULT_FONT_SIZE);
                        honorRun.setColor("666666");
                    }
                }
            }

            // 添加分隔线（除了最后一个）
            if (experiences.indexOf(edu) < experiences.size() - 1) {
                addSeparatorLine(document);
            }
        }
    }

    /** 创建项目经验内容 */
    @SuppressWarnings("unchecked")
    private void createProjectsContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> experiences =
                (List<Map<String, Object>>) props.get("experiences");
        if (experiences == null || experiences.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        for (Map<String, Object> project : experiences) {
            // 项目名称
            XWPFParagraph namePara = document.createParagraph();
            namePara.setAlignment(ParagraphAlignment.LEFT);
            namePara.setSpacingBefore(80);

            XWPFRun nameRun = namePara.createRun();

            String projectName =
                    resumeDataParser.fixChineseEncoding(
                            project.getOrDefault("name", "未命名项目").toString());
            String startDate = resumeDataParser.formatDate(project.get("startDate"));
            String endDate = resumeDataParser.formatDate(project.get("endDate"));

            nameRun.setText(projectName + " (" + startDate + " - " + endDate + ")");
            nameRun.setFontFamily(FONT_FAMILY_HEITI);
            nameRun.setFontSize(SUB_SECTION_FONT_SIZE);
            nameRun.setBold(true);

            // 项目描述
            if (project.get("description") != null) {
                String description =
                        resumeDataParser.fixChineseEncoding(project.get("description").toString());
                if (!description.trim().isEmpty()) {
                    XWPFParagraph descPara = document.createParagraph();
                    descPara.setAlignment(ParagraphAlignment.LEFT);
                    descPara.setIndentationLeft(200);

                    XWPFRun descRun = descPara.createRun();
                    descRun.setText("项目描述：" + description);
                    descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    descRun.setFontSize(DEFAULT_FONT_SIZE);
                    descRun.setColor("555555");
                }
            }

            // 项目成就
            List<String> achievements = (List<String>) project.get("achievements");
            if (achievements != null && !achievements.isEmpty()) {
                XWPFParagraph achieveTitlePara = document.createParagraph();
                achieveTitlePara.setAlignment(ParagraphAlignment.LEFT);
                achieveTitlePara.setIndentationLeft(200);

                XWPFRun achieveTitleRun = achieveTitlePara.createRun();
                achieveTitleRun.setText("项目成果：");
                achieveTitleRun.setFontFamily(FONT_FAMILY_SIMSUN);
                achieveTitleRun.setFontSize(DEFAULT_FONT_SIZE);
                achieveTitleRun.setBold(true);

                for (String achievement : achievements) {
                    String achievementText = resumeDataParser.fixChineseEncoding(achievement);
                    if (!achievementText.trim().isEmpty()) {
                        XWPFParagraph achievePara = document.createParagraph();
                        achievePara.setAlignment(ParagraphAlignment.LEFT);
                        achievePara.setIndentationLeft(400);

                        XWPFRun achieveRun = achievePara.createRun();
                        achieveRun.setText("• " + achievementText);
                        achieveRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        achieveRun.setFontSize(DEFAULT_FONT_SIZE);
                        achieveRun.setColor("666666");
                    }
                }
            }

            // 添加分隔线（除了最后一个）
            if (experiences.indexOf(project) < experiences.size() - 1) {
                addSeparatorLine(document);
            }
        }
    }

    /** 创建求职意向内容 */
    @SuppressWarnings("unchecked")
    private void createJobIntentionContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        List<Map<String, Object>> intentions = (List<Map<String, Object>>) props.get("intentions");
        if (intentions == null || intentions.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 获取样式设置
        String highlightColor = getColorFromStyle(styles, "highlightColor", "#1890ff");

        // 使用表格显示求职意向
        XWPFTable table = document.createTable(intentions.size() + 1, 4);
        table.setWidth("100%");

        // 表头
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"期望职位", "工作类型", "期望地点", "期望薪资"};

        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            cell.setWidth("25%");
            XWPFParagraph para = cell.getParagraphs().get(0);
            para.setAlignment(ParagraphAlignment.CENTER);

            XWPFRun run = para.createRun();
            run.setText(resumeDataParser.fixChineseEncoding(headers[i]));
            run.setFontFamily(FONT_FAMILY_SIMSUN);
            run.setFontSize(DEFAULT_FONT_SIZE);
            run.setBold(true);
            run.setColor("FFFFFF");

            // 设置表头背景色
            CTShd shd = cell.getCTTc().addNewTcPr().addNewShd();
            shd.setFill(highlightColor.replace("#", ""));
        }

        // 数据行
        for (int i = 0; i < intentions.size(); i++) {
            Map<String, Object> intention = intentions.get(i);
            XWPFTableRow row = table.getRow(i + 1);

            String[] values = {
                resumeDataParser.fixChineseEncoding(
                        intention.getOrDefault("position", "").toString()),
                resumeDataParser.fixChineseEncoding(
                        intention.getOrDefault("jobType", "").toString()),
                resumeDataParser.fixChineseEncoding(intention.getOrDefault("city", "").toString()),
                intention.getOrDefault("salary", "").toString()
            };

            for (int j = 0; j < values.length; j++) {
                XWPFTableCell cell = row.getCell(j);
                XWPFParagraph para = cell.getParagraphs().get(0);
                para.setAlignment(ParagraphAlignment.CENTER);

                XWPFRun run = para.createRun();
                run.setText(values[j]);
                run.setFontFamily(FONT_FAMILY_SIMSUN);
                run.setFontSize(DEFAULT_FONT_SIZE);
            }
        }
    }

    /** 创建默认内容 */
    private void createDefaultContent(
            XWPFDocument document, Map<String, Object> props, Map<String, Object> styles)
            throws Exception {
        createEmptyContent(document);
    }

    /** 创建空内容提示 */
    private void createEmptyContent(XWPFDocument document) throws Exception {
        XWPFParagraph emptyPara = document.createParagraph();
        emptyPara.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun emptyRun = emptyPara.createRun();
        emptyRun.setText(resumeDataParser.fixChineseEncoding("暂无内容"));
        emptyRun.setFontFamily(FONT_FAMILY_SIMSUN);
        emptyRun.setFontSize(DEFAULT_FONT_SIZE);
        emptyRun.setColor("999999");
        emptyRun.setItalic(true);
    }

    /** 添加底部边框 */
    private void addBottomBorder(XWPFParagraph paragraph, String color) {
        try {
            CTP ctp = paragraph.getCTP();
            CTPPr pPr = ctp.getPPr();
            if (pPr == null) {
                pPr = ctp.addNewPPr();
            }

            CTBorder bottomBorder = CTBorder.Factory.newInstance();
            bottomBorder.setVal(STBorder.SINGLE);
            bottomBorder.setSz(BigInteger.valueOf(8));
            bottomBorder.setColor(color != null ? color.replace("#", "") : "722ed1");

            CTPBdr borders = CTPBdr.Factory.newInstance();
            borders.setBottom(bottomBorder);
            pPr.setPBdr(borders);
        } catch (Exception e) {
            log.warn("添加底部边框失败", e);
        }
    }

    /** 添加分隔线 */
    private void addSeparatorLine(XWPFDocument document) {
        XWPFParagraph separatorPara = document.createParagraph();
        separatorPara.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun separatorRun = separatorPara.createRun();
        separatorRun.addBreak();
        separatorRun.setText("────────────────────");
        separatorRun.setFontFamily(FONT_FAMILY_SIMSUN);
        separatorRun.setFontSize(8);
        separatorRun.setColor("CCCCCC");
        separatorRun.addBreak();
    }

    /** 创建页脚 */
    private void createFooter(XWPFDocument document, Resumes resumes) throws Exception {
        document.createParagraph();

        XWPFParagraph footerPara = document.createParagraph();
        footerPara.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun footerRun = footerPara.createRun();
        footerRun.setText(
                resumeDataParser.fixChineseEncoding(
                        "简历编号: "
                                + resumes.getId()
                                + " | "
                                + "生成时间: "
                                + LocalDateTime.now().format(DATE_FORMATTER)));
        footerRun.setFontFamily(FONT_FAMILY_SIMSUN);
        footerRun.setFontSize(9);
        footerRun.setColor("666666");
    }

    /** 保存Word文档 */
    private void saveWordDocument(XWPFDocument document, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.write(fos);
            fos.flush();
        }

        log.debug("Word文档已保存: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /** 从样式中获取颜色值 */
    private String getColorFromStyle(Map<String, Object> styles, String key, String defaultValue) {
        if (styles == null) return defaultValue;
        Object value = styles.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /** 从样式中获取值 */
    private String getStyleValue(Map<String, Object> styles, String key, String defaultValue) {
        if (styles == null) return defaultValue;
        Object value = styles.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    /** 字段信息辅助类 */
    private static class FieldInfo {
        String label;
        String value;

        FieldInfo(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return DocumentType.WORD == documentType;
    }
}
