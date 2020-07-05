package one.yezii.peanut.core.ioc;

import java.util.ArrayList;
import java.util.List;

public class PackageRegister {
    private final static List<String> packages = new ArrayList<>();

    public static void register(String packageName) {
        packages.add(packageName);
    }

    public static String[] list() {
        return packages.toArray(String[]::new);
    }
}
