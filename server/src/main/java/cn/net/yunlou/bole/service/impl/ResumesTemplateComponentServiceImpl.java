package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.MultiService;
import cn.net.yunlou.bole.mapper.ResumesTemplateComponentMapper;
import cn.net.yunlou.bole.model.ResumesTemplateComponentDTO;
import cn.net.yunlou.bole.model.entity.ResumesComponent;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.service.ResumesComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * FileName: ResumesTemplateComponentServiceImpl Description: Created By laughtiger Created At
 * 2025/12/18 22:01 Modified By Modified At
 */
@Service
@RequiredArgsConstructor
public class ResumesTemplateComponentServiceImpl
        extends MultiService<
                ResumesTemplateComponentMapper,
                ResumesTemplateComponent,
                ResumesTemplate,
                ResumesComponent>
        implements ResumesTemplateComponentService {

    private final ResumesComponentService resumesComponentService;

    @Override
    public ResumesTemplateComponent createEntity(ResumesTemplate left, ResumesComponent right) {
        return ResumesTemplateComponent.builder()
                .templateId(
                        (ObjectUtils.isNotEmpty(left) && ObjectUtils.isNotEmpty(left.getId()))
                                ? left.getId()
                                : null)
                .componentId(
                        (ObjectUtils.isNotEmpty(right) && ObjectUtils.isNotEmpty(right.getId()))
                                ? right.getId()
                                : null)
                .key(
                        (ObjectUtils.isNotEmpty(right) && ObjectUtils.isNotEmpty(right.getKey()))
                                ? right.getKey()
                                : null)
                .name(
                        (ObjectUtils.isNotEmpty(right) && ObjectUtils.isNotEmpty(right.getName()))
                                ? right.getName()
                                : null)
                .defaultConfig(
                        (ObjectUtils.isNotEmpty(right)
                                        && ObjectUtils.isNotEmpty(right.getDefaultConfig()))
                                ? right.getDefaultConfig()
                                : null)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindBatch(ResumesTemplate left, List<ResumesTemplateComponentDTO> dtoList) {

        if (CollectionUtils.isEmpty(dtoList)) {
            return;
        }

        // 批量获取所有组件信息，避免循环中多次查询
        List<Long> componentIds =
                dtoList.stream()
                        .map(ResumesTemplateComponentDTO::getComponentId)
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList();

        if (CollectionUtils.isEmpty(componentIds)) {
            return;
        }

        Map<Long, ResumesComponent> componentMap =
                resumesComponentService.listByIds(componentIds).stream()
                        .collect(Collectors.toMap(ResumesComponent::getId, Function.identity()));

        // 批量保存，减少数据库交互
        List<ResumesTemplateComponent> entities =
                dtoList.stream()
                        .map(
                                dto -> {
                                    ResumesComponent component =
                                            componentMap.get(dto.getComponentId());
                                    if (component == null) {
                                        return null;
                                    }
                                    ResumesTemplateComponent entity = createEntity(left, component);
                                    entity.setProps(dto.getProps());
                                    entity.setStyles(dto.getStyles());
                                    return entity;
                                })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        if (!entities.isEmpty()) {
            saveBatch(entities);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncBatch(ResumesTemplate left, List<ResumesTemplateComponentDTO> dtoList) {
        clean(left, null);
        bindBatch(left, dtoList);
    }
}
