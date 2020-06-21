package one.yezii.peanut.demo.demo1;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;

@Router("/demo")
public class DemoRouter {
    @Autowired
    private String world;

    @Route
    public String hello() {
        return "hello, " + world;
    }
}
