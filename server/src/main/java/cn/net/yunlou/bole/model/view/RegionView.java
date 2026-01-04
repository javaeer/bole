package cn.net.yunlou.bole.model.view;

import cn.net.yunlou.bole.common.BaseTreeView;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * FileName: 
 * Description:
 * Created By laughtiger
 * Created At 2026/1/2 23:10
 * Modified By
 * Modified At
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegionView extends BaseTreeView<RegionView> {

    private String name;

    /**
     * 简称 省、直辖市 京、津、冀； 区县去掉所包含的以下汉字 区/新区 县 *族自治区/县
     */
    private String shortName;

    /**
     * 首字母
     */
    private String initial;

    /**
     * 拼音 简称的拼音 全拼 小写
     */
    private String pinyin;

    /**
     * 拼音 简称的拼音 简拼 小写
     */
    private String jianpin;

    /**
     * 经度
     */
    private Double longitude; // 112.54,

    /**
     * 纬度
     */
    private Double latitude; // 37.75,

    /**
     * 电话区号
     */
    private Integer telCode;

    /**
     * 邮政编码
     */
    private Integer zipCode;

    /**
     * 车牌编码 京 A
     */
    private String carCode;


    /**
     * 天气站 来源 中国天气网/高德地图 站点编码 https://m.weather.com.cn/mweather/101340201.shtml
     */
    private String cnwStationCode;

    /**
     * 中央气象台 站点编码 http://www.nmc.cn/publish/forecast/ABJ/changping.html
     */
    private String nmcStationCode;

    private String nmcProvinceCode;

    /**
     * 中国气象局 站点编码 https://weather.cma.cn/web/weather/54511.html
     */
    private String cmaStationCode;

    // 关联属性（非数据库字段）
    private String parentName;

    private Integer childrenCount;
}
