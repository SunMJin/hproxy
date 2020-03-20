package com.sunrt.proxy;

import com.sunrt.proxy.protocol.SSAddrRequest;
import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<ByteBuf> {

    final Bootstrap b = new Bootstrap();
    Channel remote_channel;
    public List<ByteBuf> list=new ArrayList<>();

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        SSAddrRequest addrRequest = SSAddrRequest.getAddrRequest(msg);
        if(addrRequest!=null){
            InetSocketAddress addr = new InetSocketAddress(addrRequest.host(), addrRequest.port());
            list.add(msg.retain());
            final Channel inboundChannel = ctx.channel();
            Promise<Channel> promise = ctx.executor().newPromise();
            promise.addListener(
                    (FutureListener<Channel>) future -> {
                        final Channel remote_channel = future.getNow();
                        if (future.isSuccess()) {
                            this.remote_channel=remote_channel;
                            for(ByteBuf b:list){
                                remote_channel.write(b);
                            }
                            remote_channel.flush();
                            if(ctx.channel().isOpen()){
                                ctx.pipeline().remove(SocksServerConnectHandler.this);
                                ctx.pipeline().addLast(new OutRelayHandler(remote_channel));
                            }
                            remote_channel.pipeline().addLast(new InRelayHandler(ctx.channel()));
                        } else {
                            SocksServerUtils.closeOnFlush(ctx.channel());
                        }
                    });

            b.group(inboundChannel.eventLoop())
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new DirectClientHandler(promise));
            b.connect(addr).addListener((ChannelFutureListener) future -> {
                if (!future.isSuccess()) {
                    SocksServerUtils.closeOnFlush(ctx.channel());
                }
            });
        }else{
            if(this.remote_channel==null){
                list.add(msg.retain());
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
