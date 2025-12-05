package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.mapstruct.*;

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

    /** 实体转视图 */
    V toView(T entity);

    /** 视图转实体 */
    T viewToEntity(V view);

    /** 创建转实体 */
    T createToEntity(C create);

    /** 编辑转实体 */
    T editToEntity(E edit);

    /** 查询转实体（用于查询条件） */
    T queryToEntity(Q query);

    /** 实体列表转视图列表 */
    List<V> toViews(List<T> entities);

    /** 视图列表转实体列表 */
    List<T> viewsToEntities(List<V> views);

    /** 创建列表转实体列表 */
    List<T> createsToEntities(List<C> creates);

    /** 编辑列表转实体列表 */
    List<T> editsToEntities(List<E> edits);

    /** 实体分页转视图分页 */
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

    /** 更新实体（忽略null值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromEdit(E edit, @MappingTarget T entity);

    /** 从查询更新实体（用于构建查询条件） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromQuery(Q query, @MappingTarget T entity);
}
