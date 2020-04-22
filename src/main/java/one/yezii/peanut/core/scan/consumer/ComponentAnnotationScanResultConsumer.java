package one.yezii.peanut.core.scan.consumer;

import io.github.classgraph.ScanResult;
import one.yezii.peanut.core.constant.ClassName;
import one.yezii.peanut.core.scan.ClassScanResultConsumer;

public class ComponentAnnotationScanResultConsumer implements ClassScanResultConsumer {
    @Override
    public void consume(ScanResult scanResult) {
        scanResult.getClassesWithAnnotation(ClassName.componentAnnotation).forEach(classInfo ->
                System.out.println("[classsssssss]" + classInfo.getName()));
    }
}
