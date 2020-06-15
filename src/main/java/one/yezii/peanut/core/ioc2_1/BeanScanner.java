package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.ClassGraph;
import one.yezii.peanut.core.annotation.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrapVoid;

public class BeanScanner {
    private BeanContainerFactory beanContainerFactory = new BeanContainerFactory();
    private Logger logger = Logger.getLogger(BeanScanner.class.getName());

    public void scan() throws Exception {
        new ClassGraph().verbose(false).enableAllInfo()
                .whitelistPackages(PackageRegister.list()).scan()
                .getClassesWithAnnotation(Component.class.getName())
                .filter(classInfo -> !classInfo.isAnnotation())
                .forEach(wrapVoid(beanContainerFactory::initComponentBeanContainer));
        List<BeanContainer> notReadyBeanList = BeanContainerRepository.notReadyBeanList();
        int size = notReadyBeanList.size();
        while (size != 0) {
            removeAndInjectDependencies(notReadyBeanList);
            removeReadyBeanContainerOfList(notReadyBeanList);
            if (notReadyBeanList.size() == size) {
                throw new RuntimeException("dependencies not found or circular dependencies!");
            }
            size = notReadyBeanList.size();
        }
    }

    private void removeAndInjectDependencies(List<BeanContainer> notReadyBeanList) throws NoSuchFieldException, IllegalAccessException {
        List<String> notReadyBeanNames = notReadyBeanList.stream()
                .map(BeanContainer::name).collect(Collectors.toList());
        for (BeanContainer beanContainer : notReadyBeanList) {
            for (String dependency : beanContainer.getDependencies()) {
                if (!notReadyBeanNames.contains(dependency)) {
                    //if componentBean,inject the field to bean instance
                    if (beanContainer.isComponentBean()) {
                        Object beanInstance = beanContainer.beanInstance();
                        if (beanInstance != null) {
                            Field field = beanInstance.getClass().getDeclaredField(dependency);
                            field.setAccessible(true);
                            field.set(beanInstance, BeanContainerRepository.getBeanInstance(dependency));
                        } else {
                            logger.warning("component bean [" + beanContainer.name() + "] has null bean instance.");
                        }
                    }
                    beanContainer.removeDependency(dependency);
                }
            }
        }
    }

    private void removeReadyBeanContainerOfList(List<BeanContainer> notReadyBeanList) throws Exception {
        List<String> removeList = new ArrayList<>();
        for (BeanContainer beanContainer : notReadyBeanList) {
            if (beanContainer.noDependencies()) {
                if (beanContainer.beanInstance() == null) {
                    if (beanContainer.isMethodBean()) {
                        Object[] args = Arrays.stream(((MethodBeanContainer) beanContainer).getParameterNames())
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
