package one.yezii.peanut.demo;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.constant.HttpMethod;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//@Router("/demo")
@Component
public class DemoRunner implements PeanutRunner {
    @Autowired
    private HelloWorldGetter helloWorldGetter;

    public void run() {
        System.out.println(helloWorldGetter.get());
    }

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = DemoRunner.class.getDeclaredMethod("hello", String.class);
        method.invoke(new DemoRunner(), "a");
    }

    @Route(value = "/t1", method = HttpMethod.Get)
    private void hello(String msg) {
        System.out.println("hello," + msg);
    }
}
