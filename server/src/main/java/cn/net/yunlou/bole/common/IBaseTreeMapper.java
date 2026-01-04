package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface IBaseTreeMapper<T extends BaseTreeEntity<T>> extends BaseMapper<T> {

    /** 根据路径更新子节点路径 */
    @Update(
            "UPDATE ${tableName} "
                    + "SET path = CONCAT(#{newParentPath}, SUBSTRING(path, LENGTH(#{oldParentPath}) + 1)) "
                    + "WHERE path LIKE CONCAT(#{oldParentPath}, '/%')")
    int updateChildrenPath(
            @Param("tableName") String tableName,
            @Param("oldParentPath") String oldParentPath,
            @Param("newParentPath") String newParentPath);
}
