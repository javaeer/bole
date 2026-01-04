package cn.net.yunlou.bole.model.entity;

import cn.net.yunlou.bole.common.BaseTreeEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * FileName: City Description: Created By MR. WANG Created At 2025/11/25 21:57 Modified By Modified
 * At
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_city")
public class City extends BaseTreeEntity<City> {

    // 直接使用 编码作为主键 中国区号均为数字
    // private String code;
    // private Long id;

    private String name;

    /** 简称 省、直辖市 京、津、冀； 区县去掉所包含的以下汉字 区/新区 县 *族自治区/县 */
    private String shortName;

    /** 首字母 */
    private String initial;

    /** 拼音 简称的拼音 全拼 小写 */
    private String pinyin;

    /** 拼音 简称的拼音 简拼 小写 */
    private String jianpin;

    /** 经度 */
    private Double longitude; // 112.54,

    /** 纬度 */
    private Double latitude; // 37.75,

    /** 电话区号 */
    private String telCode;

    /** 邮政编码 */
    private String zipCode;

    /** 车牌编码 京 A */
    private String carCode;

    /** 天气站 来源 中国天气网/高德地图 站点编码 https://m.weather.com.cn/mweather/101340201.shtml */
    private String cnwStationCode;

    /** 中央气象台 站点编码 http://www.nmc.cn/publish/forecast/ABJ/changping.html */
    private String nmcStationCode;

    private String nmcProvinceCode;

    private String nmcWeatherUrl;

    /** 中国气象局 站点编码 https://weather.cma.cn/web/weather/54511.html */
    private String cmaStationCode;
}
