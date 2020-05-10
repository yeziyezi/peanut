package one.yezii.peanut.core.ioc;

import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    private Map<String, Method> getRoutMap(Map<String, Object> routerMap) {
        Map<String, Method> routeMap = new HashMap<>();
        routerMap.forEach((name, bean) -> {
            String prefix = bean.getClass().getAnnotation(Router.class).value();
            Arrays.stream(bean.getClass().getMethods())
                    .filter(method -> method.isAnnotationPresent(Route.class))
                    .filter(method -> method.canAccess(bean))//non-public method will not register in routeMap
                    .forEach(method -> {
                        String routeString = prefix + method.getDeclaredAnnotation(Route.class).value();
                        if (routeString.startsWith("//")) {
                            routeString = routeString.replaceFirst("//", "/");
                        } else if (!routeString.startsWith("/")) {
                            routeString = "/" + routeString;
                        } else {
                            //todo: other handling
                        }
                        if (routeMap.containsKey(routeString)) {
                            throw new RuntimeException("");
                        }
                        routeMap.put(routeString, method);
                    });
        });
        return routeMap;
    }

    private void registerBeans(BeanDependencies beanDependencies) {
        GlobalContext.beans.putAll(beanDependencies.getBeans());
        GlobalContext.runners.putAll(beanDependencies.getBeansWithInterface(PeanutRunner.class));
        GlobalContext.routeMap.putAll(getRoutMap(beanDependencies.getBeansWithAnnotation(Router.class)));
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
