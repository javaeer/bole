package cn.net.yunlou.bole.handler.document;

import cn.net.yunlou.bole.common.constant.DocumentType;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.service.ResumesService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Word文档生成策略（使用Apache POI）
 */
@Slf4j
@Component
public class WordDocumentGeneratorStrategy extends AbstractDocumentGeneratorStrategy {

    private static final String CACHE_NAME = "resumeWordCache";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    // 字体配置
    private static final String FONT_FAMILY_SIMSUN = "宋体";
    private static final String FONT_FAMILY_MICROSOFT_YAHEI = "微软雅黑";
    private static final int DEFAULT_FONT_SIZE = 10;
    private static final int TITLE_FONT_SIZE = 16;
    private static final int SECTION_FONT_SIZE = 14;
    private static final int SUB_SECTION_FONT_SIZE = 12;

    public WordDocumentGeneratorStrategy(DocumentDirectoryManager directoryManager,
                                         ResumesService resumesService) {
        super(directoryManager,resumesService);
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
    @Cacheable(value = CACHE_NAME, key = "#resumesId + '_' + #version",
            unless = "#result == null")
    public File generate(Long resumesId, String device, String version) {
        // Word 文档通常不需要设备类型
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

            log.info("Word简历生成完成，简历ID: {}, 耗时: {}ms, 文件大小: {}字节",
                    resumesId, duration, outputFile.length());

            return outputFile;

        } catch (Exception e) {
            log.error("Word简历生成失败，简历ID: {}", resumesId, e);
            throw new RuntimeException("Word简历生成失败: " + e.getMessage(), e);
        } finally {
            cleanupTempFile(tempFile);
        }
    }

    /**
     * 创建Word文档
     */
    private XWPFDocument createWordDocument(Resumes resumes) throws Exception {
        XWPFDocument document = new XWPFDocument();

        // 设置页面属性
        setPageProperties(document);

        // 1. 标题
        createTitle(document, "个人简历");

        // 2. 简历组件
        List<ResumesTemplateComponent> components = resumes.getComponents();
        if (components != null && !components.isEmpty()) {
            for (ResumesTemplateComponent component : components) {
                createSubSection(document, component.getName());
                createComponentContent(document, component);
            }
        }

        // 4. 页脚
        createFooter(document, resumes);

        return document;
    }

    /**
     * 设置页面属性
     */
    private void setPageProperties(XWPFDocument document) {
        // 设置页边距
        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        CTPageSz pageSz = sectPr.addNewPgSz();
        pageSz.setW(BigInteger.valueOf(11906));  // A4纸宽度 (21cm * 567 = 11907)
        pageSz.setH(BigInteger.valueOf(16838));  // A4纸高度 (29.7cm * 567 = 16838)

        CTPageMar pageMar = sectPr.addNewPgMar();
        pageMar.setLeft(BigInteger.valueOf(1701));    // 左边距 3cm
        pageMar.setRight(BigInteger.valueOf(1701));   // 右边距 3cm
        pageMar.setTop(BigInteger.valueOf(1417));     // 上边距 2.5cm
        pageMar.setBottom(BigInteger.valueOf(1417));  // 下边距 2.5cm
        pageMar.setHeader(BigInteger.valueOf(851));   // 页眉边距 1.5cm
        pageMar.setFooter(BigInteger.valueOf(851));   // 页脚边距 1.5cm
    }

    /**
     * 创建标题
     */
    private void createTitle(XWPFDocument document, String title) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setFontFamily(FONT_FAMILY_MICROSOFT_YAHEI);
        titleRun.setFontSize(TITLE_FONT_SIZE);
        titleRun.setBold(true);

        // 添加空行
        document.createParagraph();
    }

    /**
     * 创建章节标题
     */
    private void createSection(XWPFDocument document, String sectionTitle) {
        XWPFParagraph sectionParagraph = document.createParagraph();
        sectionParagraph.setAlignment(ParagraphAlignment.LEFT);
        sectionParagraph.setSpacingBefore(200);  // 段前间距

        XWPFRun sectionRun = sectionParagraph.createRun();
        sectionRun.setText(sectionTitle);
        sectionRun.setFontFamily(FONT_FAMILY_MICROSOFT_YAHEI);
        sectionRun.setFontSize(SECTION_FONT_SIZE);
        sectionRun.setBold(true);
        sectionRun.setColor("000000");

        // 添加下划线
        addBottomBorder(sectionParagraph);

        document.createParagraph();
    }

    /**
     * 创建子章节标题
     */
    private void createSubSection(XWPFDocument document, String subSectionTitle) {
        XWPFParagraph subSectionParagraph = document.createParagraph();
        subSectionParagraph.setAlignment(ParagraphAlignment.LEFT);
        subSectionParagraph.setSpacingBefore(100);  // 段前间距

        XWPFRun subSectionRun = subSectionParagraph.createRun();
        subSectionRun.setText(subSectionTitle);
        subSectionRun.setFontFamily(FONT_FAMILY_MICROSOFT_YAHEI);
        subSectionRun.setFontSize(SUB_SECTION_FONT_SIZE);
        subSectionRun.setBold(true);
        subSectionRun.setColor("333333");

        document.createParagraph();
    }

    /**
     * 创建组件内容
     */
    private void createComponentContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        if (component.getProps() == null) {
            createEmptyContent(document);
            return;
        }

        // 根据组件类型创建不同的内容
        switch (component.getKey()) {
            case "userBaseInfo":
                createUserBasicInfo(document,component);
                break;
            case "education":
                createEducationContent(document, component);
                break;
            case "work_experience":
                createWorkExperienceContent(document, component);
                break;
            case "skills":
                createSkillsContent(document, component);
                break;
            case "projects":
                createProjectsContent(document, component);
                break;
            case "certificates":
                createCertificatesContent(document, component);
                break;
            case "self_evaluation":
                createSelfEvaluationContent(document, component);
                break;
            default:
                createDefaultContent(document, component);
        }

        document.createParagraph();
    }

    /**
     * 创建基本信息
     */
    private void createUserBasicInfo(XWPFDocument document, ResumesTemplateComponent component) throws Exception {

        Map<String, Object> basicInfo = component.getProps();

        // 使用表格布局基本信息
        XWPFTable table = document.createTable(basicInfo.size(), 2);
        table.setWidth("100%");

        int rowIndex = 0;
        for (Map.Entry<String, Object> entry : basicInfo.entrySet()) {
            XWPFTableRow row = table.getRow(rowIndex);

            // 标签单元格
            XWPFTableCell labelCell = row.getCell(0);
            labelCell.setWidth("30%");
            XWPFParagraph labelPara = labelCell.getParagraphs().get(0);
            labelPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun labelRun = labelPara.createRun();
            labelRun.setText(entry.getKey() + ":");
            labelRun.setFontFamily(FONT_FAMILY_SIMSUN);
            labelRun.setFontSize(DEFAULT_FONT_SIZE);
            labelRun.setBold(true);

            // 值单元格
            XWPFTableCell valueCell = row.getCell(1);
            valueCell.setWidth("70%");
            XWPFParagraph valuePara = valueCell.getParagraphs().get(0);
            valuePara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun valueRun = valuePara.createRun();
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            valueRun.setText(value);
            valueRun.setFontFamily(FONT_FAMILY_SIMSUN);
            valueRun.setFontSize(DEFAULT_FONT_SIZE);

            rowIndex++;
        }

        document.createParagraph();
    }


    /**
     * 创建教育背景内容
     */
    @SuppressWarnings("unchecked")
    private void createEducationContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        List<Map<String, Object>> educations = (List<Map<String, Object>>)
                component.getProps().get("educations");

        if (educations == null || educations.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        for (int i = 0; i < educations.size(); i++) {
            Map<String, Object> edu = educations.get(i);

            // 创建学校和时间行
            XWPFParagraph schoolPara = document.createParagraph();
            schoolPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun schoolRun = schoolPara.createRun();
            schoolRun.setText(edu.get("school") + " | " + edu.get("degree"));
            schoolRun.setFontFamily(FONT_FAMILY_SIMSUN);
            schoolRun.setFontSize(DEFAULT_FONT_SIZE);
            schoolRun.setBold(true);

            // 专业和时间
            XWPFParagraph detailPara = document.createParagraph();
            detailPara.setAlignment(ParagraphAlignment.LEFT);
            detailPara.setIndentationLeft(200);  // 缩进

            XWPFRun detailRun = detailPara.createRun();
            detailRun.setText(edu.get("major") + " | " +
                    edu.get("startDate") + " - " + edu.get("endDate"));
            detailRun.setFontFamily(FONT_FAMILY_SIMSUN);
            detailRun.setFontSize(DEFAULT_FONT_SIZE);
            detailRun.setColor("666666");

            // 描述
            if (edu.get("description") != null) {
                XWPFParagraph descPara = document.createParagraph();
                descPara.setAlignment(ParagraphAlignment.LEFT);
                descPara.setIndentationLeft(400);

                XWPFRun descRun = descPara.createRun();
                descRun.setText(edu.get("description").toString());
                descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                descRun.setFontSize(DEFAULT_FONT_SIZE);
            }

            if (i < educations.size() - 1) {
                document.createParagraph();
            }
        }
    }

    /**
     * 创建工作经历内容
     */
    @SuppressWarnings("unchecked")
    private void createWorkExperienceContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        List<Map<String, Object>> experiences = (List<Map<String, Object>>)
                component.getProps().get("experiences");

        if (experiences == null || experiences.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        for (int i = 0; i < experiences.size(); i++) {
            Map<String, Object> exp = experiences.get(i);

            // 公司名称
            XWPFParagraph companyPara = document.createParagraph();
            companyPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun companyRun = companyPara.createRun();
            companyRun.setText(exp.get("company").toString());
            companyRun.setFontFamily(FONT_FAMILY_SIMSUN);
            companyRun.setFontSize(DEFAULT_FONT_SIZE);
            companyRun.setBold(true);

            // 职位和时间
            XWPFParagraph positionPara = document.createParagraph();
            positionPara.setAlignment(ParagraphAlignment.LEFT);
            positionPara.setIndentationLeft(200);

            XWPFRun positionRun = positionPara.createRun();
            String positionText = exp.get("position") + " | " +
                    exp.get("startDate") + " - " + exp.get("endDate");
            positionRun.setText(positionText);
            positionRun.setFontFamily(FONT_FAMILY_SIMSUN);
            positionRun.setFontSize(DEFAULT_FONT_SIZE);
            positionRun.setColor("666666");

            // 部门
            if (exp.get("department") != null) {
                XWPFParagraph deptPara = document.createParagraph();
                deptPara.setAlignment(ParagraphAlignment.LEFT);
                deptPara.setIndentationLeft(200);

                XWPFRun deptRun = deptPara.createRun();
                deptRun.setText("部门: " + exp.get("department"));
                deptRun.setFontFamily(FONT_FAMILY_SIMSUN);
                deptRun.setFontSize(DEFAULT_FONT_SIZE);
            }

            // 工作描述
            if (exp.get("description") != null) {
                XWPFParagraph descPara = document.createParagraph();
                descPara.setAlignment(ParagraphAlignment.LEFT);
                descPara.setIndentationLeft(400);

                XWPFRun descRun = descPara.createRun();
                descRun.setText("工作描述:");
                descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                descRun.setFontSize(DEFAULT_FONT_SIZE);
                descRun.setBold(true);

                // 详细描述
                XWPFParagraph detailsPara = document.createParagraph();
                detailsPara.setAlignment(ParagraphAlignment.LEFT);
                detailsPara.setIndentationLeft(600);

                XWPFRun detailsRun = detailsPara.createRun();
                detailsRun.setText(exp.get("description").toString());
                detailsRun.setFontFamily(FONT_FAMILY_SIMSUN);
                detailsRun.setFontSize(DEFAULT_FONT_SIZE);
            }

            // 工作业绩
            if (exp.get("achievements") != null) {
                List<String> achievements = (List<String>) exp.get("achievements");
                if (!achievements.isEmpty()) {
                    XWPFParagraph achievePara = document.createParagraph();
                    achievePara.setAlignment(ParagraphAlignment.LEFT);
                    achievePara.setIndentationLeft(400);

                    XWPFRun achieveRun = achievePara.createRun();
                    achieveRun.setText("工作业绩:");
                    achieveRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    achieveRun.setFontSize(DEFAULT_FONT_SIZE);
                    achieveRun.setBold(true);

                    for (String achievement : achievements) {
                        XWPFParagraph itemPara = document.createParagraph();
                        itemPara.setAlignment(ParagraphAlignment.LEFT);
                        itemPara.setIndentationLeft(600);

                        XWPFRun itemRun = itemPara.createRun();
                        itemRun.setText("• " + achievement);
                        itemRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        itemRun.setFontSize(DEFAULT_FONT_SIZE);
                    }
                }
            }

            if (i < experiences.size() - 1) {
                document.createParagraph();
            }
        }
    }

    /**
     * 创建技能内容
     */
    @SuppressWarnings("unchecked")
    private void createSkillsContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        List<Map<String, Object>> skills = (List<Map<String, Object>>)
                component.getProps().get("skills");

        if (skills == null || skills.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        // 使用表格显示技能
        XWPFTable table = document.createTable(skills.size(), 3);
        table.setWidth("100%");

        for (int i = 0; i < skills.size(); i++) {
            Map<String, Object> skill = skills.get(i);
            XWPFTableRow row = table.getRow(i);

            // 技能名称
            XWPFTableCell nameCell = row.getCell(0);
            nameCell.setWidth("30%");
            XWPFParagraph namePara = nameCell.getParagraphs().get(0);
            namePara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun nameRun = namePara.createRun();
            nameRun.setText(skill.get("name").toString());
            nameRun.setFontFamily(FONT_FAMILY_SIMSUN);
            nameRun.setFontSize(DEFAULT_FONT_SIZE);
            nameRun.setBold(true);

            // 技能等级
            XWPFTableCell levelCell = row.getCell(1);
            levelCell.setWidth("40%");
            XWPFParagraph levelPara = levelCell.getParagraphs().get(0);
            levelPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun levelRun = levelPara.createRun();
            if (skill.get("level") != null) {
                int level = Integer.parseInt(skill.get("level").toString());
                levelRun.setText("熟练程度: " + level + "/5");
            }
            levelRun.setFontFamily(FONT_FAMILY_SIMSUN);
            levelRun.setFontSize(DEFAULT_FONT_SIZE);

            // 技能描述
            XWPFTableCell descCell = row.getCell(2);
            descCell.setWidth("30%");
            XWPFParagraph descPara = descCell.getParagraphs().get(0);
            descPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun descRun = descPara.createRun();
            if (skill.get("description") != null) {
                descRun.setText(skill.get("description").toString());
            }
            descRun.setFontFamily(FONT_FAMILY_SIMSUN);
            descRun.setFontSize(DEFAULT_FONT_SIZE);
        }
    }

    /**
     * 创建项目经验内容
     */
    @SuppressWarnings("unchecked")
    private void createProjectsContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        List<Map<String, Object>> projects = (List<Map<String, Object>>)
                component.getProps().get("projects");

        if (projects == null || projects.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        for (int i = 0; i < projects.size(); i++) {
            Map<String, Object> project = projects.get(i);

            // 项目名称
            XWPFParagraph namePara = document.createParagraph();
            namePara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun nameRun = namePara.createRun();
            nameRun.setText((i + 1) + ". " + project.get("name"));
            nameRun.setFontFamily(FONT_FAMILY_SIMSUN);
            nameRun.setFontSize(DEFAULT_FONT_SIZE);
            nameRun.setBold(true);

            // 项目角色和时间
            XWPFParagraph rolePara = document.createParagraph();
            rolePara.setAlignment(ParagraphAlignment.LEFT);
            rolePara.setIndentationLeft(200);

            XWPFRun roleRun = rolePara.createRun();
            String roleText = "担任角色: " + project.get("role") + " | " +
                    "项目时间: " + project.get("startDate") + " - " + project.get("endDate");
            roleRun.setText(roleText);
            roleRun.setFontFamily(FONT_FAMILY_SIMSUN);
            roleRun.setFontSize(DEFAULT_FONT_SIZE);

            // 项目描述
            if (project.get("description") != null) {
                XWPFParagraph descPara = document.createParagraph();
                descPara.setAlignment(ParagraphAlignment.LEFT);
                descPara.setIndentationLeft(200);

                XWPFRun descRun = descPara.createRun();
                descRun.setText("项目描述: " + project.get("description"));
                descRun.setFontFamily(FONT_FAMILY_SIMSUN);
                descRun.setFontSize(DEFAULT_FONT_SIZE);
            }

            // 技术栈
            if (project.get("technologies") != null) {
                List<String> technologies = (List<String>) project.get("technologies");
                if (!technologies.isEmpty()) {
                    XWPFParagraph techPara = document.createParagraph();
                    techPara.setAlignment(ParagraphAlignment.LEFT);
                    techPara.setIndentationLeft(200);

                    XWPFRun techRun = techPara.createRun();
                    techRun.setText("技术栈: " + String.join(", ", technologies));
                    techRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    techRun.setFontSize(DEFAULT_FONT_SIZE);
                }
            }

            // 项目职责
            if (project.get("responsibilities") != null) {
                List<String> responsibilities = (List<String>) project.get("responsibilities");
                if (!responsibilities.isEmpty()) {
                    XWPFParagraph respPara = document.createParagraph();
                    respPara.setAlignment(ParagraphAlignment.LEFT);
                    respPara.setIndentationLeft(200);

                    XWPFRun respRun = respPara.createRun();
                    respRun.setText("项目职责:");
                    respRun.setFontFamily(FONT_FAMILY_SIMSUN);
                    respRun.setFontSize(DEFAULT_FONT_SIZE);
                    respRun.setBold(true);

                    for (String responsibility : responsibilities) {
                        XWPFParagraph itemPara = document.createParagraph();
                        itemPara.setAlignment(ParagraphAlignment.LEFT);
                        itemPara.setIndentationLeft(400);

                        XWPFRun itemRun = itemPara.createRun();
                        itemRun.setText("• " + responsibility);
                        itemRun.setFontFamily(FONT_FAMILY_SIMSUN);
                        itemRun.setFontSize(DEFAULT_FONT_SIZE);
                    }
                }
            }

            if (i < projects.size() - 1) {
                document.createParagraph();
            }
        }
    }

    /**
     * 创建证书内容
     */
    @SuppressWarnings("unchecked")
    private void createCertificatesContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        List<Map<String, Object>> certificates = (List<Map<String, Object>>)
                component.getProps().get("certificates");

        if (certificates == null || certificates.isEmpty()) {
            createEmptyContent(document);
            return;
        }

        for (int i = 0; i < certificates.size(); i++) {
            Map<String, Object> cert = certificates.get(i);

            XWPFParagraph certPara = document.createParagraph();
            certPara.setAlignment(ParagraphAlignment.LEFT);

            XWPFRun certRun = certPara.createRun();
            certRun.setText("• " + cert.get("name") + " | " +
                    cert.get("issueDate") + " | " +
                    cert.get("issuingAuthority"));
            certRun.setFontFamily(FONT_FAMILY_SIMSUN);
            certRun.setFontSize(DEFAULT_FONT_SIZE);
        }
    }

    /**
     * 创建自我评价内容
     */
    private void createSelfEvaluationContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        String content = (String) component.getProps().get("content");

        if (!StringUtils.hasText(content)) {
            createEmptyContent(document);
            return;
        }

        XWPFParagraph evalPara = document.createParagraph();
        evalPara.setAlignment(ParagraphAlignment.LEFT);
        evalPara.setIndentationLeft(200);

        XWPFRun evalRun = evalPara.createRun();
        evalRun.setText(content);
        evalRun.setFontFamily(FONT_FAMILY_SIMSUN);
        evalRun.setFontSize(DEFAULT_FONT_SIZE);
    }

    /**
     * 创建默认内容
     */
    private void createDefaultContent(XWPFDocument document, ResumesTemplateComponent component) throws Exception {
        createEmptyContent(document);
    }

    /**
     * 创建空内容提示
     */
    private void createEmptyContent(XWPFDocument document) throws Exception {
        XWPFParagraph emptyPara = document.createParagraph();
        emptyPara.setAlignment(ParagraphAlignment.LEFT);

        XWPFRun emptyRun = emptyPara.createRun();
        emptyRun.setText("暂无内容");
        emptyRun.setFontFamily(FONT_FAMILY_SIMSUN);
        emptyRun.setFontSize(DEFAULT_FONT_SIZE);
        emptyRun.setColor("999999");
        emptyRun.setItalic(true);
    }

    /**
     * 创建页脚
     */
    private void createFooter(XWPFDocument document, Resumes resumes) throws Exception {
        document.createParagraph();

        XWPFParagraph footerPara = document.createParagraph();
        footerPara.setAlignment(ParagraphAlignment.CENTER);

        XWPFRun footerRun = footerPara.createRun();
        footerRun.setText("简历编号: " + resumes.getId() + " | " +
                "生成时间: " + LocalDateTime.now().format(DATE_FORMATTER));
        footerRun.setFontFamily(FONT_FAMILY_SIMSUN);
        footerRun.setFontSize(9);
        footerRun.setColor("666666");
    }

    /**
     * 添加底部边框
     */
    private void addBottomBorder(XWPFParagraph paragraph) {
        CTP ctp = paragraph.getCTP();
        CTPPr pPr = ctp.getPPr();
        if (pPr == null) {
            pPr = ctp.addNewPPr();
        }

        CTBorder bottomBorder = CTBorder.Factory.newInstance();
        bottomBorder.setVal(STBorder.SINGLE);
        bottomBorder.setSz(BigInteger.valueOf(4));
        bottomBorder.setColor("000000");

        CTBorder topBorder = CTBorder.Factory.newInstance();
        topBorder.setVal(STBorder.NIL);

        CTBorder leftBorder = CTBorder.Factory.newInstance();
        leftBorder.setVal(STBorder.NIL);

        CTBorder rightBorder = CTBorder.Factory.newInstance();
        rightBorder.setVal(STBorder.NIL);
    }

    /**
     * 保存Word文档
     */
    private void saveWordDocument(XWPFDocument document, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.write(fos);
            fos.flush();
        }

        log.debug("Word文档已保存: {}, 大小: {}字节", file.getAbsolutePath(), file.length());
    }

    /**
     * 异步生成Word简历
     */
    public CompletableFuture<File> generateAsync(Long resumesId, String version) {
        return CompletableFuture.supplyAsync(() -> generate(resumesId, null, version));
    }

    /**
     * 验证Word文件
     */
    public boolean validateWordFile(File wordFile) {
        if (!validateOutputFile(wordFile)) {
            return false;
        }

        try {
            // 简单的Word文件头验证（检查是否是ZIP格式的DOCX）
            byte[] header = new byte[4];
            try (java.io.FileInputStream fis = new java.io.FileInputStream(wordFile)) {
                fis.read(header);
            }

            // DOCX文件是ZIP格式，前两个字节是PK
            boolean isValidDocx = (header[0] == 0x50 && header[1] == 0x4B);

            if (!isValidDocx) {
                log.warn("文件不是有效的DOCX格式: {}", wordFile.getAbsolutePath());
            }

            return isValidDocx;

        } catch (IOException e) {
            log.error("验证Word文件失败", e);
            return false;
        }
    }

    @Override
    public boolean supports(DocumentType documentType) {
        return DocumentType.WORD == documentType;
    }
}