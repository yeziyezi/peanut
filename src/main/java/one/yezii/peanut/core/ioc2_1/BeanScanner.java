package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.ClassGraph;
import one.yezii.peanut.core.annotation.Component;

import java.util.List;

import static one.yezii.peanut.core.util.LambdaExceptionWrapper.wrapVoid;

public class BeanScanner {
    private BeanContainerFactory beanContainerFactory = new BeanContainerFactory();

    public void scan() throws Exception {
        new ClassGraph().verbose(false).enableAllInfo()
                .whitelistPackages(PackageRegister.list()).scan()
                .getClassesWithAnnotation(Component.class.getName())
                .filter(classInfo -> !classInfo.isAnnotation())
                .forEach(wrapVoid(beanContainerFactory::initComponentBeanContainer));
        List<BeanContainer> notReadyBeanList = BeanContainerRepository.notReadyBeanList();
        int size = notReadyBeanList.size();
        while (size != 0) {
            dealWithDependencies(notReadyBeanList);
            notReadyBeanList = BeanContainerRepository.notReadyBeanList();
            if (notReadyBeanList.size() == size) {
                throw new RuntimeException("dependencies not found or circular dependencies!");
            }
            size = notReadyBeanList.size();
        }
    }

    private void dealWithDependencies(List<BeanContainer> notReadyBeanList) throws Exception {
        //todo
        for (BeanContainer beanContainer : notReadyBeanList) {
            if (beanContainer.noDependencies() && beanContainer.beanInstance() == null) {
                beanContainer.initBeanInstance();
            }
        }
    }
}
