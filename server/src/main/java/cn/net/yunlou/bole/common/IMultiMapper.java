package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * FileName: MultiMapper Description: Created By MR. WANG Created At 2025/11/19 15:34 Modified By
 * Modified At
 */
public interface IMultiMapper<T, L, R> extends BaseMapper<T> {

    long selectCountLeft(@Param("et") T entity);

    long selectCountRight(@Param("et") T entity);

    List<L> selectListLeft(@Param("et") T entity);

    Page<L> selectPageLeft(Page<L> page, @Param("et") T entity);

    List<R> selectListRight(@Param("et") T entity);

    Page<R> selectPageRight(Page<R> page, @Param("et") T entity);
}
