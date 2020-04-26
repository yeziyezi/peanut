package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.time.format.DateTimeFormatter;

@Component
public class DemoRunner implements PeanutRunner {
    @Autowired
    private HelloWorldGetter helloWorldGetter;
    @Autowired
    private DateTimeFormatter dateTimeFormatter;

    public void run() {
        System.out.println(helloWorldGetter.get());
    }
}
