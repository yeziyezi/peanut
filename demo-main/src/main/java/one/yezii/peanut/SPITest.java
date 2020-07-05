package one.yezii.peanut;

import one.yezii.peanut.core.facade.PeanutRunner;

import java.util.ServiceLoader;

public class SPITest {
    public static void main(String[] args) {
        ServiceLoader.load(PeanutRunner.class)
                .iterator()
                .forEachRemaining(PeanutRunner::run);
    }
}
