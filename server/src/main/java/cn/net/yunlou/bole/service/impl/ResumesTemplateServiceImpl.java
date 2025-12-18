package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.entity.ResumesComponent;
import cn.net.yunlou.bole.entity.ResumesTemplate;
import cn.net.yunlou.bole.entity.ResumesTemplateComponent;
import cn.net.yunlou.bole.mapper.ResumesTemplateMapper;
import cn.net.yunlou.bole.model.create.ResumesTemplateComponentCreate;
import cn.net.yunlou.bole.model.create.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateView;
import cn.net.yunlou.bole.service.ResumesComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.struct.ResumesTemplateStructMapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * FileName: ResumesTemplateServiceImpl Description: Created By MR. WANG Created At 2025/11/24 23:54
 * Modified By Modified At
 */
@Service
@RequiredArgsConstructor
public class ResumesTemplateServiceImpl
        extends BaseService<
                ResumesTemplateMapper,
                ResumesTemplate,
                ResumesTemplateCreate,
                ResumesTemplateView,
                ResumesTemplateEdit,
                ResumesTemplateQuery,
                ResumesTemplateStructMapper>
        implements ResumesTemplateService {

    private final ResumesComponentService resumesComponentService;

    private final ResumesTemplateComponentService resumesTemplateComponentService;

    @Override
    public List<ResumesTemplate> getAllActiveTemplates() {
        ResumesTemplate entity = new ResumesTemplate();
        entity.setIsActive(Boolean.TRUE);
        return list(entity);
    }

    @Override
    public ResumesTemplate getByCode(String code) {

        ResumesTemplate entity = new ResumesTemplate();
        entity.setCode(code);
        return get(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveByCreate(ResumesTemplateCreate create) {

        ResumesTemplate toEntity = structMapper.createToEntity(create);
        boolean save = super.save(toEntity);

        if (save) {
            List<ResumesTemplateComponentCreate> componentCreates = create.getComponents();
            if (!CollectionUtils.isEmpty(componentCreates)) {
                ArrayList<@Nullable ResumesTemplateComponent> realComponents = Lists.newArrayList();

                for (ResumesTemplateComponentCreate resumesTemplateComponentCreate :
                        componentCreates) {
                    Long componentId = resumesTemplateComponentCreate.getComponentId();
                    ResumesComponent component = resumesComponentService.getById(componentId);
                    if (ObjectUtils.isNotEmpty(component)) {
                        ResumesTemplateComponent templateComponent =
                                ResumesTemplateComponent.builder()
                                        .templateId(toEntity.getId())
                                        .componentId(componentId)
                                        .key(component.getKey())
                                        .name(component.getName())
                                        .defaultConfig(component.getDefaultConfig())
                                        .props(resumesTemplateComponentCreate.getProps())
                                        .styles(resumesTemplateComponentCreate.getStyles())
                                        .build();

                        resumesTemplateComponentService.save(templateComponent);

                        realComponents.add(templateComponent);
                    }
                }
                toEntity.setComponents(realComponents);
            }
        }

        return save;
    }
}
