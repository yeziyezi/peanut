package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;

@Component
public class HelloWorldGetter {
    @Autowired
    DemoRunner demoRunner;

    public String get() {
        return "hello world";
    }
}
