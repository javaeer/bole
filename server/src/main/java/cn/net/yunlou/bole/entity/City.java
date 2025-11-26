package cn.net.yunlou.bole.entity;

import cn.net.yunlou.bole.common.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: City
 * Description:
 * Created By MR. WANG
 * Created At 2025/11/25 21:57
 * Modified By
 * Modified At
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_city")
public class City extends BaseTreeEntity<City> {

    private String name;

    private String enName;

    /**
     * 首字母
     */
    private String initial;

    /**
     * 拼音
     * 简称的拼音 全拼 小写
     */
    private String pinyin;

    /**
     * 简称
     * 去掉所包含的以下汉字
     * 区/新区
     * 县
     * *族自治区/县
     */
    private String shortName;

    /**
     * 经度
     */
    private Double longitude;// 112.54,

    /**
     * 纬度
     */
    private Double latitude;// 37.75,


    private Long cityGradeId;

    @TableField(exist = false)
    private CityGrade cityGrade;

    /**
     * 天气站
     * 来源 中国天气网/高德地图 站点编码
     * https://m.weather.com.cn/mweather/101340201.shtml
     */
    private String cnwStationCode;

    /**
     * 中央气象台 站点编码
     * http://www.nmc.cn/publish/forecast/ABJ/changping.html
     */
    private String nmcStationCode;

    private String nmcProvinceCode;

    /**
     * 中国气象局 站点编码
     * https://weather.cma.cn/web/weather/54511.html
     */
    private String cmaStationCode;

}
