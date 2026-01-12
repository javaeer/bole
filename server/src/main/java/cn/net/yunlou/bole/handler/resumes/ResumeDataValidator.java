package cn.net.yunlou.bole.handler.resumes;

import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayoutColumns;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ResumeDataValidator {

    /** 验证简历数据完整性 */
    public ValidationResult validateResume(Resumes resume) {
        ValidationResult result = new ValidationResult();

        if (resume == null) {
            result.addError("resume", "简历数据不能为空");
            return result;
        }

        // 验证基本信息
        if (!StringUtils.hasText(resume.getName())) {
            result.addWarning("name", "简历名称未设置");
        }

        if (resume.getTemplateId() == null) {
            result.addError("templateId", "未指定简历模板");
        }

        // 验证组件数据
        List<ResumesTemplateComponent> components = resume.getComponents();
        if (components != null) {
            for (int i = 0; i < components.size(); i++) {
                ResumesTemplateComponent comp = components.get(i);
                if (comp.getComponentId() == null) {
                    result.addWarning("components[" + i + "].componentId", "组件ID为空，可能无法正确渲染");
                }

                // 验证必要属性
                if ("contact_info".equals(comp.getKey())) {
                    if (comp.getProps() == null || !comp.getProps().containsKey("name")) {
                        result.addWarning("components[" + i + "].props.name", "联系信息组件缺少姓名");
                    }
                }
            }
        }

        return result;
    }

    /** 验证布局配置 */
    public ValidationResult validateLayout(ResumesTemplateLayout layout) {
        ValidationResult result = new ValidationResult();

        if (layout == null) {
            result.addWarning("layout", "未设置布局配置，将使用默认布局");
            return result;
        }

        String type = layout.getType();
        List<String> validTypes = Arrays.asList("single-column", "two-column", "three-column");

        if (!validTypes.contains(type)) {
            result.addError("layout.type", "无效的布局类型: " + type);
        }

        // 验证双栏布局的宽度配置
        if ("two-column".equals(type) && layout.getColumns() != null) {
            ResumesTemplateLayoutColumns columns = layout.getColumns();
            Integer left = columns.getLeft();
            Integer right = columns.getRight();

            if (left != null && (left < 10 || left > 90)) {
                result.addError("layout.columns.left", "左侧栏宽度必须在10%-90%之间");
            }

            if (right != null && (right < 10 || right > 90)) {
                result.addError("layout.columns.right", "右侧栏宽度必须在10%-90%之间");
            }

            if (left != null && right != null && (left + right) != 100) {
                result.addWarning("layout.columns.sum", "左右栏宽度之和不等于100%，布局可能异常");
            }
        }

        return result;
    }

    /** 验证结果类 */
    @Data
    public static class ValidationResult {
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();

        public void addError(String field, String message) {
            errors.add(field + ": " + message);
        }

        public void addWarning(String field, String message) {
            warnings.add(field + ": " + message);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }

        public boolean hasWarnings() {
            return !warnings.isEmpty();
        }
    }
}
