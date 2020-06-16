package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.ClassGraph;
import one.yezii.peanut.core.annotation.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrapVoid;

public class BeanScanner {
    private BeanContainerFactory beanContainerFactory = new BeanContainerFactory();
    private Logger logger = Logger.getLogger(BeanScanner.class.getName());
    private List<BeanContainer> notReadyBeanList;

    public void scan() throws Exception {
        new ClassGraph().verbose(false).enableAllInfo()
                .whitelistPackages(PackageRegister.list()).scan()
                .getClassesWithAnnotation(Component.class.getName())
                .filter(classInfo -> !classInfo.isAnnotation())
                .forEach(wrapVoid(beanContainerFactory::initComponentBeanContainer));
        notReadyBeanList = BeanContainerRepository.notReadyBeanList();
        int size = notReadyBeanList.size();
        while (size != 0) {
            removeAndInjectDependencies();
            removeReadyBeanContainerOfList();
            if (notReadyBeanList.size() == size) {
                checkInvalidDependencies();
            }
            size = notReadyBeanList.size();
        }
    }

    private void checkInvalidDependencies() {
        for (BeanContainer beanContainer : notReadyBeanList) {
            for (String dependency : beanContainer.getDependencies()) {
                BeanContainerRepository.assertExist(dependency, beanContainer.name());
            }
        }
        String circularDependencies = notReadyBeanList.stream().map(BeanContainer::name)
                .collect(Collectors.joining(","));
        throw new RuntimeException("maybe circular dependencies in [" + circularDependencies + "]");
    }

    private void removeAndInjectDependencies() throws NoSuchFieldException, IllegalAccessException {
        List<String> notReadyBeanNames = notReadyBeanList.stream()
                .map(BeanContainer::name).collect(Collectors.toList());
        for (BeanContainer beanContainer : notReadyBeanList) {
            List<String> dependencyRemoveList = new ArrayList<>();
            for (String dependency : beanContainer.getDependencies()) {
                if (!notReadyBeanNames.contains(dependency)) {
                    //if componentBean,inject the field to bean instance
                    if (beanContainer.isComponentBean()) {
                        ((ComponentBeanContainer) beanContainer).initField(dependency);
                    }
                    dependencyRemoveList.add(dependency);
                }
            }
            beanContainer.removeDependency(dependencyRemoveList.toArray(String[]::new));
        }
    }

    private void removeReadyBeanContainerOfList() throws Exception {
        List<String> removeList = new ArrayList<>();
        for (BeanContainer beanContainer : notReadyBeanList) {
            if (beanContainer.noDependencies()) {
                if (beanContainer.beanInstance() == null) {
                    if (beanContainer.isMethodBean()) {
                        Object[] args = Arrays.stream(((MethodBeanContainer) beanContainer).getParameterNames())
                                .filter(parameterName -> BeanContainerRepository
                                        .assertExist(parameterName, beanContainer.name()))
                                .map(BeanContainerRepository::getBeanInstance)
                                .toArray();
                        beanContainer.initBeanInstance(args);
                    } else {
                        beanContainer.initBeanInstance();
                    }
                }
                removeList.add(beanContainer.name());
            }
        }
        notReadyBeanList.removeIf(beanContainer -> removeList.contains(beanContainer.name()));
    }
}
