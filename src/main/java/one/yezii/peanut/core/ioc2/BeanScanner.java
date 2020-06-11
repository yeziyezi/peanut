package one.yezii.peanut.core.ioc2;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import one.yezii.peanut.core.annotation.*;
import one.yezii.peanut.core.bootloader.Peanut;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrap;
import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrapVoid;

public class BeanScanner {
    private Map<String, BeanContainer> beanContainers = new HashMap<>();
    private List<String> annotationsWithComponentAnnotation;
    private Map<String, ClassInfo> configurationClassInfos = new HashMap<>();
    private Map<String, MethodInfo> beanMethodInfos = new HashMap<>();
    private Map<String, BeanContainer> finishedBeanContainer = new HashMap<>();

    public BeanScanner() {
        String bootloaderPackageName = Peanut.class.getPackageName();
        String corePackageName = bootloaderPackageName.substring(bootloaderPackageName.lastIndexOf("."));
        annotationsWithComponentAnnotation = new ClassGraph()
                .verbose(false)
                .enableAllInfo()
                .whitelistPackages(corePackageName)
                .scan()
                .getClassesWithAnnotation(Component.class.getName())
                .filter(ClassInfo::isAnnotation)
                .stream()
                .map(ClassInfo::getName)
                .collect(Collectors.toList());
    }

    public BeanScanner scan(String basePackage) {
        new ClassGraph().verbose(false).enableAllInfo()
                .whitelistPackages(basePackage).scan()
                .getClassesWithAnnotation(Component.class.getName())
                .stream()
                .filter(classInfo -> !classInfo.isAnnotation())
                .forEach(wrapVoid(this::getBeanContainerOfClassInfo));
        beanContainers.values().stream()
                .filter(bc -> bc.hasAnnotation(Configuration.class.getName()))
                .collect(Collectors.toList())
                .forEach(bc -> getMethodBeanContainersOfConfiguration(bc.name()));
        while (!beanContainers.isEmpty()) {
            List<Map.Entry<String, BeanContainer>> entryList = beanContainers.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().dependencies().isEmpty())
                    .collect(Collectors.toList());
            if (entryList.isEmpty()) {
                throw new RuntimeException("dependencies not found or circular dependencies!");
            }
            entryList.forEach(wrapVoid(entry -> {
                if (entry.getValue().isMethodBean()) {
                    injectBeanInMethodBean(entry.getValue());
                }
                beanContainers.remove(entry.getKey());
                finishedBeanContainer.put(entry.getKey(), entry.getValue());
                beanContainers.forEach((k, v) -> {
                    if (v.dependencies().contains(entry.getKey())) {
                        v.removeDependencies(entry.getKey());
                        if (!v.isMethodBean()) {
                            try {
                                Field field = v.beanType().getDeclaredField(entry.getValue().name());
                                field.setAccessible(true);
                                field.set(v.bean(), entry.getValue().bean());
                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
            }));
        }
        return this;
    }

    public Map<String, BeanContainer> result() {
        return finishedBeanContainer;
    }

    private void getMethodBeanContainersOfConfiguration(String beanName) {
        configurationClassInfos.get(beanName).getMethodInfo().stream()
                .filter(methodInfo -> methodInfo.hasAnnotation(Bean.class.getName()))
                .map(wrap(methodInfo -> getMethodBeanContainerOfMethodInfo(beanName, methodInfo)))
                .forEach(bc -> beanContainers.put(bc.name(), bc));
    }

    private void getBeanContainerOfClassInfo(ClassInfo classInfo) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        String beanName = getBeanNameOfClassInfo(classInfo);
        if (beanContainers.containsKey(beanName)) {
            throw new IllegalArgumentException("bean [" + beanName + "] already exist");
        }
        beanContainers.put(beanName, getComponentBeanContainerOfClassInfo(beanName, classInfo));
        if (classInfo.hasAnnotation(Configuration.class.getName())) {
            configurationClassInfos.put(beanName, classInfo);
        }
    }

    private void injectBeanInMethodBean(BeanContainer beanContainer)
            throws InvocationTargetException, IllegalAccessException {
        MethodInfo methodInfo = beanMethodInfos.get(beanContainer.name());
        if (beanContainer.dependencies().isEmpty()) {
            //now only support no parameter bean method
            String configurationBeanName = beanContainer.configurationBeanName();
            BeanContainer configurationBeanContainer = Optional.ofNullable(beanContainers.get(configurationBeanName))
                    .or(() -> Optional.ofNullable(finishedBeanContainer.get(configurationBeanName)))
                    .orElseThrow(() -> new RuntimeException(configurationBeanName + " not found"));
            beanContainer.injectBean(methodInfo.loadClassAndGetMethod()
                    .invoke(configurationBeanContainer.bean()));
        }
    }

    private BeanContainer getMethodBeanContainerOfMethodInfo(String configurationBeanName, MethodInfo methodInfo) {
        String beanName = Optional.ofNullable(methodInfo.getAnnotationInfo(Bean.class.getName()))
                .map(annotationInfo -> annotationInfo.getParameterValues().getValue("value"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse(methodInfo.getName());
        if (beanContainers.containsKey(beanName)) {
            throw new IllegalArgumentException("bean [" + beanName + "] already exist");
        }
        beanMethodInfos.put(beanName, methodInfo);
        BeanContainer beanContainer = new BeanContainer(beanName);
        beanContainer.setMethodBean();
        beanContainer.configurationBeanName(configurationBeanName);
        if (methodInfo.hasAnnotation(DependOn.class.getName())) {
            String[] dependOns = (String[]) (methodInfo.getAnnotationInfo(DependOn.class.getName())
                    .getParameterValues().getValue("value"));
            beanContainer.addDependencies(dependOns);
        }
        beanContainer.beanType(methodInfo.loadClassAndGetMethod().getReturnType());
        return beanContainer;
    }

    private BeanContainer getComponentBeanContainerOfClassInfo(String beanName, ClassInfo classInfo)
            throws IllegalAccessException, InvocationTargetException, InstantiationException {
        BeanContainer beanContainer = new BeanContainer(beanName);
        beanContainer.addDependencies(getDependenciesOfClassInfo(classInfo));
        beanContainer.addAnnotations(classInfo.getAnnotations().getNames().toArray(String[]::new));
        beanContainer.addInterfaces(classInfo.getInterfaces().getNames().toArray(String[]::new));
        beanContainer.addParents(classInfo.getSuperclasses().getNames().toArray(String[]::new));
        beanContainer.beanType(classInfo.loadClass());
        beanContainer.injectBean(classInfo.loadClass().getDeclaredConstructors()[0].newInstance());
        return beanContainer;
    }

    private String[] getDependenciesOfClassInfo(ClassInfo classInfo) {
        return classInfo.getFieldInfo()
                .filter(fieldInfo -> fieldInfo.hasAnnotation(Autowired.class.getName()))
                .stream()
                .map(fieldInfo -> fieldInfo.loadClassAndGetField().getName())
                .collect(Collectors.toList()).toArray(String[]::new);
    }

    private String getBeanNameOfClassInfo(ClassInfo classInfo) {
        return annotationsWithComponentAnnotation
                .stream()
                .filter(classInfo::hasAnnotation)
                .map(annotationName -> getNameValueOfClassInfo(classInfo, annotationName))
                .filter(name -> !name.isBlank())
                .findFirst()
                .orElse(getStringWithFirstCharacterLowercase(classInfo.getSimpleName()));
    }

    private String getNameValueOfClassInfo(ClassInfo classInfo, String annotationName) {
        return classInfo.getAnnotationInfo(annotationName)
                .getParameterValues()
                .getValue("value")
                .toString();
    }

    private String getStringWithFirstCharacterLowercase(String s) {
        if (s.isEmpty()) {
            return s;
        }
        String s0 = s.substring(0, 1);
        String s1 = s.substring(1);
        return s0.toLowerCase() + s1;
    }
}
