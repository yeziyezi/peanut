package one.yezii.peanut.demo;

import one.yezii.peanut.core.bootloader.Peanut;
import one.yezii.peanut.core.annotation.PeanutBoot;

@PeanutBoot
public class App {
    public static void main(String[] args) {
        Peanut.eat(App.class);
    }
}

