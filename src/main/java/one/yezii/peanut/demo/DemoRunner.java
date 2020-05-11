package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.util.logging.Logger;

@Component
public class DemoRunner implements PeanutRunner {
    @Autowired
    private HelloWorldGetter helloWorldGetter;

    public void run() {
        Logger.getLogger(DemoRunner.class.getName()).info(helloWorldGetter.get());
    }
}
