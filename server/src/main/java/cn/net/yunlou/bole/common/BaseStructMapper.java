package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * 基础映射接口
 *
 * @param <T> 实体类型
 * @param <D> DTO类型
 * @param <Q> 查询DTO类型
 */
public interface BaseStructMapper<T extends BaseEntity, D extends BaseDTO, Q extends BaseQuery> {

    /** 实体转DTO */
    D toDTO(T entity);

    /** DTO转实体 */
    T toEntity(D dto);

    /** 查询DTO转实体（用于查询条件） */
    T queryToEntity(Q query);

    /** 实体列表转DTO列表 */
    List<D> toDTOList(List<T> entityList);

    /** DTO列表转实体列表 */
    List<T> toEntityList(List<D> dtoList);

    default Page<D> toDTOPage(Page<T> page) {
        if (page == null) {
            return null;
        }
        Page<D> dtoPage = new Page<>();
        dtoPage.setRecords(toDTOList(page.getRecords()));
        dtoPage.setTotal(page.getTotal());
        dtoPage.setSize(page.getSize());
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setPages(page.getPages());

        return dtoPage;
    }

    /** 更新实体（忽略null值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(D dto, @MappingTarget T entity);

    /** 从查询DTO更新实体（用于构建查询条件） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromQuery(Q query, @MappingTarget T entity);
}
