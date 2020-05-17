package one.yezii.peanut.core.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaExceptionWrapper {
    public static <T, R> Function<T, R> wrap(CheckedFunction<T, R> checkedFunction) {
        return t -> {
            try {
                return checkedFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Consumer<T> wrapVoid(CheckedFunctionVoid<T> checkedFunctionVoid) {
        return t -> {
            try {
                checkedFunctionVoid.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface CheckedFunctionVoid<T> {
        void apply(T t) throws Exception;
    }
}
