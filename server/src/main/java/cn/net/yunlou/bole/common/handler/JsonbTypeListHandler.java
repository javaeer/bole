package cn.net.yunlou.bole.common.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

@Slf4j
@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes({List.class})
public class JsonbTypeListHandler extends BaseTypeHandler<List<?>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final TypeReference<?> typeReference;

    /** 处理 List<String> */
    public static class StringListHandler extends JsonbTypeListHandler {
        public StringListHandler() {
            super(new TypeReference<List<String>>() {});
        }
    }

    /** 处理 List<Integer> */
    public static class IntegerListHandler extends JsonbTypeListHandler {
        public IntegerListHandler() {
            super(new TypeReference<List<Integer>>() {});
        }
    }

    /** 处理 List<Long> */
    public static class LongListHandler extends JsonbTypeListHandler {
        public LongListHandler() {
            super(new TypeReference<List<Long>>() {});
        }
    }

    /** 处理 List<Map<String, Object>> */
    public static class MapListHandler extends JsonbTypeListHandler {
        public MapListHandler() {
            super(new TypeReference<List<java.util.Map<String, Object>>>() {});
        }
    }

    /** 自定义类型的 List 处理器 */
    public JsonbTypeListHandler(TypeReference<?> typeReference) {
        this.typeReference = typeReference;
        if (log.isDebugEnabled()) {
            log.debug(
                    "JsonbTypeListHandler initialized for type: {}",
                    typeReference.getType().getTypeName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setNonNullParameter(
            PreparedStatement ps, int i, List<?> parameter, JdbcType jdbcType) throws SQLException {
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

    @SuppressWarnings("unchecked")
    @Override
    public List<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseListFromResultSet(rs, columnName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseListFromResultSet(rs, columnIndex);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return parseList(value);
    }

    @SuppressWarnings("unchecked")
    private List<?> parseListFromResultSet(ResultSet rs, Object columnIdentifier)
            throws SQLException {
        Object value;
        if (columnIdentifier instanceof String) {
            value = rs.getObject((String) columnIdentifier);
        } else {
            value = rs.getObject((Integer) columnIdentifier);
        }
        return parseList(value);
    }

    @SuppressWarnings("unchecked")
    private List<?> parseList(Object value) {
        if (value == null) {
            return new ArrayList<>();
        }

        try {
            // 处理PGobject
            if (value instanceof PGobject) {
                PGobject pgObject = (PGobject) value;
                String jsonValue = pgObject.getValue();
                if (jsonValue == null || jsonValue.trim().isEmpty() || "null".equals(jsonValue)) {
                    return new ArrayList<>();
                }
                return (List<?>) OBJECT_MAPPER.readValue(jsonValue, typeReference);
            }

            // 处理字符串
            if (value instanceof String) {
                String strValue = (String) value;
                if (strValue.trim().isEmpty() || "null".equals(strValue)) {
                    return new ArrayList<>();
                }
                return (List<?>) OBJECT_MAPPER.readValue(strValue, typeReference);
            }

            // 其他类型直接返回空列表
            log.warn("Unsupported type for JSONB list: {}", value.getClass());
            return new ArrayList<>();

        } catch (IOException e) {
            log.error("Failed to parse JSON list", e);
            return new ArrayList<>();
        }
    }
}
