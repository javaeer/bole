package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * FileName: IMultiService Description: Created By MR. WANG Created At 2025/11/19 15:28 Modified
 * By Modified At
 */
public interface IMultiService<
                T extends MultiEntity<L, R>, L extends BaseEntity, R extends BaseEntity>
        extends IService<T> {

    long countLeft(T entity);

    long countRight(T entity);

    List<L> listLeft(T entity);

    List<R> listRight(T entity);

    Page<L> pageLeft(long pageNum, long pageSize, T entity);

    Page<R> pageRight(long pageNum, long pageSize, T entity);

    boolean bind(L left, R right);

    boolean unbind(L left, R right);

    boolean sync(L left, List<R> rights);
}
