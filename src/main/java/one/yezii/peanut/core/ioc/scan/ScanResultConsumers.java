package one.yezii.peanut.core.ioc.scan;

import one.yezii.peanut.core.ioc.scan.consumer.ComponentAnnotationScanResultConsumer;

public class ScanResultConsumers {
    public static ComponentAnnotationScanResultConsumer componentAnnotationScanResultConsumer() {
        return new ComponentAnnotationScanResultConsumer();
    }
}
