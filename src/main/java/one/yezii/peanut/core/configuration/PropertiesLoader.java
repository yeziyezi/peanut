package one.yezii.peanut.core.configuration;

import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PropertiesLoader {
    private static HashMap<String, String> properties = new HashMap<>();

    public static void load() {
        ResourceBundle defaultResource = PropertyResourceBundle.getBundle("default");
        loadPropertiesFromBundle(defaultResource);
        ResourceBundle customResource = null;
        try {
            customResource = PropertyResourceBundle.getBundle("peanut");
            loadPropertiesFromBundle(customResource);
        } catch (MissingResourceException e) {
            throwMissingResourceException("peanut.properties doesn't exist");
        }
        if (customResource.containsKey("peanut.active")) {
            String[] actives = customResource.getString("peanut.active").split(",");
            for (String active : actives) {
                try {
                    loadPropertiesFromBundle(PropertyResourceBundle.getBundle(active));
                } catch (MissingResourceException e) {
                    throwMissingResourceException(active + ".properties doesn't exist");
                }
            }
        }
    }

    private static void loadPropertiesFromBundle(ResourceBundle bundle) {
        properties.putAll(bundle.keySet().stream().collect(Collectors.toMap(k -> k, bundle::getString)));
    }

    public static String getString(String key) {
        if (properties.get(key) == null) {
            throwMissingResourceException("could not found property with key [" + key + "]");
        }
        return properties.get(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(getString(key));
    }

    private static void throwMissingResourceException(String msg) {
        throw new MissingResourceException(msg, PropertiesLoader.class.getName(), "");
    }
}
