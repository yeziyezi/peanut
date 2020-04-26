package one.yezii.peanut.core.ioc;

import io.github.classgraph.ClassInfo;

import java.util.List;

public class BeanDependency {
    private List<String> dependencies;
    private ClassInfo classInfo;
    private Object bean;


    public Object getBean() {
        return bean;
    }

    public BeanDependency setBean(Object bean) {
        this.bean = bean;
        return this;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public BeanDependency setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public ClassInfo getClassInfo() {
        return classInfo;
    }

    public BeanDependency setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
        return this;
    }
}
