package one.yezii.peanut.core.context;

import one.yezii.peanut.core.facade.PeanutRunner;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GlobalContext {
    public final static Map<String, Object> beans = new HashMap<>();
    public final static Map<String, PeanutRunner> runners = new HashMap<>();
    public final static Map<String, Method> routeMap = new HashMap<>();
    public final static Map<String, String> configMap = new HashMap<>();
}
