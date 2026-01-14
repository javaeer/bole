package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

/**
 * 基础映射接口
 *
 * @param <T> 实体类型
 * @param <C> 创建类型
 * @param <E> 编辑类型
 * @param <V> 视图类型
 * @param <Q> 查询类型
 */
public interface BaseStructMapper<
        T extends BaseEntity,
        C extends BaseCreate,
        V extends BaseView,
        E extends BaseEdit,
        Q extends BaseQuery> {

    /** 基础实体拷贝 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    T copy(T entity);

    /** 实体转视图 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    V toView(T entity);

    /** 视图转实体 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    T viewToEntity(V view);

    /** 创建转实体 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    T createToEntity(C create);

    /** 编辑转实体 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    T editToEntity(E edit);

    /** 查询转实体（用于查询条件） */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    T queryToEntity(Q query);

    /** 实体列表转视图列表 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<V> toViews(List<T> entities);

    /** 视图列表转实体列表 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<T> viewsToEntities(List<V> views);

    /** 创建列表转实体列表 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<T> createsToEntities(List<C> creates);

    /** 编辑列表转实体列表 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    List<T> editsToEntities(List<E> edits);

    /** 实体分页转视图分页 */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    default Page<V> toViewPage(Page<T> page) {
        if (page == null) {
            return null;
        }
        Page<V> viewPage = new Page<>();
        viewPage.setRecords(toViews(page.getRecords()));
        viewPage.setTotal(page.getTotal());
        viewPage.setSize(page.getSize());
        viewPage.setCurrent(page.getCurrent());
        viewPage.setPages(page.getPages());

        return viewPage;
    }

    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(T source, @MappingTarget T target);

    /** 更新实体（忽略null值） */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromEdit(E edit, @MappingTarget T entity);

    /** 从查询更新实体（用于构建查询条件） */
    @BeanMapping(
            unmappedTargetPolicy = ReportingPolicy.IGNORE,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromQuery(Q query, @MappingTarget T entity);
}
