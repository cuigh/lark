package lark.core.codec;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lark.core.util.Exceptions;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.*;

/**
 * @author cuigh
 */
public final class JsonCodec {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.disable(WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(WRITE_DURATIONS_AS_TIMESTAMPS);
        MAPPER.disable(FAIL_ON_EMPTY_BEANS);
        MAPPER.disable(FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.registerModule(new JavaTimeModule());
    }

    private JsonCodec() {
        // 防止实例化
    }

    public static String encode(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static <T> T decode(String value, Class<T> clazz) {
        try {
            return MAPPER.readValue(value, clazz);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static <T> T decode(String value, Type type) {
        try {
            JavaType javaType = MAPPER.getTypeFactory().constructType(type);
            return MAPPER.readValue(value, javaType);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static <T> T decode(String value, TypeReference<T> type) {
        try {
            return MAPPER.readValue(value, type);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static <T> List<T> decodeList(String value, Class<T> clazz) {
        try {
            CollectionType type = MAPPER.getTypeFactory().constructCollectionType(ArrayList.class, clazz);
            return MAPPER.readValue(value, type);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static Map<String, Object> decodeMap(String value) {
        try {
            MapHolder holder = MAPPER.readValue(value, MapHolder.class);
            return holder.getMap();
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    public static <V> Map<String, V> decodeMap(String value, Class<V> clazz) {
        try {
            MapType type = MAPPER.getTypeFactory().constructMapType(HashMap.class, String.class, clazz);
            return MAPPER.readValue(value, type);
        } catch (Exception e) {
            throw Exceptions.asRuntime(e);
        }
    }

    private static class MapHolder {
        private Map<String, Object> map = new HashMap<>();

        @JsonAnyGetter
        public Map<String, Object> getMap() {
            return map;
        }

        @JsonAnySetter
        public void setMap(String name, Object value) {
            map.put(name, value);
        }
    }
}
