package cn.net.yunlou.bole.model;

import cn.net.yunlou.bole.common.BaseTreeQuery;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = false)
public class CityQuery extends BaseTreeQuery {
    private String username;
    private String email;
    private Integer status;
    private Integer minAge;
    private Integer maxAge;
}
