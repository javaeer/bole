package cn.net.yunlou.bole.common.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
public class JsonbTypeHandler<T> extends AbstractJsonTypeHandler<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final TypeReference<T> typeReference;
    private final Class<T> rawType;

    public JsonbTypeHandler(Class<T> type) {
        super(type);
        if (log.isTraceEnabled()) {
            log.trace("JsonbTypeHandler(" + type + ")");
        }
        this.rawType = type;
        this.typeReference = createTypeReference(type);
    }

    @SuppressWarnings("unchecked")
    public JsonbTypeHandler(Type type) {
        super(getRawType(type));
        if (log.isTraceEnabled()) {
            log.trace("JsonbTypeHandler(Type: " + type + ")");
        }
        this.rawType = (Class<T>) getRawType(type);
        this.typeReference = createTypeReference(type);
    }

    private static Class<?> getRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type);
        }
    }

    private TypeReference<T> createTypeReference(Type type) {
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        };
    }

    @Override
    public T parse(String json) {
        try {
            if (rawType == String.class) {
                return rawType.cast(json);
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
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(toJson(parameter));
        ps.setObject(i, pgObject);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        if (value instanceof PGobject) {
            return parse(((PGobject) value).getValue());
        }
        return parse(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        if (value instanceof PGobject) {
            return parse(((PGobject) value).getValue());
        }
        return parse(rs.getString(columnIndex));
    }
}
