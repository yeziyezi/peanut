package one.yezii.peanut.core.http;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.*;
import one.yezii.peanut.core.context.GlobalContext;

import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;

public class RequestHandler {
    public FullHttpResponse handle(FullHttpRequest fullHttpRequest) {
        if (GlobalContext.routeMap.containsKey(fullHttpRequest.uri())) {
            Method method = GlobalContext.routeMap.get(fullHttpRequest.uri());
            try {
                String responseBody = (String) method.invoke(
                        GlobalContext.beans.get(method.getDeclaringClass().getName()), "hello");
                return getFullHttpResponse200(responseBody);
            } catch (Exception e) {
                e.printStackTrace();
                return getFullHttpResponse500();
            }
        } else {
            return getFullHttpResponse(HttpResponseStatus.NOT_FOUND, "404 NOT FOUND");
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

    private FullHttpResponse getFullHttpResponse500() {
        return getFullHttpResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, "500 internal server error");
    }
}
