package cn.net.yunlou.bole.common.handler;

import cn.net.yunlou.bole.model.entity.ResumesTemplateComponent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({List.class})
public class JsonbTypeListHandler<T> extends BaseTypeHandler<List<T>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final JavaType javaType;
    private final Class<T> elementType;

    /**
     * 使用 TypeReference 构造函数
     */
    public JsonbTypeListHandler(TypeReference<List<T>> typeReference) {
        this.javaType = OBJECT_MAPPER.getTypeFactory().constructType(typeReference);
        this.elementType = extractElementType(typeReference.getType());
        if (log.isDebugEnabled()) {
            log.debug("JsonbTypeListHandler initialized for type: {}", javaType);
        }
    }

    /**
     * 使用 Class 构造函数，用于简单类型 List<String>, List<Integer> 等
     */
    public JsonbTypeListHandler(Class<T> elementType) {
        this.javaType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, elementType);
        this.elementType = elementType;
        if (log.isDebugEnabled()) {
            log.debug("JsonbTypeListHandler initialized for element type: {}", elementType);
        }
    }

    /**
     * 使用 Type 构造函数，用于更复杂的泛型场景
     */
    @SuppressWarnings("unchecked")
    public JsonbTypeListHandler(Type type) {
        this.javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
        this.elementType = (Class<T>) extractElementType(type);
        if (log.isDebugEnabled()) {
            log.debug("JsonbTypeListHandler initialized for type: {}", type);
        }
    }

    @SuppressWarnings("unchecked")
    private Class<T> extractElementType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) type;
            Type[] typeArgs = paramType.getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<T>) typeArgs[0];
            }
        }
        return (Class<T>) Object.class;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        try {
            pgObject.setValue(OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            log.error("Failed to serialize list to JSON", e);
            throw new SQLException("Failed to serialize list to JSON", e);
        }
        ps.setObject(i, pgObject);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseListFromResultSet(rs, columnName);
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseListFromResultSet(rs, columnIndex);
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return parseList(value);
    }

    private List<T> parseListFromResultSet(ResultSet rs, Object columnIdentifier) throws SQLException {
        Object value;
        if (columnIdentifier instanceof String) {
            value = rs.getObject((String) columnIdentifier);
        } else {
            value = rs.getObject((Integer) columnIdentifier);
        }
        return parseList(value);
    }

    @SuppressWarnings("unchecked")
    private List<T> parseList(Object value) {
        if (value == null) {
            return new ArrayList<>();
        }

        try {
            String jsonValue = null;

            // 处理PGobject
            if (value instanceof PGobject) {
                PGobject pgObject = (PGobject) value;
                jsonValue = pgObject.getValue();
            }
            // 处理字符串
            else if (value instanceof String) {
                jsonValue = (String) value;
            }
            // 其他类型直接尝试转换为字符串
            else {
                jsonValue = value.toString();
            }

            if (jsonValue == null || jsonValue.trim().isEmpty() || "null".equals(jsonValue)) {
                return new ArrayList<>();
            }

            // 使用JavaType进行反序列化
            return OBJECT_MAPPER.readValue(jsonValue, javaType);

        } catch (IOException e) {
            log.error("Failed to parse JSON list. Value: {}, Expected type: {}",
                    value, javaType, e);
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Unexpected error while parsing JSON list", e);
            return new ArrayList<>();
        }
    }

    /**
     * 工具方法：快速创建常见类型的处理器
     */
    public static <T> JsonbTypeListHandler<T> of(Class<T> elementType) {
        return new JsonbTypeListHandler<>(elementType);
    }

    public static <T> JsonbTypeListHandler<T> of(TypeReference<List<T>> typeReference) {
        return new JsonbTypeListHandler<>(typeReference);
    }

    /**
     * 静态工厂方法创建预定义处理器
     */
    public static class Factory {
        public static JsonbTypeListHandler<String> stringList() {
            return new JsonbTypeListHandler<>(String.class);
        }

        public static JsonbTypeListHandler<Integer> integerList() {
            return new JsonbTypeListHandler<>(Integer.class);
        }

        public static JsonbTypeListHandler<Long> longList() {
            return new JsonbTypeListHandler<>(Long.class);
        }

        public static JsonbTypeListHandler<Double> doubleList() {
            return new JsonbTypeListHandler<>(Double.class);
        }

        public static JsonbTypeListHandler<Boolean> booleanList() {
            return new JsonbTypeListHandler<>(Boolean.class);
        }

        @SuppressWarnings("rawtypes")
        public static JsonbTypeListHandler<Object> objectList() {
            return new JsonbTypeListHandler<>(Object.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<String>
     */
    public static class StringListHandler extends JsonbTypeListHandler<String> {
        public StringListHandler() {
            super(String.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<Integer>
     */
    public static class IntegerListHandler extends JsonbTypeListHandler<Integer> {
        public IntegerListHandler() {
            super(Integer.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<Long>
     */
    public static class LongListHandler extends JsonbTypeListHandler<Long> {
        public LongListHandler() {
            super(Long.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<Double>
     */
    public static class DoubleListHandler extends JsonbTypeListHandler<Double> {
        public DoubleListHandler() {
            super(Double.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<Boolean>
     */
    public static class BooleanListHandler extends JsonbTypeListHandler<Boolean> {
        public BooleanListHandler() {
            super(Boolean.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<Map<String, Object>>
     */
    public static class MapListHandler extends JsonbTypeListHandler<java.util.Map<String, Object>> {
        public MapListHandler() {
            super(new TypeReference<List<java.util.Map<String, Object>>>() {});
        }
    }

    /**
     * 静态内部类 - 处理 List<?>
     */
    public static class ObjectListHandler extends JsonbTypeListHandler<Object> {
        public ObjectListHandler() {
            super(Object.class);
        }
    }

    /**
     * 静态内部类 - 处理 List<ResumesTemplateComponent>
     */
    public static class ResumesTemplateComponentListHandler extends JsonbTypeListHandler<ResumesTemplateComponent> {
        public ResumesTemplateComponentListHandler() {
            super(ResumesTemplateComponent.class);
        }
    }

}