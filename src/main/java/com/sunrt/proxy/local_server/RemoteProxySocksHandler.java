package com.sunrt.proxy.local_server;

import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.v5.*;

public class RemoteProxySocksHandler extends ChannelInboundHandlerAdapter {
    private Socks5CommandRequest request;
    private ChannelHandlerContext outCtx;
    private com.sunrt.proxy.local_server.SocksServerConnectHandler socksServerConnectHandler;

    public RemoteProxySocksHandler(Socks5CommandRequest request, ChannelHandlerContext outCtx,
                                   com.sunrt.proxy.local_server.SocksServerConnectHandler socksServerConnectHandler) {
        this.request = request;
        this.outCtx = outCtx;
        this.socksServerConnectHandler = socksServerConnectHandler;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof Socks5InitialResponse){
            Socks5InitialResponse initialResponse=(Socks5InitialResponse)msg;
            if(initialResponse.authMethod()== Socks5AuthMethod.NO_AUTH){
                ctx.pipeline().addFirst(new Socks5CommandResponseDecoder());
                ctx.pipeline().writeAndFlush(request);
            }
        }else if(msg instanceof Socks5CommandResponse){
            Socks5CommandResponse commandResponse=(Socks5CommandResponse)msg;
            if(commandResponse.status()==Socks5CommandStatus.SUCCESS){
                ChannelFuture responseFuture =
                        outCtx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                request.dstAddrType(),
                                request.dstAddr(),
                                request.dstPort()));
                responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                    ctx.pipeline().remove(this);
                    outCtx.pipeline().remove(socksServerConnectHandler);
                    ctx.pipeline().addLast(new OutRelayHandler(outCtx.channel()));
                    outCtx.pipeline().addLast(new InRelayHandler(ctx.channel()));
                });
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().writeAndFlush(new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH)).addListener((ChannelFutureListener) future -> {
            ctx.pipeline().writeAndFlush(request);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }

}
