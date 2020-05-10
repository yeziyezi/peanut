package one.yezii.peanut.core.util;

public class ExceptionUtil {
    public static void throwRE(RuntimeException runtimeException) {
        throw runtimeException;
    }

    public static void throwRE(String msg) {
        throwRE(new RuntimeException(msg));
    }
}
