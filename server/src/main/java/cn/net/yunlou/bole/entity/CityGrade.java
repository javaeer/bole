package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_city_grade")
public class CityGrade extends BaseEntity {

    private String name;

    private String level; // 洲，国，省，市，区/县,乡镇，街道
}
