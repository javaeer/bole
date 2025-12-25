package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FileName: MultiMapper Description: Created By MR. WANG Created At 2025/11/19 15:34 Modified By
 * Modified At
 */
public interface IMultiMapper<T, L, R> extends BaseMapper<T> {

    //合并后，写一个 XML 方法，通过方法重载支持两种调用
    List<L> selectListLeft(@Param("et") T entity);

    Page<L> selectListLeft(Page<L> page, @Param("et") T entity);

    List<R> selectListRight(@Param("et") T entity);

    Page<R> selectListRight(Page<R> page, @Param("et") T entity);
}
