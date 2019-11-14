package com.sunrt.proxy;

import com.sunrt.proxy.utils.Conf;
import com.sunrt.proxy.utils.TLSUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.util.NettyRuntime;
import io.netty.util.internal.SystemPropertyUtil;

public final class RemoteSocksServer {
    static final int PORT = Conf.remotePort;
    static final SslContext sslCtx = TLSUtil.getServerSslContext();
    static {
        if(sslCtx==null){
            throw new NullPointerException("ssl context error");
        }
    }
    public static void main(String[] args) throws Exception {
        EventLoopGroup allGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors()*8);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(allGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SocksServerInitializer(sslCtx));
            ChannelFuture channelFuture=b.bind(PORT).sync().channel().closeFuture();
            System.out.println("remote server started successfully!");
            channelFuture.sync();
        } finally {
            allGroup.shutdownGracefully();
        }
    }
}
