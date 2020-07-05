package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.configuration.HttpServerConfiguration;
import one.yezii.peanut.core.configuration.PropertiesLoader;
import one.yezii.peanut.core.http.server.HttpServer;
import one.yezii.peanut.core.ioc.BeanRepository;
import one.yezii.peanut.core.ioc.BeanScanner;
import one.yezii.peanut.core.ioc.PackageRegister;

import java.util.logging.Logger;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());

    public static <T> void run(Class<T> bootClass) {
        try {
            new Peanut().boot(bootClass);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private <T> void boot(Class<T> startClass) throws Exception {
        String bootloaderPackageName = Peanut.class.getPackageName();
        String corePackage = bootloaderPackageName.substring(bootloaderPackageName.lastIndexOf("."));
        PackageRegister.register(corePackage);
        PropertiesLoader.load();
        scanBeans(startClass);
        BeanRepository.runners.forEach((k, v) -> v.run());
        startHttpServer();
        logger.info("Peanut Application started.");
    }

    private <T> void scanBeans(Class<T> startClass) throws Exception {
        if (!startClass.isAnnotationPresent(PeanutBoot.class)) {
            throw new RuntimeException("boot class '" + startClass.getName() + "' without @PeanutBoot annotation");
        }
        PackageRegister.register(startClass.getPackageName());
        new BeanScanner().scan();
    }

    private void startHttpServer() throws InterruptedException {
        HttpServerConfiguration configuration = new HttpServerConfiguration();
        if (configuration.enableServer) {
            new HttpServer().listen(configuration.port).start();
        }
    }
}
