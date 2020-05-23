package one.yezii.peanut.core.http;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UriParamParser {
    private static HashMap<Class<?>, StringObjectParser> basicParserMap = new HashMap<>();

    static {
        basicParserMap.put(Integer.TYPE, Integer::parseInt);
        basicParserMap.put(String.class, String::valueOf);
        basicParserMap.put(Boolean.TYPE, Boolean::parseBoolean);
        basicParserMap.put(Long.TYPE, Long::parseLong);
        basicParserMap.put(Float.TYPE, Float::parseFloat);
        basicParserMap.put(Double.TYPE, Double::parseDouble);
        basicParserMap.put(Byte.TYPE, Byte::parseByte);
    }

    private Parameter[] parameters;
    private Map<String, String> uriParams;

    private UriParamParser() {
    }

    public static UriParamParser of(Parameter[] parameters, Map<String, String> uriParams) {
        UriParamParser parser = new UriParamParser();
        parser.parameters = parameters;
        parser.uriParams = uriParams;
        return parser;
    }


    private Object parse(Parameter parameter) {
        String uriValueString = uriParams.get(parameter.getName());
        StringObjectParser parser = basicParserMap.get(parameter.getType());
        if (uriValueString == null || parser == null) {
            return null;
        }
        return parser.parse(parameter.getName());
    }

    public Object[] parse() {
        return Arrays.stream(parameters).map(this::parse).toArray();
    }

    @FunctionalInterface
    private interface StringObjectParser {
        Object parse(String s);
    }
}
