package one.yezii.peanut.core.http;

import one.yezii.peanut.core.context.GlobalContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodInvoker {
    private Method method;
    private String beanName;

    private MethodInvoker() {
    }

    public static MethodInvoker of(Method method, String beanName) {
        MethodInvoker invoker = new MethodInvoker();
        invoker.method = method;
        invoker.beanName = beanName;
        return invoker;
    }

    public Object invoke(Object... params) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(GlobalContext.beans.get(beanName), params);
    }

    public Method getMethod() {
        return method;
    }
}
