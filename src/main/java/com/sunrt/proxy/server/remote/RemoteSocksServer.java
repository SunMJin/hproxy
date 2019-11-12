package com.sunrt.proxy.server.remote;

import com.sunrt.proxy.utils.TLSUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class RemoteSocksServer {
    static final int PORT = Integer.parseInt(System.getProperty("port", "444"));
    static final SslContext sslCtx = TLSUtil.getServerSslContext();
    static {
        if(sslCtx==null){
            throw new NullPointerException("ssl context error");
        }
    }
    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SocksServerInitializer(sslCtx));
            ChannelFuture channelFuture=b.bind(PORT).sync().channel().closeFuture();
            System.out.println("remote server started successfully!");
            channelFuture.sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
