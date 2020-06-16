package one.yezii.peanut.core.ioc2_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BeanContainerRepository {
    private static Map<String, BeanContainer> map = new HashMap<>();

    public static void addBeanContainer(BeanContainer beanContainer) {
        map.put(beanContainer.name(), beanContainer);
    }

    public static BeanContainer getBeanContainer(String name) {
        return map.get(name);
    }

    public static void assertNotExist(String name) {
        if (map.containsKey(name)) {
            throw new IllegalArgumentException("bean container [" + name + "] already exist");
        }
    }

    public static boolean assertExist(String dependency, String selfContainer) {
        if (!map.containsKey(dependency)) {
            throw new IllegalStateException("dependency [" + dependency + "]  doesn't exist " +
                    "in bean [" + selfContainer + "]");
        }
        return true;
    }

    //get BeanContainer with null bean or has dependencies
    public static List<BeanContainer> notReadyBeanList() {
        return map.values().stream()
                .filter(bc -> bc.beanInstance() == null || !bc.noDependencies())
                .collect(Collectors.toList());
    }

    public static Object getBeanInstance(String name) {
        return getBeanContainer(name).beanInstance();
    }

    public static List<BeanContainer> all() {
        return new ArrayList<>(map.values());
    }
}
