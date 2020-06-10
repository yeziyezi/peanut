package one.yezii.peanut.core.ioc2;

public class Dependency {
    private String className;
    private String objectName;

    public Dependency(String className, String objectName) {
        this.className = className;
        this.objectName = objectName;
    }

    public boolean classNameEquals(String beanName) {
        return beanName.equals(className);
    }

    public boolean objectNameEquals(String beanName) {
        return beanName.equals(objectName);
    }
}
