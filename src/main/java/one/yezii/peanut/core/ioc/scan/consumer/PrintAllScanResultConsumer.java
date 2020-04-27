package one.yezii.peanut.core.ioc.scan.consumer;

import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.ioc.scan.ScanResultConsumer;

public class PrintAllScanResultConsumer implements ScanResultConsumer {
    @Override
    public void consume(ScanResult scanResult) {
        scanResult.getAllClassesAsMap().forEach((k, v) -> System.out.println(k));
    }
}
