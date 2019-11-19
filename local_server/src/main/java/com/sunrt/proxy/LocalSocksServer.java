package com.sunrt.proxy;

import com.sunrt.proxy.utils.LocalConf;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public final class LocalSocksServer {

    static final int PORT = LocalConf.localPort;

    public static void main(String[] args) throws Exception {
        EventLoopGroup allGroup = new NioEventLoopGroup(LocalConf.thread);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(allGroup)
             .channel(NioServerSocketChannel.class)
             .childHandler(new SocksServerInitializer());
            ChannelFuture channelFuture=b.bind(PORT).sync().channel().closeFuture();
            System.out.println("local server started successfully!");
            channelFuture.sync();
        } finally {
            allGroup.shutdownGracefully();
        }
    }
}
