package one.yezii.peanut.core.scan;

import io.github.classgraph.ScanResult;

@FunctionalInterface
/*
    ClassScanResultUser is "the consumer of classgraph scan result"
    the class which deal with class information should implement this.
 */
public interface ScanResultConsumer {
    void consume(ScanResult scanResult);
}
