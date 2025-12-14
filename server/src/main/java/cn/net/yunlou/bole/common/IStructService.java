package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;

/**
 * FileName: IStructService Description: MapStruct 映射服务接口 Created By MR. WANG Created At 2025/11/25
 * 23:08 Modified By Modified At
 */
public interface IStructService<
        T extends BaseEntity,
        C extends BaseCreate,
        V extends BaseView,
        E extends BaseEdit,
        Q extends BaseQuery> {

    /** 根据ID获取DTO */
    V getViewById(Serializable id);

    /** 保存DTO */
    boolean saveByCreate(C create);

    /** 更新DTO */
    boolean updateByEdit(E edit);

    /** 删除DTO */
    boolean removeByQuery(Q query);

    /** 条件分页查询 */
    Page<T> pageByQuery(long pageNum, long pageSize, Q query);

    Page<V> pageViewByQuery(long pageNum, long pageSize, Q query);

    /** 条件列表查询 */
    List<T> listByQuery(Q query);

    List<V> listViewByQuery(Q query);
}
