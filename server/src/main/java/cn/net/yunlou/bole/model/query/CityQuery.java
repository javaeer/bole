package cn.net.yunlou.bole.model.query;

import cn.net.yunlou.bole.common.BaseTreeQuery;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class CityQuery extends BaseTreeQuery {
    private String username;
    private String email;
    private Integer status;
    private Integer minAge;
    private Integer maxAge;
}
