package one.yezii.peanut.core.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

@ChannelHandler.Sharable
public class SimpleChannelInboundHandler extends ChannelInboundHandlerAdapter {
    private RequestHandler requestHandler = new RequestHandler();

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) {
        FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
        System.out.println("request uri:" + fullHttpRequest.uri());
        context.write(requestHandler.handle(fullHttpRequest));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
