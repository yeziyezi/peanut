package one.yezii.peanut.core.ioc;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanDependencies {
    private Map<String, BeanContainer> beanMap;


    public BeanDependencies(String basePackage) {
        ScanResult scanResult = new ClassGraph().verbose(false).enableAllInfo().whitelistPackages(basePackage).scan();
        this.beanMap = scanResult
                .getClassesWithAnnotation(Component.class.getName())
                .stream()
                .filter(classInfo -> !classInfo.isAnnotation())
                .collect(Collectors.toMap(ClassInfo::getName, classInfo -> {
                    List<String> dependencies = classInfo.getFieldInfo()
                            .filter(fieldInfo -> fieldInfo.hasAnnotation(Autowired.class.getName()))
                            .stream().map(fieldInfo -> fieldInfo.loadClassAndGetField().getType().getName())
                            .collect(Collectors.toList());
                    return BeanContainer.ofName(classInfo.getName()).addDependencies(dependencies)
                            .setBeanClass(classInfo.loadClass()).addInterfaces(classInfo.getInterfaces().getNames())
                            .addAnnotations(classInfo.getAnnotations().getNames());
                }));
    }

    public Map<String, Object> getBeans() {
        return beanMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBean()));
    }

    @SuppressWarnings("unchecked")
    public <T> Map<String, T> getBeansWithInterface(Class<T> tClass) {
        return beanMap.entrySet().stream()
                .filter(entry -> entry.getValue().getInterfaces().contains(tClass.getName()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (T) (entry.getValue().getBean())));
    }

    public Map<String, Object> getBeansWithAnnotation(Class<?> tClass) {
        return beanMap.entrySet().stream()
                .filter(entry -> entry.getValue().getAnnotations().contains(tClass.getName()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getBean()));
    }

    public Map<String, BeanContainer> getBeanMap() {
        return beanMap;
    }
}
