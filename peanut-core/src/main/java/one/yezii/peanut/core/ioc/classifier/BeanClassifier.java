package one.yezii.peanut.core.ioc.classifier;

import one.yezii.peanut.core.ioc.BeanContainer;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanClassifier implements Classifier<String, Object> {
    @Override
    public Map<String, Object> classify(List<BeanContainer> beanContainers) {
        return beanContainers.stream()
                .collect(Collectors.toMap(BeanContainer::name, BeanContainer::beanInstance));
    }
}
