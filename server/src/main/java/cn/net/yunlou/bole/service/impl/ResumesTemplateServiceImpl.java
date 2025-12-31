package cn.net.yunlou.bole.service.impl;

import cn.net.yunlou.bole.common.BaseService;
import cn.net.yunlou.bole.model.entity.ResumesTemplate;
import cn.net.yunlou.bole.mapper.ResumesTemplateMapper;
import cn.net.yunlou.bole.model.ResumesTemplateComponentDTO;
import cn.net.yunlou.bole.model.create.ResumesTemplateCreate;
import cn.net.yunlou.bole.model.edit.ResumesTemplateEdit;
import cn.net.yunlou.bole.model.query.ResumesTemplateQuery;
import cn.net.yunlou.bole.model.view.ResumesTemplateView;
import cn.net.yunlou.bole.service.ResumesComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateComponentService;
import cn.net.yunlou.bole.service.ResumesTemplateService;
import cn.net.yunlou.bole.struct.ResumesTemplateStructMapper;
import java.io.Serializable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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
            List<ResumesTemplateComponentDTO> componentCreates = create.getComponents();
            if (!CollectionUtils.isEmpty(componentCreates)) {
                resumesTemplateComponentService.bindBatch(toEntity, componentCreates);
            }
        }
        return save;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByEdit(ResumesTemplateEdit edit) {
        boolean b = super.updateByEdit(edit);
        if (b) {
            ResumesTemplate resumesTemplate = getById(edit.getId());
            List<ResumesTemplateComponentDTO> editComponents = edit.getComponents();
            if (ObjectUtils.isNotEmpty(editComponents)) {
                resumesTemplateComponentService.syncBatch(resumesTemplate, editComponents);
            }
        }
        return b;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean b = super.removeById(id);
        if (b) {
            ResumesTemplate resumesTemplate = ResumesTemplate.builder().build();
            resumesTemplate.setId((Long) id);
            resumesTemplateComponentService.clean(resumesTemplate, null);
        }
        return b;
    }
}
