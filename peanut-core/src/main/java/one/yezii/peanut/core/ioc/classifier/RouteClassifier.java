package one.yezii.peanut.core.ioc.classifier;

import one.yezii.peanut.core.annotation.Json;
import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.constant.HttpMethod;
import one.yezii.peanut.core.http.MethodInvoker;
import one.yezii.peanut.core.http.route.UriRoute;
import one.yezii.peanut.core.ioc.BeanContainer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RouteClassifier implements Classifier<UriRoute, MethodInvoker> {
    @Override
    public Map<UriRoute, MethodInvoker> classify(List<BeanContainer> beanContainers) {
        Map<String, Object> routerMap = beanContainers.stream()
                .filter(bc -> bc.hasAnnotation(Router.class))
                .collect(Collectors.toMap(BeanContainer::name, BeanContainer::beanInstance));
        return getRouteMap(routerMap);
    }

    private void checkUriRouteAlreadyExist(Map<UriRoute, MethodInvoker> routeMap, UriRoute uriRoute) {
        if (routeMap.containsKey(uriRoute)) {
            throw new RuntimeException("route[" + uriRoute.routeUri() + "] is duplicated.");
        }
    }

    private Map<UriRoute, MethodInvoker> getRouteMap(Map<String, Object> routerMap) {
        Map<UriRoute, MethodInvoker> routeMap = new HashMap<>();
        routerMap.forEach((routerName, bean) -> {
            String prefix = getPrefixRoute(bean);
            boolean routerReturnJson = bean.getClass().isAnnotationPresent(Json.class);
            Arrays.stream(bean.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Route.class))
                    .filter(method -> method.canAccess(bean))//non-public method will not register in routeMap
                    .forEach(method -> {
                        Route route = method.getDeclaredAnnotation(Route.class);
                        MethodInvoker methodInvoker = MethodInvoker.of(method, routerName);
                        boolean routeReturnJson = routerReturnJson || method.isAnnotationPresent(Json.class);
                        for (HttpMethod httpMethod : route.method()) {
                            UriRoute uriRoute = UriRoute.of(getFullRoute(prefix, route.value()), httpMethod.toString());
                            checkUriRouteAlreadyExist(routeMap, uriRoute);
                            if (routeReturnJson) {
                                methodInvoker.setJsonResponse();
                            }
                            routeMap.put(uriRoute, methodInvoker);
                        }
                    });
        });
        return routeMap;
    }

    private String getPrefixRoute(Object bean) {
        String prefix = bean.getClass().getAnnotation(Router.class).value();
        return prefix.startsWith("/") ? prefix : "/" + prefix;
    }

    private String getFullRoute(String prefix, String subRoute) {
        return prefix + (subRoute.startsWith("/") ? subRoute : "/" + subRoute);
    }
}
