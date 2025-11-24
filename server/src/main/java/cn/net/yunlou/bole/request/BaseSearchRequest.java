package cn.net.yunlou.bole.request;

import cn.net.yunlou.bole.common.BaseModel;
import lombok.Data;

/**
 * FileName: BaseSearchRequest
 * Description:
 * Created By laughtiger
 * Created At 2025/11/23 10:54
 * Modified By
 * Modified At
 */
@Data
public class BaseSearchRequest extends BaseModel {

    /**
     * 关键字
     */
    protected String keyWords;

    /**
     * 查询 字段
     */
    protected String keyField = "name";
}
