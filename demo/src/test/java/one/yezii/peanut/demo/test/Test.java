package one.yezii.peanut.demo.test;

import one.yezii.peanut.core.ioc.BeanRepository;
import one.yezii.peanut.core.ioc.BeanScanner;

public class Test {
    public static void main(String[] args) throws Exception {
        new BeanScanner().scan();
        System.out.println(BeanRepository.routes);
    }
}
