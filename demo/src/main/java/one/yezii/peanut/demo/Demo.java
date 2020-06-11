package one.yezii.peanut.demo;

import one.yezii.peanut.core.ioc2.BeanScanner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) throws NoSuchFieldException, NoSuchMethodException {
//        System.out.println(D.class.getDeclaredField("m").getGenericType().getTypeName());
//        System.out.println(D.class.getDeclaredField("m").toGenericString());
//        System.out.println(D.class.getDeclaredMethod("list").toGenericString());
//        System.out.println(D.class.getDeclaredMethod("list").getGenericReturnType().getTypeName());
        BeanScanner beanScanner = new BeanScanner();
        System.out.println(beanScanner.scan(Demo.class.getPackageName()).result().toString());
    }

    static class D {
        public Map<String, Object> m;

        public List<String> list() {
            return Collections.emptyList();
        }
    }
}
