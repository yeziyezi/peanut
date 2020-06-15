package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodParameterInfo;
import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Bean;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.annotation.DependOn;
import one.yezii.peanut.core.bootloader.Peanut;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BeanContainerFactory {
    private List<String> annotationsWithComponentAnnotation;

    public BeanContainerFactory() {
        collectAnnotationsWithComponentAnnotation();
    }

    private void collectAnnotationsWithComponentAnnotation() {
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

    public void initComponentBeanContainer(ClassInfo classInfo) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        String beanName = getBeanNameOfClassInfo(classInfo);
        BeanContainerRepository.checkBeanContainerExist(beanName);
        ComponentBeanContainer beanContainer = new ComponentBeanContainer(beanName);
        beanContainer.addDependencies(getDependenciesOfClassInfo(classInfo));
        beanContainer.setClassInfo(classInfo);
        beanContainer.initBeanInstance();
        BeanContainerRepository.addBeanContainer(beanContainer);
        if (beanContainer.isConfiguration()) {
            classInfo.getDeclaredMethodInfo()
                    .filter(methodInfo -> methodInfo.hasAnnotation(Bean.class.getName()))
                    .forEach(methodInfo -> initMethodBeanContainer(beanName, methodInfo));
        }
    }

    private void initMethodBeanContainer(String configurationBeanName, MethodInfo methodInfo) {
        String beanName = getBeanNameOfMethodInfo(methodInfo);
        BeanContainerRepository.checkBeanContainerExist(beanName);
        MethodBeanContainer beanContainer = new MethodBeanContainer(beanName);
        beanContainer.setConfigurationBeanName(configurationBeanName);
        beanContainer.setMethodInfo(methodInfo);
        beanContainer.setConfigurationBeanName(configurationBeanName);

        List<String> parameterNames = getParameterNamesOfMethodInfo(methodInfo);
        beanContainer.setParameterNames(parameterNames.toArray(String[]::new));

        List<String> dependencies = getDependOnsOfMethodInfo(methodInfo);
        dependencies.addAll(parameterNames);
        beanContainer.addDependencies(dependencies.toArray(String[]::new));

        BeanContainerRepository.addBeanContainer(beanContainer);
    }

    private List<String> getParameterNamesOfMethodInfo(MethodInfo methodInfo) {
        return Arrays.stream(methodInfo.getParameterInfo())
                .map(MethodParameterInfo::getName)
                .collect(Collectors.toList());
    }

    private List<String> getDependOnsOfMethodInfo(MethodInfo methodInfo) {
        if (!methodInfo.hasAnnotation(DependOn.class.getName())) {
            return Collections.emptyList();
        }
        return Arrays.asList((String[]) (methodInfo.getAnnotationInfo(DependOn.class.getName())
                .getParameterValues().getValue("value")));
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
                .map(annotationName -> getValueOfAnnotationInClassInfo(classInfo, annotationName))
                .filter(name -> !name.isBlank())
                .findFirst()
                .orElse(getStringWithFirstCharacterLowercase(classInfo.getSimpleName()));
    }

    private String getValueOfAnnotationInClassInfo(ClassInfo classInfo, String annotationName) {
        return classInfo.getAnnotationInfo(annotationName)
                .getParameterValues()
                .getValue("value")
                .toString();
    }

    private String getStringWithFirstCharacterLowercase(String s) {
        if (s.length() <= 1) {
            return s.toLowerCase();
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private String getBeanNameOfMethodInfo(MethodInfo methodInfo) {
        return Optional.ofNullable(methodInfo.getAnnotationInfo(Bean.class.getName()))
                .map(annotationInfo -> annotationInfo.getParameterValues().getValue("value"))
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse(methodInfo.getName());
    }
}
