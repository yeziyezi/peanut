package one.yezii.peanut.core.ioc.scan.consumer;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.constant.ClassName;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.ioc.BeanDependency;
import one.yezii.peanut.core.ioc.DependencyEndpoint;
import one.yezii.peanut.core.ioc.DependencyForest;
import one.yezii.peanut.core.ioc.scan.ScanResultConsumer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentAnnotationScanResultConsumer implements ScanResultConsumer {
    private Map<String, BeanDependency> getDependencyMap(ClassInfoList classInfos) {
        return classInfos.stream().collect(Collectors.toMap(ClassInfo::getName, classInfo -> {
            List<String> dependencies = classInfo.getFieldInfo()
                    .filter(fieldInfo -> fieldInfo.hasAnnotation(ClassName.autowiredAnnotation))
                    .stream().map(fieldInfo -> fieldInfo.loadClassAndGetField().getType().getName())
                    .collect(Collectors.toList());
            return new BeanDependency().setClassInfo(classInfo).setDependencies(dependencies);
        }));
    }

    private void registerBeans(Map<String, BeanDependency> dependencyMap) {
        GlobalContext.runners.putAll(dependencyMap.entrySet().stream()
                .filter(entry -> entry.getValue().getClassInfo().getInterfaces().containsName(ClassName.runnerInterface))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (PeanutRunner) (entry.getValue().getBean()))));
        GlobalContext.beans.putAll(dependencyMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBean())));
    }

    private Map<String, BeanDependency> injectBeans(Map<String, BeanDependency> dependencyMap) {
        List<DependencyEndpoint> endpoints = dependencyMap.entrySet().stream()
                .map(entry -> new DependencyEndpoint().setName(entry.getKey())
                        .addNext(entry.getValue().getDependencies()))
                .collect(Collectors.toList());
        List<String> injectOrders = new DependencyForest().addEndpoints(endpoints).getInjectOrders();
        Map<String, BeanDependency> injectedMap = new HashMap<>();
        try {
            for (String key : injectOrders) {
                BeanDependency bd = dependencyMap.get(key);
                Class<?> clazz = bd.getClassInfo().loadClass();
                Object bean = clazz.getConstructors()[0].newInstance();
                List<Field> autowiredFields = Arrays.stream(clazz.getDeclaredFields())
                        .filter(field -> bd.getDependencies().contains(field.getType().getName()))
                        .collect(Collectors.toList());
                for (Field autowiredField : autowiredFields) {
                    autowiredField.setAccessible(true);
                    autowiredField.set(bean, injectedMap.get(autowiredField.getType().getName()).getBean());
                }
                injectedMap.put(key, bd.setBean(bean));
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return injectedMap;
    }

    public void consume(ScanResult scanResult) {
        ClassInfoList classWithComponentAnnotation = scanResult.getClassesWithAnnotation(ClassName.componentAnnotation);
        registerBeans(injectBeans(getDependencyMap(classWithComponentAnnotation)));
    }
}
