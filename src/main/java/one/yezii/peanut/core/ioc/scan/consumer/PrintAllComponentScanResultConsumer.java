package one.yezii.peanut.core.ioc.scan.consumer;

import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.constant.ClassName;
import one.yezii.peanut.core.ioc.scan.ScanResultConsumer;

public class PrintAllComponentScanResultConsumer implements ScanResultConsumer {
    @Override
    public void consume(ScanResult scanResult) {
        scanResult.getClassesWithAnnotation(ClassName.componentAnnotation).getNames().forEach(System.out::println);
    }
}
