package one.yezii.peanut.core.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import one.yezii.peanut.core.annotation.Json;
import one.yezii.peanut.core.bean.UtilBeans;
import one.yezii.peanut.core.ioc.BeanRepository;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

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

    public Object invoke(Object... params) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        Object result = method.invoke(BeanRepository.beans.get(beanName), params);
        if (result == null) {
            return null;
        }
        if (method.isAnnotationPresent(Json.class)) {
            return UtilBeans.objectMapper.writeValueAsString(result);
        }
        return result;
    }

    public Method method() {
        return method;
    }

    public Parameter[] parameters() {
        return method.getParameters();
    }
}
