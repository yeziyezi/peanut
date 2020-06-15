package one.yezii.peanut.demo;

import one.yezii.peanut.core.ioc2_1.BeanContainer;
import one.yezii.peanut.core.ioc2_1.BeanContainerRepository;
import one.yezii.peanut.core.ioc2_1.BeanScanner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws Exception {
        new BeanScanner().scan();
        List<BeanContainer> list = BeanContainerRepository.all();
    }

    static class D {
        public Map<String, Object> m;

        public List<String> list() {
            return Collections.emptyList();
        }
    }
}
