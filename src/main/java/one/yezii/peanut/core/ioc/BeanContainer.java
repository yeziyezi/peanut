package one.yezii.peanut.core.ioc;

import java.util.ArrayList;
import java.util.List;

public class BeanContainer {
    private String name;
    private List<String> dependencies = new ArrayList<>();
    private Object bean;
    private Class<?> beanClass;
    private List<String> interfaces = new ArrayList<>();

    private BeanContainer() {
    }

    public static BeanContainer ofName(String name) {
        BeanContainer bean = new BeanContainer();
        bean.name = name;
        return bean;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public BeanContainer setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
        return this;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public BeanContainer addInterfaces(List<String> interfaces) {
        this.interfaces.addAll(interfaces);
        return this;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public BeanContainer addDependencies(List<String> dependencies) {
        this.dependencies.addAll(dependencies);
        return this;
    }

    public BeanContainer injectBean(Object bean) {
        this.bean = bean;
        return this;
    }

    public Object getBean() {
        return bean;
    }

}
