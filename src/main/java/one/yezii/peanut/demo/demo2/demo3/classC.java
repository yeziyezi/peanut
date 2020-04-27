package one.yezii.peanut.demo.demo2.demo3;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.demo.HelloWorldGetter;

@Component
public class ClassC {
    @Autowired
    private HelloWorldGetter helloWorldGetter;
}
