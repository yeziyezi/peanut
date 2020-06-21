package one.yezii.peanut.core.ioc;

import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.http.MethodInvoker;
import one.yezii.peanut.core.http.route.UriRoute;

import java.util.HashMap;
import java.util.Map;

public interface BeanRepository {
    Map<String, PeanutRunner> runners = new HashMap<>();
    Map<String, Object> beans = new HashMap<>();
    Map<UriRoute, MethodInvoker> routes = new HashMap<>();
}
