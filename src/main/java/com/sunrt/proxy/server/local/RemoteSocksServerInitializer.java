package com.sunrt.proxy.server.local;

import com.sunrt.proxy.utils.TLSUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5ClientEncoder;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthResponseDecoder;
import io.netty.handler.ssl.SslContext;

public class RemoteSocksServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final SslContext sslCtx= TLSUtil.getClientSslContext();
    private Socks5CommandRequest request;
    private ChannelHandlerContext ctx;

    public RemoteSocksServerInitializer(Socks5CommandRequest request, ChannelHandlerContext ctx) {
        this.request = request;
        this.ctx = ctx;
    }
    static {
        if(sslCtx==null){
            throw new NullPointerException("ssl context error");
        }
    }
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(sslCtx.newHandler(ch.alloc(), "127.0.0.1", 3080));
        ch.pipeline().addLast(Socks5ClientEncoder.DEFAULT);
        ch.pipeline().addLast("Socks5PasswordAuthResponseDecoder",new Socks5PasswordAuthResponseDecoder());
        ch.pipeline().addLast(new RemoteProxySocksHandler(request,ctx));
    }
}
