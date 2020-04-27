package one.yezii.peanut.core.ioc.scan;

import one.yezii.peanut.core.ioc.scan.consumer.ComponentAnnotationScanResultConsumer;
import one.yezii.peanut.core.ioc.scan.consumer.PrintAllComponentScanResultConsumer;
import one.yezii.peanut.core.ioc.scan.consumer.PrintAllScanResultConsumer;

public class ScanResultConsumers {
    public static ScanResultConsumer ComponentAnnotationScanResultConsumer() {
        return new ComponentAnnotationScanResultConsumer();
    }

    public static ScanResultConsumer PrintAllScanResultConsumer() {
        return new PrintAllScanResultConsumer();
    }

    public static ScanResultConsumer PrintAllComponentScanResultConsumer() {
        return new PrintAllComponentScanResultConsumer();
    }
}
