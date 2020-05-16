package one.yezii.peanut.core.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

import java.util.logging.Logger;

public class HttpServer {
    private int port;
    private static Logger logger = Logger.getLogger(HttpServer.class.toGenericString());

    public HttpServer listen(int port) {
        this.port = port;
        return this;
    }

    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast("codec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))
                                .addLast("requestHandler", new SimpleChannelInboundHandler())
                                .addLast("compressor", new HttpContentCompressor());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler());
                    }
                });
        try {
            logger.info("http server is listening on port " + port);
            serverBootstrap.bind().sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
