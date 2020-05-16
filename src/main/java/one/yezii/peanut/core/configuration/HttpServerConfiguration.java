package one.yezii.peanut.core.configuration;

import static one.yezii.peanut.core.configuration.ConfigurationLoader.getInt;

public class HttpServerConfiguration {
    public int port = getInt("server.port");
    public boolean enableServer = ConfigurationLoader.getBool("server.enabled");
}
