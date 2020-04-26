package one.yezii.peanut.core.ioc.scan;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClassScanner {
    private List<ScanResultConsumer> scanResultConsumers = new ArrayList<>();

    public void scan(String pkg) {
        try (ScanResult scanResult = new ClassGraph().verbose(false).enableAllInfo().whitelistPackages(pkg).scan()) {
            scanResultConsumers.forEach(s -> s.consume(scanResult));
        }
    }

    public ClassScanner addScanResultConsumer(ScanResultConsumer... consumers) {
        scanResultConsumers.addAll(Arrays.asList(consumers));
        return this;
    }
}
