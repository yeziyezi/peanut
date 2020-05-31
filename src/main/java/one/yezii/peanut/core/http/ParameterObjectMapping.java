package one.yezii.peanut.core.http;

import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

public class ParameterObjectMapping {
    private Map<Parameter, Object> map = new LinkedHashMap<>();

    public void add(Parameter parameter, Object value) {
        this.map.put(parameter, value);
    }

    public boolean containsNull() {
        return map.containsValue(null);
    }

    public Object[] toArray() {
        return map.values().toArray();
    }
}
