package one.yezii.peanut.core.ioc;

import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.constant.HttpMethod;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.http.MethodInvoker;
import one.yezii.peanut.core.http.route.UriRoute;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrap;
import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrapVoid;

public class BeanManager {
    public void initBeans(String basePackage) {
        BeanDependencies beanDependencies = new BeanDependencies(basePackage);
        injectBeans(beanDependencies);
        registerBeans(beanDependencies);
    }

    private Map<UriRoute, MethodInvoker> getRouteMap(Map<String, Object> routerMap) {
        Map<UriRoute, MethodInvoker> routeMap = new HashMap<>();
        routerMap.forEach((routerName, bean) -> {
            String prefix = getPrefixRoute(bean);
            Arrays.stream(bean.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Route.class))
                    .filter(method -> method.canAccess(bean))//non-public method will not register in routeMap
                    .forEach(method -> {
                        Route route = method.getDeclaredAnnotation(Route.class);
                        MethodInvoker methodInvoker = MethodInvoker.of(method, routerName);
                        for (HttpMethod httpMethod : route.method()) {
                            UriRoute uriRoute = UriRoute.of(getFullRoute(prefix, route.value()), httpMethod.toString());
                            checkUriRouteAlreadyExist(routeMap, uriRoute);
                            routeMap.put(uriRoute, methodInvoker);
                        }
                    });
        });
        return routeMap;
    }

    private void checkUriRouteAlreadyExist(Map<UriRoute, MethodInvoker> routeMap, UriRoute uriRoute) {
        if (routeMap.containsKey(uriRoute)) {
            throw new RuntimeException("route[" + uriRoute.routeUri() + "] is duplicated.");
        }
    }

    private String getPrefixRoute(Object bean) {
        String prefix = bean.getClass().getAnnotation(Router.class).value();
        return prefix.startsWith("/") ? prefix : "/" + prefix;
    }

    private String getFullRoute(String prefix, String subRoute) {
        return prefix + (subRoute.startsWith("/") ? subRoute : "/" + subRoute);
    }

    private void registerBeans(BeanDependencies beanDependencies) {
        GlobalContext.beans.putAll(beanDependencies.getBeans());
        GlobalContext.runners.putAll(beanDependencies.getBeansWithInterface(PeanutRunner.class));
        GlobalContext.routeMap.putAll(getRouteMap(beanDependencies.getBeansWithAnnotation(Router.class)));
    }

    private List<String> getInjectOrders(BeanDependencies beanDependencies) {
        List<DependencyEndpoint> endpoints = beanDependencies.getBeanMap().entrySet().stream()
                .map(entry -> new DependencyEndpoint().setName(entry.getKey())
                        .addNext(entry.getValue().getDependencies()))
                .collect(Collectors.toList());
        return new DependencyForest().addEndpoints(endpoints).generate().getInjectOrders();
    }

    private Field setFieldAccessible(Field field) {
        field.setAccessible(true);
        return field;
    }

    private void injectBeans(BeanDependencies beanDependencies) {
        List<String> injectOrders = getInjectOrders(beanDependencies);
        Map<String, BeanContainer> beanMap = beanDependencies.getBeanMap();
        injectOrders.stream()
                .map(beanMap::get)
                .forEach(wrapVoid(beanContainer -> {
                    Object bean = beanContainer.getBeanClass().getDeclaredConstructors()[0].newInstance();
                    Arrays.stream(beanContainer.getBeanClass().getDeclaredFields())
                            .filter(field -> beanContainer.getDependencies().contains(field.getType().getName()))
                            .map(wrap(this::setFieldAccessible))
                            .forEach(wrapVoid(autowiredField ->
                                    autowiredField.set(bean, beanMap.get(autowiredField.getType().getName()).getBean())));
                    beanContainer.injectBean(bean);
                }));
    }
}
