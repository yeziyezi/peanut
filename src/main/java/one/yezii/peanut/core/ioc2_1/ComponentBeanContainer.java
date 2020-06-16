package one.yezii.peanut.core.ioc2_1;

import io.github.classgraph.ClassInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class ComponentBeanContainer extends BeanContainer {
    private ClassInfo classInfo;

    public ComponentBeanContainer(String name) {
        super(name);
    }

    @Override
    public boolean hasAnnotation(Class<?> annotation) {
        return classInfo.hasAnnotation(annotation.getName());
    }

    @Override
    public boolean implementsInterface(Class<?> interfaceClass) {
        return classInfo.implementsInterface(interfaceClass.getName());
    }

    @Override
    protected void initBeanInstance(Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        injectBeanInstance(classInfo.loadClass().getDeclaredConstructors()[0].newInstance(args));
    }

    @Override
    public boolean isMethodBean() {
        return false;
    }

    public void setClassInfo(ClassInfo classInfo) {
        this.classInfo = classInfo;
    }

    public void initField(String name) throws NoSuchFieldException, IllegalAccessException {
        Objects.requireNonNull(beanInstance());
        Field field = beanInstance().getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(beanInstance(), BeanContainerRepository.getBeanInstance(name));
    }
}
