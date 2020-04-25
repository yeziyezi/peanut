package one.yezii.peanut.core.scan;

import one.yezii.peanut.core.scan.consumer.ComponentAnnotationScanResultConsumer;

public class ScanResultConsumers {
    public static ComponentAnnotationScanResultConsumer commponentAnnotationScanResultConsumer() {
        return new ComponentAnnotationScanResultConsumer();
    }
}
