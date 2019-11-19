package com.sunrt.proxy;

import com.sunrt.proxy.utils.LocalConf;
import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.*;

public class RemoteProxySocksHandler extends SimpleChannelInboundHandler<Socks5Message> {

    private final Socks5CommandRequest request;
    private final ChannelHandlerContext ctx_local;

    public RemoteProxySocksHandler(Socks5CommandRequest request, ChannelHandlerContext outCtx) {
        this.request = request;
        this.ctx_local = outCtx;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx_remote, Socks5Message msg) throws Exception {
        if(msg instanceof Socks5PasswordAuthResponse){
            Socks5PasswordAuthResponse socks5PasswordAuthResponse=(Socks5PasswordAuthResponse)msg;
            if(socks5PasswordAuthResponse.status().isSuccess()){
                ctx_remote.pipeline().addAfter("Socks5PasswordAuthResponseDecoder", null, new Socks5CommandResponseDecoder());
                ctx_remote.pipeline().writeAndFlush(request);
            }else{
                socks5CmdCallBack(false);
            }
        }else if(msg instanceof Socks5CommandResponse){
            Socks5CommandResponse commandResponse=(Socks5CommandResponse)msg;
            if(commandResponse.status()==Socks5CommandStatus.SUCCESS){
                socks5CmdCallBack(true).addListener((ChannelFutureListener) channelFuture -> {
                    ctx_remote.pipeline().remove(this);
                    ctx_remote.pipeline().addLast(new OutRelayHandler(ctx_local.channel()));
                    ctx_local.pipeline().addLast(new InRelayHandler(ctx_remote.channel()));
                });
            }else{
                socks5CmdCallBack(false);
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().writeAndFlush(new DefaultSocks5PasswordAuthRequest(LocalConf.user, LocalConf.password));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }

    public ChannelFuture socks5CmdCallBack(boolean isSuccess){
        if(isSuccess){
            return ctx_local.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                    Socks5CommandStatus.SUCCESS,
                    request.dstAddrType(),
                    request.dstAddr(),
                    request.dstPort()));
        }else{
            return ctx_local.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                    Socks5CommandStatus.FAILURE,
                    request.dstAddrType(),
                    request.dstAddr(),
                    request.dstPort()));
        }
    }

}
