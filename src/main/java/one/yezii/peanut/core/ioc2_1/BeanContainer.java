package one.yezii.peanut.core.ioc2_1;

import one.yezii.peanut.core.annotation.Configuration;
import one.yezii.peanut.core.annotation.Route;
import one.yezii.peanut.core.annotation.Router;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class BeanContainer {
    private Set<String> dependencies = new HashSet<>();
    private Object beanInstance;
    private String name;

    public BeanContainer(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public void addDependencies(String... beanName) {
        dependencies.addAll(Arrays.asList(beanName));
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public boolean noDependencies() {
        return dependencies.isEmpty();
    }

    public void removeDependency(String... dependency) {
        dependencies.removeAll(Arrays.asList(dependency));
    }

    protected void injectBeanInstance(Object beanInstance) {
        this.beanInstance = beanInstance;
    }

    public abstract boolean hasAnnotation(Class<?> annotation);

    public abstract boolean implementsInterface(Class<?> interfaceClass);

    protected abstract void initBeanInstance(Object... args) throws Exception;

    public boolean isPeanutRunner() {
        return hasAnnotation(PeanutRunner.class);
    }

    public boolean isRouter() {
        return !isMethodBean() && hasAnnotation(Router.class);
    }

    public boolean isRoute() {
        return isMethodBean() && hasAnnotation(Route.class);
    }

    public boolean hasDependency(String dependency) {
        return dependencies.contains(dependency);
    }

    public Object beanInstance() {
        return beanInstance;
    }

    public abstract boolean isMethodBean();

    public boolean isComponentBean() {
        return !isMethodBean();
    }

    public boolean isConfiguration() {
        return !isMethodBean() && hasAnnotation(Configuration.class);
    }
}
