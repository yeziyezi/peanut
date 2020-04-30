package one.yezii.peanut.core.ioc;

import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanManager {

    public void initBeans(String basePackage) {
        BeanDependencies beanDependencies = new BeanDependencies(basePackage);
        injectBeans(beanDependencies);
        registerBeans(beanDependencies);
    }

    private void registerBeans(BeanDependencies beanDependencies) {
        GlobalContext.beans.putAll(beanDependencies.getBeans());
        GlobalContext.runners.putAll(beanDependencies.getBeansWithInterface(PeanutRunner.class));
    }

    private List<String> getInjectOrders(BeanDependencies beanDependencies) {
        List<DependencyEndpoint> endpoints = beanDependencies.getBeanMap().entrySet().stream()
                .map(entry -> new DependencyEndpoint().setName(entry.getKey())
                        .addNext(entry.getValue().getDependencies()))
                .collect(Collectors.toList());
        return new DependencyForest().addEndpoints(endpoints).generate().getInjectOrders();
    }

    private void injectBeans(BeanDependencies beanDependencies) {
        List<String> injectOrders = getInjectOrders(beanDependencies);
        Map<String, BeanContainer> beanMap = beanDependencies.getBeanMap();
        try {
            for (String key : injectOrders) {
                BeanContainer beanContainer = beanMap.get(key);
                Class<?> clazz = beanContainer.getBeanClass();
                Object bean = clazz.getDeclaredConstructors()[0].newInstance();
                List<Field> autowiredFields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> beanContainer.getDependencies().contains(field.getType().getName()))
                        .collect(Collectors.toList());
                for (Field autowiredField : autowiredFields) {
                    autowiredField.setAccessible(true);
                    autowiredField.set(bean, beanMap.get(autowiredField.getType().getName()).getBean());
                }
                beanContainer.injectBean(bean);
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
