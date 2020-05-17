package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.configuration.HttpServerConfiguration;
import one.yezii.peanut.core.configuration.PropertiesLoader;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.http.HttpServer;
import one.yezii.peanut.core.ioc.BeanManager;

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

    private <T> void boot(Class<T> bootClass) throws Exception {
        PropertiesLoader.load();
        getClasses(bootClass);
        GlobalContext.runners.forEach((k, v) -> v.run());
        startHttpServer();
        logger.info("Peanut Application started.");
    }

    private <T> void getClasses(Class<T> bootClass) {
        if (!bootClass.isAnnotationPresent(PeanutBoot.class)) {
            throw new RuntimeException("boot class '" + bootClass.getName() + "' without @PeanutBoot annotation");
        }
        new BeanManager().initBeans(bootClass.getPackageName());
    }

    private void startHttpServer() throws InterruptedException {
        HttpServerConfiguration configuration = new HttpServerConfiguration();
        if (configuration.enableServer) {
            new HttpServer().listen(configuration.port).start();
        }
    }
}
