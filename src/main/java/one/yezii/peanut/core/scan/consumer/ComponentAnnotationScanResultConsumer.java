package one.yezii.peanut.core.scan.consumer;

import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.BeanDependency;
import one.yezii.peanut.core.constant.ClassName;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.scan.ScanResultConsumer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
        //todo add dependency cycle test
        //todo add not found dependency test
        HashMap<String, BeanDependency> done = new HashMap<>();
        while (!dependencyMap.isEmpty()) {
            List<String> removeList = new ArrayList<>();
            for (Map.Entry<String, BeanDependency> entry : dependencyMap.entrySet()) {
                String k = entry.getKey();
                BeanDependency bd = entry.getValue();
                try {
                    if (bd.getDependencies().isEmpty()) {
                        bd.setBean(bd.getClassInfo().loadClass().getConstructors()[0].newInstance());
                        done.put(k, bd);
                        removeList.add(k);
                        continue;
                    }
                    if (done.keySet().containsAll(bd.getDependencies())) {
                        Class<?> clazz = bd.getClassInfo().loadClass();
                        Object bean = clazz.getConstructors()[0].newInstance();
                        for (Field field : clazz.getDeclaredFields()) {
                            field.setAccessible(true);
                            field.set(bean, done.get(field.getType().getName()).getBean());
                        }
                        bd.setBean(bean);
                        done.put(k, bd);
                        removeList.add(k);
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            removeList.forEach(dependencyMap::remove);
        }
        return done;
    }

    public void consume(ScanResult scanResult) {
        ClassInfoList classWithComponentAnnotation = scanResult.getClassesWithAnnotation(ClassName.componentAnnotation);
        registerBeans(injectBeans(getDependencyMap(classWithComponentAnnotation)));
    }
}
