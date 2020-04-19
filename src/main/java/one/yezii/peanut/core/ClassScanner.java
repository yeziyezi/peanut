package one.yezii.peanut.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClassScanner {
    public Set<Class<?>> scan(String packageName) throws IOException {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader classLoader =ClassScanner.class.getClassLoader();
        classLoader.getResources(packageName);
        return null;
    }
}
