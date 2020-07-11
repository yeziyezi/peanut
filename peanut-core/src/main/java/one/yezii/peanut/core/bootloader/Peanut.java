package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.configuration.HttpServerConfiguration;
import one.yezii.peanut.core.configuration.PropertiesLoader;
import one.yezii.peanut.core.exceptions.BootFailedError;
import one.yezii.peanut.core.facade.PeanutRegister;
import one.yezii.peanut.core.http.server.HttpServer;
import one.yezii.peanut.core.ioc.BeanRepository;
import one.yezii.peanut.core.ioc.BeanScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());
    private List<String> packageScanList = new ArrayList<>();

    private Peanut() {
    }

    public static Peanut peanut(Class<?> bootClass) {
        Peanut peanut = new Peanut();
        try {
            peanut.init(bootClass);
        } catch (Exception e) {
            throw new BootFailedError(e);
        }
        return peanut;
    }


    public Peanut registerPackage(String... packageNames) {
        packageScanList.addAll(Arrays.asList(packageNames));
        return this;
    }

    public void init(Class<?> bootClass) throws Exception {
        String[] extendPackages = ServiceLoader.load(PeanutRegister.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .map(PeanutRegister::packageName)
                .collect(Collectors.toList())
                .toArray(String[]::new);
        registerPackage(bootClass.getPackageName());
        registerPackage(extendPackages);
        PropertiesLoader.load();
        new BeanScanner().scan(packageScanList.toArray(String[]::new));
    }

    public void run() {
        try {
            BeanRepository.runners.forEach((k, v) -> v.run());
            checkConfigurationThenStartHttpServer();
            logger.info("Peanut Application started.");
        } catch (Exception e) {
            throw new BootFailedError(e);
        }
    }

    public Object getBean(String name) {
        return BeanRepository.beans.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> tClass) {
        return (T) getBean(tClass.getName());
    }

    private void checkConfigurationThenStartHttpServer() throws InterruptedException {
        HttpServerConfiguration configuration = new HttpServerConfiguration();
        if (configuration.enableServer) {
            new HttpServer().listen(configuration.port).start();
        }
    }
}
