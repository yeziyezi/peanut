package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.context.GlobalContext;
import one.yezii.peanut.core.scan.ClassScanner;
import one.yezii.peanut.core.scan.consumer.ComponentAnnotationScanResultConsumer;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());
    private Set<Object> components = new HashSet<>();

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
            logger.log(Level.SEVERE, "boot class " + bootClass.getName() + "without @PeanutBoot annotation");
            System.exit(-1);
        }
        //todo
        new ClassScanner().addScanResultConsumer(new ComponentAnnotationScanResultConsumer())
                .scan(bootClass.getPackageName());
    }
}
