package cn.net.yunlou.bole.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * FileName: KeyFieldEnum Description: 查询字段枚举 接口 Created By MR. WANG Created At 2025/11/23 22:55
 * Modified By Modified At
 */
public interface KeyFieldEnum<T extends String> extends IEnum<T> {

    /** 应用查询条件 */
    <T> void applyQuery(QueryWrapper<T> wrapper, String keywords);
}
