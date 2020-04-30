package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.ioc.BeanManager;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());

    public static <T> void eat(Class<T> bootClass) {
        Peanut peanut = new Peanut();
        try {
            peanut.getClasses(bootClass);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info("Peanut Application started.");
        GlobalContext.runners.forEach((k, v) -> v.run());
    }

    public <T> void getClasses(Class<T> bootClass) {
        if (!bootClass.isAnnotationPresent(PeanutBoot.class)) {
            logger.log(Level.SEVERE, "boot class '" + bootClass.getName() + "' without @PeanutBoot annotation");
            System.exit(-1);
        }
        new BeanManager().initBeans(bootClass.getPackageName());
    }
}
