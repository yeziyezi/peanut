package one.yezii.peanut.core.http;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.*;
import one.yezii.peanut.core.context.GlobalContext;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

public class RequestHandler {

    public FullHttpResponse handle(FullHttpRequest request) {
        //Uri does not support path param
        UriRoute uriRoute = UriRoute.of(request.uri(), request.method().name());
        if (!GlobalContext.routeMap.containsKey(uriRoute.hash())) {
            return getFullHttpResponse404();
        }
        //todo: execute method call here.
        try {
            return getFullHttpResponse200("route:" + uriRoute.routeUri());
        } catch (Exception e) {
            e.printStackTrace();
            return getFullHttpResponse500();
        }
    }

    private FullHttpResponse getFullHttpResponse(HttpResponseStatus status, String content) {
        return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, CharBuffer.wrap(content),
                        StandardCharsets.UTF_8));
    }

    private FullHttpResponse getFullHttpResponse200(String responseBody) {
        return getFullHttpResponse(HttpResponseStatus.OK, responseBody);
    }

    private FullHttpResponse getFullHttpResponse404() {
        return getFullHttpResponse(NOT_FOUND, NOT_FOUND.toString());
    }

    private FullHttpResponse getFullHttpResponse500() {
        return getFullHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.toString());
    }
}
