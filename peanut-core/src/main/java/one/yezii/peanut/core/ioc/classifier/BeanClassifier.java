package one.yezii.peanut.core.ioc.classifier;

import one.yezii.peanut.core.ioc.BeanContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanClassifier implements Classifier<String, Object> {
    @Override
    public Map<String, Object> classify(List<BeanContainer> beanContainers) {
        Map<String, Object> beans = new HashMap<>();
        beanContainers.forEach(container -> {
            beans.put(container.name(), container.beanInstance());
            beans.put(container.beanInstance().getClass().getName(), container.beanInstance());
        });
        return beans;
    }
}
