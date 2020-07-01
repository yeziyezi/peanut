package one.yezii.peanut.demo.demo1;

import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Json;
import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.util.CommonMap;

@Router("demo")
@Json
public class DemoRouter {
    @Autowired
    private String world;

    @Route
    public String hello() {
        return "hello, " + world;
    }

    @Route("hello")
    public String hello(int a) {
        return "hello, " + a;
    }

    @Route("json")
    public CommonMap json(@Json CommonMap commonMap) {
        return commonMap;
    }

    @Route("student")
    public Student student(@Json Student student) {
        return student;
    }

    @Route("studentC")
    public Student studentComplexParser(Student student) {
        return student;
    }
}
