package one.yezii.peanut.core.http.server;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.*;
import one.yezii.peanut.core.http.MethodInvoker;
import one.yezii.peanut.core.http.paramparsing.ParameterObjectMapping;
import one.yezii.peanut.core.http.paramparsing.RequestParamParser;
import one.yezii.peanut.core.http.route.UriRoute;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static one.yezii.peanut.core.context.GlobalContext.routeMap;

public class RequestHandler {

    public FullHttpResponse handle(FullHttpRequest request) {
        UriRoute uriRoute = UriRoute.of(request.uri(), request.method().name());
        if (!routeMap.containsKey(uriRoute)) {
            return notFound();
        }
        try {
            MethodInvoker methodInvoker = routeMap.get(uriRoute);
            //todo: add resolver of request body and fill result in map
            Map<String, String> requestParamMap = uriRoute.uriParam();
            ParameterObjectMapping poMapping = RequestParamParser.of(
                    methodInvoker.parameters(), requestParamMap).parse();
            Object result = methodInvoker.invoke(poMapping.toArray());
            return ok(result == null ? null : result.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return badRequest();
        } catch (Exception e) {
            e.printStackTrace();
            return internalServerError();
        }
    }

    private FullHttpResponse response(HttpResponseStatus status, String content) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, CharBuffer.wrap(content),
                        StandardCharsets.UTF_8));
    }

    private FullHttpResponse ok(String responseBody) {
        return response(HttpResponseStatus.OK, responseBody);
    }

    private FullHttpResponse notFound() {
        return response(NOT_FOUND, NOT_FOUND.toString());
    }

    private FullHttpResponse badRequest() {
        return response(BAD_REQUEST, BAD_REQUEST.toString());
    }

    private FullHttpResponse internalServerError() {
        return response(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.toString());
    }
}
