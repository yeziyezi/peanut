package one.yezii.peanut.core.http.paramparsing;

import one.yezii.peanut.core.util.CommonMap;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static one.yezii.peanut.core.bean.UtilBeans.objectMapper;

public class RequestParamParser {
    private static Map<Class<?>, StringObjectParser> basicParserMap = new HashMap<>();

    static {
        basicParserMap.put(String.class, String::valueOf);
        basicParserMap.put(Integer.class, Integer::parseInt);
        basicParserMap.put(Integer.TYPE, Integer::parseInt);
        basicParserMap.put(Boolean.class, Boolean::parseBoolean);
        basicParserMap.put(Boolean.TYPE, Boolean::parseBoolean);
        basicParserMap.put(Long.class, Long::parseLong);
        basicParserMap.put(Long.TYPE, Long::parseLong);
        basicParserMap.put(Float.class, Float::parseFloat);
        basicParserMap.put(Float.TYPE, Float::parseFloat);
        basicParserMap.put(Double.class, Double::parseDouble);
        basicParserMap.put(Double.TYPE, Double::parseDouble);
        basicParserMap.put(Byte.class, Byte::parseByte);
        basicParserMap.put(Byte.TYPE, Byte::parseByte);
    }

    private Parameter[] routeParams;
    private CommonMap requestParams;

    private RequestParamParser() {
    }

    public static RequestParamParser of(Parameter[] routeParams, CommonMap requestParams) {
        RequestParamParser parser = new RequestParamParser();
        parser.routeParams = routeParams;
        parser.requestParams = requestParams;
        return parser;
    }


    private Object parse(Parameter parameter) {
        String valueString = Optional.ofNullable(requestParams.get(parameter.getName()))
                .map(Object::toString).orElse(null);
        StringObjectParser parser = basicParserMap.get(parameter.getType());
        //if parameter has a basic type,use basic parser
        if (parser != null) {
            if (valueString == null) {
                return null;
            }
            return parser.parse(valueString);
        }
        //if not,use jackson parser to parse complex type
        return objectMapper.convertValue(requestParams, parameter.getType());
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
