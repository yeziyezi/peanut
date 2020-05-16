package one.yezii.peanut.core.configuration;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ConfigurationLoader {
    public final static Map<String, String> configs;

    static {
        //get default properties from default.properties
        ResourceBundle resourceBundle = ResourceBundle.getBundle("default");
        configs = resourceBundle.keySet().stream()
                .collect(Collectors.toMap(k -> k, resourceBundle::getString));
    }

    public static String getString(String key) {
        return configs.get(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(getString(key));
    }
}
