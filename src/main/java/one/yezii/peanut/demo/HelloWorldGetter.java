package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.demo.demo2.ClassB;

@Component
public class HelloWorldGetter {
    @Autowired
    private ClassB classB;

    public String get() {
        return "hello world";
    }
}
