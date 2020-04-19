package one.yezii.peanut.core.bootloader;

import one.yezii.peanut.core.ClassScanner;
import one.yezii.peanut.core.annotation.Autowired;
import one.yezii.peanut.core.annotation.Component;
import one.yezii.peanut.core.annotation.PeanutBoot;
import one.yezii.peanut.core.facade.PeanutRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Peanut {
    private static Logger logger = Logger.getLogger(Peanut.class.toGenericString());
    private Set<PeanutRunner> runners = new HashSet<>();
    private Set<Object> components = new HashSet<>();

    public static <T> void eat(Class<T> clazz) {
        Peanut peanut = new Peanut();
        try {
            peanut.getClasses(clazz);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
        peanut.runners.forEach(PeanutRunner::run);
        logger.info("Peanut Application started.");
    }

    public <T> void getClasses(Class<T> bootClass) throws IOException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!bootClass.isAnnotationPresent(PeanutBoot.class)) {
            logger.log(Level.SEVERE, "boot class " + bootClass.getName() + "without @PeanutBoot annotation");
            System.exit(-1);
        }
        Set<Class<?>> classes = new ClassScanner().scan(bootClass.getPackageName());
        for (Class<?> c : classes) {//todo
            if (c.isAnnotationPresent(Component.class)) {
                Object object = c.getConstructors()[0].newInstance();
                for (Field field : c.getFields()) {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        field.set(object, field.getType().getConstructors()[0].newInstance());
                    }
                }
            }
//            if (c.getAnnotation(Component.class) != null
//                    && Arrays.asList(c.getInterfaces()).contains(PeanutRunner.class)) {
//                runners.add((PeanutRunner) c.getConstructors()[0].newInstance());
//            }
        }
    }
}
