package com.sunrt.proxy.server.local;

import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

import javax.net.ssl.SSLException;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<Socks5Message> {

    private final Bootstrap b = new Bootstrap();
    private static SslContext sslCtx;

    static {
        try {
            sslCtx = SslContextBuilder.forClient().protocols("TLSv1.3").trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final Socks5Message message) throws Exception {
        final Socks5CommandRequest request = (Socks5CommandRequest) message;
        b.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), "127.0.0.1", 3080));
                        ch.pipeline().addLast(Socks5ClientEncoder.DEFAULT);
                        ch.pipeline().addLast("Socks5PasswordAuthResponseDecoder",new Socks5PasswordAuthResponseDecoder());
                        ch.pipeline().addLast(new RemoteProxySocksHandler(request,ctx, SocksServerConnectHandler.this));
                    }
                });
        b.connect("127.0.0.1", 3080).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
            } else {
                ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                SocksServerUtils.closeOnFlush(ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
