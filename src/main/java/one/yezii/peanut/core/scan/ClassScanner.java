package one.yezii.peanut.core.scan;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    private List<ClassScanResultConsumer> classScanResultConsumers = new ArrayList<>();

    /*
    the consumers add in list will consumer order by the index in list
     */
    public ClassScanner addScanResultConsumer(ClassScanResultConsumer consumer) {
        classScanResultConsumers.add(consumer);
        return this;
    }

    public void scan(String pkg) {
        try (ScanResult scanResult = new ClassGraph().verbose(false).enableAllInfo().whitelistPackages(pkg).scan()) {
            classScanResultConsumers.forEach(consumer -> consumer.consume(scanResult));
        }
    }
}
