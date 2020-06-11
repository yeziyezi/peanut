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
    private boolean isMethodBean = false;
    private String configurationBeanName;

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

    public void addDependencies(String... dependencyName) {
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

    public void removeDependencies(String... dependencyName) {
        dependencies.removeAll(Arrays.asList(dependencyName));
    }

    public Object bean() {
        return bean;
    }

    public void injectBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> beanType() {
        return beanType;
    }

    public void beanType(Class<?> beanType) {
        this.beanType = beanType;
    }

    public void setMethodBean() {
        this.isMethodBean = true;
    }

    public boolean isMethodBean() {
        return isMethodBean;
    }

    public String configurationBeanName() {
        return configurationBeanName;
    }

    public void configurationBeanName(String configurationBeanName) {
        this.configurationBeanName = configurationBeanName;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof BeanContainer && ((BeanContainer) obj).name.equals(name);
    }
}
