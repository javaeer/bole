package cn.net.yunlou.bole.controller;

import cn.net.yunlou.bole.common.utils.JsonUtils;
import cn.net.yunlou.bole.common.utils.StyleUtils;
import cn.net.yunlou.bole.handler.resumes.ResumesLayoutCalculator;
import cn.net.yunlou.bole.handler.resumes.ResumesStyleCalculator;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplateLayout;
import cn.net.yunlou.bole.model.entity.ResumesTemplateStyle;
import cn.net.yunlou.bole.service.ResumesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PreviewController {

    private final ResumesService resumesService;

    private final ResumesLayoutCalculator resumesLayoutCalculator;

    private final ResumesStyleCalculator resumesStyleCalculator;

    @GetMapping("/preview/{id}")
    public ModelAndView preview(@PathVariable Long id,
                                @RequestParam(defaultValue = "desktop") String device) {

        // 1. 获取简历数据
        Resumes resumes = resumesService.getById(id);
        if (resumes == null) {
            throw new ResourceNotFoundException("简历不存在");
        }

        // 2. 准备数据
        List<ResumesTemplateComponent> components = resumes.getComponents();
        ResumesTemplateStyle globalStyle = resumes.getGlobalStyle();
        ResumesTemplateLayout globalLayout = resumes.getGlobalLayout();

        // 3. 【核心】计算布局
        Map<String, Object> layoutData = resumesLayoutCalculator.calculateLayout(components, globalLayout, globalStyle);


        // 4.计算容器样式
        String layoutType = (String) layoutData.get("layoutType");
        Map<String, String> containerStyle = resumesStyleCalculator.getContainerStyle(globalStyle, layoutType);

        // 5.获取响应式样式
        Map<String, String> responsiveStyles = resumesLayoutCalculator.getResponsiveStyles();


        // 6. 创建 ModelAndView
        ModelAndView modelAndView = new ModelAndView("resumes/preview");

        // 7. 添加主要数据
        modelAndView.addObject("resumes", resumes);
        modelAndView.addObject("device", device);

        // 8. 添加布局相关数据
        modelAndView.addObject("layoutData", layoutData);
        modelAndView.addObject("globalStyle", globalStyle);
        modelAndView.addObject("globalLayout", globalLayout);
        modelAndView.addObject("responsiveStyles", responsiveStyles);
        modelAndView.addObject("responsiveStylesCss", StyleUtils.toCss(responsiveStyles));
        modelAndView.addObject("containerStyle", containerStyle);
        modelAndView.addObject("containerStyleCss", StyleUtils.toCss(containerStyle));

        log.info("ModelAndView:{}", JsonUtils.toJson(modelAndView));

        return modelAndView;
    }

    @GetMapping("/preview/{id}/mobile")
    public ModelAndView previewMobile(@PathVariable Long id) {
        return preview(id, "mobile");
    }

    @GetMapping("/preview/{id}/tablet")
    public ModelAndView previewTablet(@PathVariable Long id) {
        return preview(id, "tablet");
    }

    @GetMapping("/preview/{id}/desktop")
    public ModelAndView previewDesktop(@PathVariable Long id) {
        return preview(id, "desktop");
    }
}