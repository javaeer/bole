package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import java.util.List;

/**
 * FileName: IStructService Description: MapStruct 映射服务接口 Created By MR. WANG Created At 2025/11/25
 * 23:08 Modified By Modified At
 */
public interface IStructService<D, Q> {

    /** 根据ID获取DTO */
    D getDTOById(Serializable id);

    /** 保存DTO */
    boolean saveByDTO(D dto);

    /** 更新DTO */
    boolean updateDTO(D dto);

    /** 删除DTO */
    boolean removeDTO(D dto);

    /** 条件分页查询 */
    Page<D> pageDTOByQuery(long pageNum, long pageSize, Q query);

    /** 条件列表查询 */
    List<D> listDTOByQuery(Q query);
}
