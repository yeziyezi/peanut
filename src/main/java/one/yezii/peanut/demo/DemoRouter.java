package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.constant.HttpMethod;

@Router("/demo")
public class DemoRouter {
    @Route(value = "/hello", method = HttpMethod.Get)
    private void hello(String msg) {
        System.out.println("hello," + msg);
    }
}
