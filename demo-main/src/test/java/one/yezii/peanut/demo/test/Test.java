package one.yezii.peanut.demo.test;

import one.yezii.peanut.core.bootloader.Peanut;
import one.yezii.peanut.demo.demo1.DemoApp;
import one.yezii.peanut.demo.demo1.DemoRouter;

public class Test {
    public static void main(String[] args) {
        Peanut peanut = Peanut.peanut(DemoApp.class);
        System.out.println(peanut.getBean(DemoRouter.class).hello());
    }
}
