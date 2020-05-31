package one.yezii.peanut.core.http.paramparsing;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BasicParamParser {
    private static Map<Class<?>, StringObjectParser> parserMap = new HashMap<>();

    static {
        parserMap.put(String.class, String::valueOf);
        parserMap.put(Integer.class, Integer::parseInt);
        parserMap.put(Integer.TYPE, Integer::parseInt);
        parserMap.put(Boolean.class, Boolean::parseBoolean);
        parserMap.put(Boolean.TYPE, Boolean::parseBoolean);
        parserMap.put(Long.class, Long::parseLong);
        parserMap.put(Long.TYPE, Long::parseLong);
        parserMap.put(Float.class, Float::parseFloat);
        parserMap.put(Float.TYPE, Float::parseFloat);
        parserMap.put(Double.class, Double::parseDouble);
        parserMap.put(Double.TYPE, Double::parseDouble);
        parserMap.put(Byte.class, Byte::parseByte);
        parserMap.put(Byte.TYPE, Byte::parseByte);
    }

    private Parameter[] routeParams;
    private Map<String, String> requestParams;

    private BasicParamParser() {
    }

    public static BasicParamParser of(Parameter[] routeParams, Map<String, String> requestParams) {
        BasicParamParser parser = new BasicParamParser();
        parser.routeParams = routeParams;
        parser.requestParams = requestParams;
        return parser;
    }


    private Optional<Object> parse(Parameter parameter) {
        String valueString = requestParams.get(parameter.getName());
        StringObjectParser parser = parserMap.get(parameter.getType());
        if (valueString == null || parser == null) {
            return Optional.empty();
        }
        return Optional.of(parser.parse(valueString));
    }

    public ParameterObjectMapping parse() {
        ParameterObjectMapping mapping = new ParameterObjectMapping();
        Arrays.stream(routeParams).forEach(parameter -> mapping.add(parameter, parse(parameter)));
        return mapping;
    }

    @FunctionalInterface
    private interface StringObjectParser {
        Object parse(String s);
    }
}
