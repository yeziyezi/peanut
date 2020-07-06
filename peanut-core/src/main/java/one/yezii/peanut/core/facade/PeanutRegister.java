package one.yezii.peanut.core.facade;

/**
 * Implement this at the ROOT of dependency modules
 * and add SPI file in resources folder.
 * For maven project,"SPI file" is the file names one.yezii.peanut.core.facade
 * in META-INF/service.
 * The content of file is the full name of the PeanutRegister implementation class
 */

public interface PeanutRegister {
}
