package one.yezii.peanut.core.context;

import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.http.MethodInvoker;
import one.yezii.peanut.core.http.route.UriRoute;

import java.util.HashMap;
import java.util.Map;

public class GlobalContext {
    public final static Map<String, Object> beans = new HashMap<>();
    public final static Map<String, PeanutRunner> runners = new HashMap<>();
    public final static Map<UriRoute, MethodInvoker> routeMap = new HashMap<>();
}
