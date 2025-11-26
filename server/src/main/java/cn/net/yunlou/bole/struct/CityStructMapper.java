package cn.net.yunlou.bole.struct;

import cn.net.yunlou.bole.common.BaseStructMapper;
import cn.net.yunlou.bole.model.dto.CityDTO;
import cn.net.yunlou.bole.model.query.CityQuery;
import cn.net.yunlou.bole.entity.City;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.*;

import java.util.List;

/**
 * 映射器
 */
@Mapper(componentModel = "spring")
public interface CityStructMapper extends BaseStructMapper<City, CityDTO, CityQuery> {

    @Override
    @Mapping(target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    CityDTO toDTO(City city);

    @Override
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    City toEntity(CityDTO dto);

    @Override
    List<CityDTO> toDTOList(List<City> cityList);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromQuery(CityQuery query, @MappingTarget City entity);

    @Override
    List<City> toEntityList(List<CityDTO> dtoList);

    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    City queryToEntity(CityQuery queryDTO);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    void updateEntityFromDTO(CityDTO dto, @MappingTarget City entity);

    /**
     * 具体的分页转换实现 - 解决泛型问题
     */
    default Page<CityDTO> toDTOPage(Page<City> page) {
        return BaseStructMapper.super.toDTOPage(page);
    }

    /**
     * 自定义映射方法 - 状态码转状态名称
     */
    @Named("statusToName")
    default String statusToName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "禁用";
            case 1: return "启用";
            default: return "未知";
        }
    }

    /**
     * 自定义映射方法 - 处理特殊字段转换
     */
    @Named("areaCodeFormat")
    default String areaCodeFormat(String areaCode) {
        if (areaCode == null) return null;
        // 示例：将 010 转换为 +86-010
        return "+86-" + areaCode;
    }
}