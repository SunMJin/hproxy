package com.sunrt.proxy.server.remote;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.ssl.SslContext;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SocksServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()));
        ch.pipeline().addLast(Socks5ServerEncoder.DEFAULT);
        ch.pipeline().addLast("Socks5PasswordAuthRequestDecoder",new Socks5PasswordAuthRequestDecoder());
        ch.pipeline().addLast(SocksServerHandler.INSTANCE);
    }
}
