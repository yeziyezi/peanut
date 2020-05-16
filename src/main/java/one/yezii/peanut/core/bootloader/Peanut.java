package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.http.HttpServer;
import one.yezii.peanut.core.ioc.BeanManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());
    private boolean enableServer = true;
    private int port = 8080;

    public static <T> Peanut run(Class<T> bootClass) {
        Peanut peanut = new Peanut();
        try {
            peanut.getClasses(bootClass);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        GlobalContext.runners.forEach((k, v) -> v.run());
        if (peanut.enableServer) {
            new HttpServer().listen(peanut.port).start();
        }
        logger.info("Peanut Application started.");
        return peanut;
    }
    public <T> void getClasses(Class<T> bootClass) {
        if (!bootClass.isAnnotationPresent(PeanutBoot.class)) {
            logger.log(Level.SEVERE, "boot class '" + bootClass.getName() + "' without @PeanutBoot annotation");
            System.exit(-1);
        }
        new BeanManager().initBeans(bootClass.getPackageName());
    }
}
