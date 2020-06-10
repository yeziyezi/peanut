package one.yezii.peanut.core.ioc2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BeanContainer {
    private String name;
    private Set<String> dependencies = new HashSet<>();
    private Object bean;
    private Class<?> beanType;
    private Set<String> annotations = new HashSet<>();
    private Set<String> parents = new HashSet<>();
    private Set<String> interfaces = new HashSet<>();
    private boolean isMethodBean;
    private String ClassBeanNameOfMethodBean;

    public BeanContainer(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void addAnnotations(String... annotationName) {
        annotations.addAll(Arrays.asList(annotationName));
    }

    public boolean hasAnnotation(String annotationName) {
        return annotations.contains(annotationName);
    }

    public void addDependency(String... dependencyName) {
        dependencies.addAll(Arrays.asList(dependencyName));
    }

    public Set<String> dependencies() {
        return dependencies;
    }

    public boolean hasInterface(String interfaceName) {
        return interfaces.contains(interfaceName);
    }

    public void addInterfaces(String... interfaceName) {
        interfaces.addAll(Arrays.asList(interfaceName));
    }

    public void addParents(String... parentName) {
        parents.addAll(Arrays.asList(parentName));
    }

    public boolean hasParent(String parentName) {
        return parents.contains(parentName);
    }

    public void remove(String... dependencyName) {
        dependencies.removeAll(Arrays.asList(dependencyName));
    }

    public Object bean() {
        return bean;
    }

    public BeanContainer bean(Object bean) {
        this.bean = bean;
        return this;
    }

    public Class<?> beanType() {
        return beanType;
    }

    public BeanContainer beanType(Class<?> beanType) {
        this.beanType = beanType;
        return this;
    }
}