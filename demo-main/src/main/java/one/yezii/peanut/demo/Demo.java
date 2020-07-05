package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.bootloader.Peanut;
import one.yezii.peanut.core.ioc.BeanRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@PeanutBoot
public class Demo {
    public static void main(String[] args) {
        Peanut.run(Demo.class);
        System.out.println(BeanRepository.beans);
    }

    static class D {
        public Map<String, Object> m;

        public List<String> list() {
            return Collections.emptyList();
        }
    }
}
