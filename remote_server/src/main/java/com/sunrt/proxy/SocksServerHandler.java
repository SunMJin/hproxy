package com.sunrt.proxy;

import com.sunrt.proxy.utils.ServerConf;
import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.*;

@ChannelHandler.Sharable
public final class SocksServerHandler extends SimpleChannelInboundHandler<Socks5Message> {

    public static final SocksServerHandler INSTANCE = new SocksServerHandler();

    private SocksServerHandler() { }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Socks5Message socksRequest) throws Exception {
        if (socksRequest instanceof Socks5PasswordAuthRequest) {
            Socks5PasswordAuthRequest socks5PasswordAuthRequest=(Socks5PasswordAuthRequest)socksRequest;
            if(ServerConf.user.equals(socks5PasswordAuthRequest.username())&& ServerConf.password.equals(socks5PasswordAuthRequest.password())){
                ctx.pipeline().addAfter("Socks5PasswordAuthRequestDecoder",null,new Socks5CommandRequestDecoder());
                ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));
            }else {
                ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.FAILURE));
            }
        } else if (socksRequest instanceof Socks5CommandRequest) {
            Socks5CommandRequest socks5CmdRequest = (Socks5CommandRequest) socksRequest;
            if (socks5CmdRequest.type() == Socks5CommandType.CONNECT) {
                ctx.pipeline().addLast(new SocksServerConnectHandler());
                ctx.pipeline().remove(this);
                ctx.fireChannelRead(socksRequest);
            } else {
                ctx.close();
            }
        } else {
            ctx.close();
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
