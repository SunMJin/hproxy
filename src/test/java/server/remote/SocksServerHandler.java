package server.remote;

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
        if (socksRequest instanceof Socks5InitialRequest) {
            //ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
            ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
        }/* else if (socksRequest instanceof Socks5PasswordAuthRequest) {
            ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
            ctx.write(new DefaultSocks5PasswordAuthResponse(Socks5PasswordAuthStatus.SUCCESS));
        } */else if (socksRequest instanceof Socks5CommandRequest) {
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
