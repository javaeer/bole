package cn.net.yunlou.bole.model.dto;

import cn.net.yunlou.bole.common.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CityDTO extends BaseDTO {
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private Integer age;
    private String address;
}