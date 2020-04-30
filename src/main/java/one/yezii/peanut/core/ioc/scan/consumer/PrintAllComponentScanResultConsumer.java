package one.yezii.peanut.core.ioc.scan.consumer;

import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.constant.AnnotationName;
import one.yezii.peanut.core.ioc.scan.ScanResultConsumer;

public class PrintAllComponentScanResultConsumer implements ScanResultConsumer {
    @Override
    public void consume(ScanResult scanResult) {
        scanResult.getClassesWithAnnotation(AnnotationName.Component).getNames().forEach(System.out::println);
    }
}
