package one.yezii.peanut.demo.demo2;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.demo.demo2.demo3.ClassC;

@Component
public class ClassB {
    @Autowired
    private ClassC ClassC;
}
