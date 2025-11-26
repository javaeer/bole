package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CityQuery extends BaseQuery {
    private String username;
    private String email;
    private Integer status;
    private Integer minAge;
    private Integer maxAge;
}