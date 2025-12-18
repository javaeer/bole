package cn.net.yunlou.bole.common.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

@Slf4j
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonbTypeHandler extends AbstractJsonTypeHandler<Object> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final Class<?> type;
    private final TypeReference<?> typeReference;

    public JsonbTypeHandler(Class<?> type) {
        super(type);
        if (log.isTraceEnabled()) {
            log.trace("JsonbTypeHandler(" + type + ")");
        }
        this.type = type;
        this.typeReference =
                new TypeReference<>() {
                    @Override
                    public Class<?> getType() {
                        return type;
                    }
                };
    }

    @Override
    public Object parse(String json) {
        try {
            if (type == String.class) {
                return json;
            }
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON: " + json, e);
        }
    }

    @Override
    public String toJson(Object obj) {
        try {
            if (obj instanceof String) {
                return (String) obj;
            }
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize object to JSON: " + obj, e);
        }
    }

    @Override
    public void setNonNullParameter(
            PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(toJson(parameter));
        ps.setObject(i, pgObject);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        if (value instanceof PGobject) {
            return parse(((PGobject) value).getValue());
        }
        return parse(rs.getString(columnName));
    }

    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        if (value instanceof PGobject) {
            return parse(((PGobject) value).getValue());
        }
        return parse(rs.getString(columnIndex));
    }
}
