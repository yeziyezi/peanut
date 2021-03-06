package one.yezii.peanut.core.configuration;

import static one.yezii.peanut.core.configuration.PropertiesLoader.getBool;
import static one.yezii.peanut.core.configuration.PropertiesLoader.getInt;

public class HttpServerConfiguration {
    public int port = getInt("server.port");
    public boolean enableServer = getBool("server.enabled");
}
