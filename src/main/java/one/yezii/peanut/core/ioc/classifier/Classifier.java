package one.yezii.peanut.core.ioc.classifier;

import one.yezii.peanut.core.ioc.BeanContainer;
import one.yezii.peanut.core.ioc.BeanRepository;

import java.util.List;
import java.util.Map;

public interface Classifier<K, V> {
    static void doClassify(List<BeanContainer> beanContainers) {
        BeanRepository.runners.putAll(new PeanutRunnerClassifier().classify(beanContainers));
        BeanRepository.beans.putAll(new BeanClassifier().classify(beanContainers));
        BeanRepository.routes.putAll(new RouteClassifier().classify(beanContainers));
    }

    Map<K, V> classify(List<BeanContainer> beanContainers);
}
