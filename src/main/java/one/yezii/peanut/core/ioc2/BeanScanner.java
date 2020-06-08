package one.yezii.peanut.core.ioc2;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.annotation.Configuration;
import one.yezii.peanut.core.bootloader.Peanut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanScanner {
    private Map<String, BeanContainer> beanContainers = new HashMap<>();
    private List<String> annotationsWithComponentAnnotation;

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

    public Map<String, BeanContainer> scan(String basePackage) {
        ScanResult scanResult = new ClassGraph().verbose(false).enableAllInfo()
                .whitelistPackages(basePackage).scan();
        beanContainers = scanResult.getClassesWithAnnotation(Component.class.getName())
                .stream()
                .filter(classInfo -> !classInfo.isAnnotation())
                .map(this::getBeanContainerOfClassInfo)
                .collect(Collectors.toMap(BeanContainer::name, bc -> bc));
        return beanContainers;
    }

    private BeanContainer getBeanContainerOfClassInfo(ClassInfo classInfo) {
        String beanName = getBeanNameOfClassInfo(classInfo);
        if (beanContainers.containsKey(beanName)) {
            throw new IllegalArgumentException("bean [" + beanName + "] already exist");
        }
        BeanContainer beanContainer = new BeanContainer(beanName);
        //how to resolve  dependencies of @Bean and @Configuration field correctly,Spring?
        if (classInfo.hasAnnotation(Configuration.class.getName())) {
            //todo
//            classInfo.getMethodInfo().filter(methodInfo -> methodInfo.)
//            classInfo.getFieldInfo()
//                    .filter(fieldInfo -> fieldInfo.hasAnnotation(Bean.class.getName()))
//                    .filter()
        }
        //todo
        return beanContainer;
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
                .getValue("name")
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
