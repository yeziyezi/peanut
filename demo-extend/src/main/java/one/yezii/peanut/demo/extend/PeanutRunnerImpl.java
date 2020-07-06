package one.yezii.peanut.demo.extend;

import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.facade.PeanutRunner;

@Component
public class PeanutRunnerImpl implements PeanutRunner {
    @Override
    public void run() {
        System.out.println(PeanutRunnerImpl.class.getName());
    }
}
