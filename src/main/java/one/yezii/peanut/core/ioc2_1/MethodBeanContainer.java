package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.MethodInfo;

import java.lang.reflect.InvocationTargetException;

public class MethodBeanContainer extends BeanContainer {
    private String configurationBeanName;
    private MethodInfo methodInfo;
    private String[] parameterNames;

    public MethodBeanContainer(String name) {
        super(name);
    }

    @Override
    public boolean hasAnnotation(Class<?> annotation) {
        return methodInfo.hasAnnotation(annotation.getName());
    }

    @Override
    public boolean implementsInterface(Class<?> interfaceClass) {
        throw new IllegalCallerException("methodBean doesn't support [implementsInterface] call");
    }

    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

    public void setMethodInfo(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
    }

    public void setConfigurationBeanName(String configurationBeanName) {
        this.configurationBeanName = configurationBeanName;
    }

    @Override
    public boolean isMethodBean() {
        return true;
    }

    @Override
    public void initBeanInstance(Object... args) throws InvocationTargetException, IllegalAccessException {
        BeanContainer configBeanContainer = BeanContainerRepository.getBeanContainer(configurationBeanName);
        injectBeanInstance(methodInfo.loadClassAndGetMethod().invoke(configBeanContainer.beanInstance(), args));
    }
}
