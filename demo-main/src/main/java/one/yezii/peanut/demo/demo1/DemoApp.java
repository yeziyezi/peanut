package one.yezii.peanut.demo.demo1;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.bootloader.Peanut;

@PeanutBoot
public class DemoApp {
    public static void main(String[] args) {
        Peanut.peanut(DemoApp.class)
                .registerPackage("one.yezii.peanut.demo.demo2")
                .run();
    }
}
