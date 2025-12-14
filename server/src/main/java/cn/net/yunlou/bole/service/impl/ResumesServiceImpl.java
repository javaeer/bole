package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.TemplateComponentType;
import cn.net.yunlou.bole.entity.Resumes;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.handler.ComponentDataPopulatorFactory;
import cn.net.yunlou.bole.mapper.ResumesMapper;
import cn.net.yunlou.bole.model.create.ResumesCreate;
import cn.net.yunlou.bole.model.edit.ResumesEdit;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import cn.net.yunlou.bole.model.view.ResumesView;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.struct.ResumesStructMapper;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FileName: ResumesServiceImpl Description: Created By MR. WANG Created At 2025/11/24 22:00
 * Modified By Modified At
 */
@Service
@RequiredArgsConstructor
public class ResumesServiceImpl
        extends BaseService<
                ResumesMapper,
                Resumes,
                ResumesCreate,
                ResumesView,
                ResumesEdit,
                ResumesQuery,
                ResumesStructMapper>
        implements ResumesService {

    private final ComponentDataPopulatorFactory populatorFactory;

    @Override
    @Cacheable(
            value = "resume:preview",
            key = "#entity.templateId+':'+#entity.userId",
            unless = "#result == null")
    public ResumesView preview(Resumes entity) {

        if (ObjectUtils.isNotEmpty(entity.getComponents())) {
            // 替换真实数据
            List<ResumesTemplateComponent> realComponents = Lists.newArrayList();

            for (ResumesTemplateComponent component : entity.getComponents()) {
                TemplateComponentType componentType =
                        IEnum.getEnumByValue(component.getComponent(), TemplateComponentType.class);
                if (ObjectUtils.isNotEmpty(componentType)) {
                    Map<String, Object> props = component.getProps();
                    if (ObjectUtils.isNotEmpty(props)) {
                        Map<String, Object> populated =
                                populatorFactory
                                        .getComponentDataPopulatorStrategy(componentType)
                                        .populate(entity.getUserId(), props);

                        component.setProps(populated);
                    }
                    realComponents.add(component);
                }
            }

            entity.setComponents(realComponents);
        }

        return structMapper.toView(entity);
    }

    @Override
    @Cacheable(value = "resume:view", key = "#id", unless = "#result == null")
    public ResumesView getViewById(Serializable id) {
        return super.getViewById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(
            value = {"resume:view"},
            key = "#entity.id",
            beforeInvocation = true)
    public boolean update(Resumes entity) {
        return super.update(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(
            value = {"resume:view"},
            key = "#entity.id",
            beforeInvocation = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(Resumes entity) {
        // 固化简历 结构
        if (ObjectUtils.isNotEmpty(entity.getComponents())) {
            // 替换真实数据
            List<ResumesTemplateComponent> realComponents = Lists.newArrayList();

            for (ResumesTemplateComponent component : entity.getComponents()) {
                TemplateComponentType componentType =
                        IEnum.getEnumByValue(component.getComponent(), TemplateComponentType.class);
                if (ObjectUtils.isNotEmpty(componentType)) {
                    Map<String, Object> props = component.getProps();
                    if (ObjectUtils.isNotEmpty(props)) {
                        Map<String, Object> populated =
                                populatorFactory
                                        .getComponentDataPopulatorStrategy(componentType)
                                        .populate(entity.getUserId(), props);

                        component.setProps(populated);
                    }
                    realComponents.add(component);
                }
            }

            entity.setComponents(realComponents);
        }
        return super.save(entity);
    }

    /*


    */
    /** 并行填充组件数据 */
    /*

    private void fillComponentsInParallel(Resumes resume) {
        List<CompletableFuture<Void>> futures = resume.getComponents().stream()
                .map(component -> CompletableFuture.runAsync(() -> {
                    TemplateComponentType type = IEnum.getEnumByValue(component.getComponent(), TemplateComponentType.class);

                    // 尝试从组件缓存获取
                    String componentKey = String.valueOf(resume.getTemplateId() + resume.getUserId());
                    Map<String, Object> cachedProps = redisCacheUtils.getObject(componentKey, Map.class);

                    if (cachedProps != null) {
                        component.setProps(cachedProps);
                    } else {
                        // 从服务获取并缓存
                        IComponentDataPopulatorStrategy dataPopulatorStrategy = populatorFactory.getComponentDataPopulatorStrategy(type);
                        Map<String, Object> props = dataPopulatorStrategy.populate(resume.getUserId(), component.getProps());
                        component.setProps(props);

                        // 异步缓存组件数据
                        redisCacheUtils.putObject(componentKey, props, getComponentTTL(type));
                    }
                }))
                .collect(Collectors.toList());

        // 等待所有组件填充完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }


        */
    /** 根据组件类型确定缓存时间 */
    /*

    private Long getComponentTTL(TemplateComponentType type) {
        switch (type) {
            case USER_BASIC_INFO:
                return Duration.ofMinutes(30).toMillis(); // 用户信息变化较少
            case JOB_INTENTION:
                return Duration.ofMinutes(15).toMillis(); // 求职意向可能变化
            case WORK_EXPERIENCE:
            case COMPANY_EXPERIENCE:
            case PROJECT_EXPERIENCE:
                return Duration.ofMinutes(60).toMillis(); // 经历相对稳定
            case EDUCATION_EXPERIENCE:
                return Duration.ofHours(24).toMillis(); // 教育经历很少变化
            default:
                return Duration.ofMinutes(30).toMillis();
        }
    }
    */

}
