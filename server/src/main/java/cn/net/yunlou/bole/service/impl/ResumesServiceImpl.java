package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.common.BusinessException;
import cn.net.yunlou.bole.common.BusinessStatus;
import cn.net.yunlou.bole.common.IEnum;
import cn.net.yunlou.bole.common.constant.TemplateComponentKey;
import cn.net.yunlou.bole.common.utils.SecurityContextUtils;
import cn.net.yunlou.bole.handler.ComponentDataPopulatorFactory;
import cn.net.yunlou.bole.mapper.ResumesMapper;
import cn.net.yunlou.bole.model.create.ResumesCreate;
import cn.net.yunlou.bole.model.edit.ResumesEdit;
import cn.net.yunlou.bole.model.entity.Resumes;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.model.query.ResumesQuery;
import cn.net.yunlou.bole.model.view.ResumesView;
import cn.net.yunlou.bole.service.ResumesService;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.struct.ResumesStructMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.springframework.util.CollectionUtils;

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

    private final ResumesTemplateService resumesTemplateService;

    private final ResumesTemplateComponentService resumesTemplateComponentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(ResumesCreate create) {
        Resumes entity = structMapper.createToEntity(create);
        entity.setUserId(SecurityContextUtils.getCurrentUserId());
        return save(entity);
    }

    @Override
    @Cacheable(
            value = "resume:preview",
            key = "#entity.templateId+':'+#entity.userId",
            unless = "#result == null")
    public ResumesView preview(Resumes entity) {

        ResumesTemplate template = resumesTemplateService.getById(entity.getTemplateId());
        if (ObjectUtils.isEmpty(template)) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "模板不存在");
        }

        entity.setGlobalLayout(template.getGlobalLayout());

        entity.setGlobalStyle(template.getGlobalStyle());

        List<ResumesTemplateComponent> components =
                resumesTemplateComponentService.list(
                        ResumesTemplateComponent.builder().templateId(template.getId()).build());
        if (CollectionUtils.isEmpty(components)) {
            throw new BusinessException(BusinessStatus.NOT_FOUND_RECORD, "模板未绑定组件");
        }

        // 替换真实数据
        List<ResumesTemplateComponent> realComponents = Lists.newArrayList();

        for (ResumesTemplateComponent component : components) {
            TemplateComponentKey componentType =
                    IEnum.valueOf(component.getKey(), TemplateComponentKey.class);
            if (ObjectUtils.isNotEmpty(componentType)) {
                Map<String, Object> props = component.getProps();
                if (ObjectUtils.isNotEmpty(props)) {

                    Map<String, Object> populated =
                            populatorFactory
                                    .getComponentDataPopulatorStrategy(componentType)
                                    .populate(entity.getUserId(), props);
                    component.setProps(populated);
                }
            }
            realComponents.add(component);
        }

        entity.setComponents(realComponents);

        return structMapper.toView(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(
            value = {"resume:view"},
            key = "#id",
            beforeInvocation = true)
    public Boolean viewById(Long id) {
        Resumes entity = getById(id);
        entity.setViewCount(entity.getViewCount() + 1);
        return updateById(entity);
    }

    @Override
    // @Cacheable(value = "resume:view", key = "#id", unless = "#result == null")
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
            key = "#id",
            beforeInvocation = true)
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public QueryWrapper<Resumes> getBaseQueryWrapper(Resumes entity) {

        QueryWrapper<Resumes> queryWrapper = super.getBaseQueryWrapper(entity);
        queryWrapper.lambda().eq(Resumes::getUserId, SecurityContextUtils.getCurrentUserId());
        return queryWrapper;
    }
}
