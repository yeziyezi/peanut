package one.yezii.peanut.core.http;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.*;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

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
            //parse uri Param to the type same with the method parameter type
            //now only support simple type like String and primitive types.
            Object[] methodParams = UriParamParser.of(methodInvoker.getMethod().getParameters()
                    , uriRoute.uriParam()).parse();
            Object result = methodInvoker.invoke(methodParams);
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
