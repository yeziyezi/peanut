package one.yezii.peanut.core.ioc.classifier;

import one.yezii.peanut.core.facade.PeanutRunner;
import one.yezii.peanut.core.ioc.BeanContainer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PeanutRunnerClassifier implements Classifier<String, PeanutRunner> {
    @Override
    public Map<String, PeanutRunner> classify(List<BeanContainer> beanContainers) {
        return beanContainers.stream()
                .filter(bc -> bc.implementsInterface(PeanutRunner.class))
                .collect(Collectors.toMap(BeanContainer::name, bc -> (PeanutRunner) bc.beanInstance()));
    }
}
