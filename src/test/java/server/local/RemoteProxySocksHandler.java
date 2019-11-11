package server.local;

import com.sunrt.proxy.coder.MessageProtocolDecoder;
import com.sunrt.proxy.coder.MessageProtocolEncoder;
import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.*;

public class RemoteProxySocksHandler extends SimpleChannelInboundHandler<Socks5Message> {
    private Socks5CommandRequest request;
    private ChannelHandlerContext ctx_local;
    private SocksServerConnectHandler localSocksServerConnectHandler;

    public RemoteProxySocksHandler(Socks5CommandRequest request, ChannelHandlerContext outCtx,
                                   SocksServerConnectHandler localSocksServerConnectHandler) {
        this.request = request;
        this.ctx_local = outCtx;
        this.localSocksServerConnectHandler = localSocksServerConnectHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx_remote, Socks5Message msg) throws Exception {
        if(msg instanceof Socks5InitialResponse){
            Socks5InitialResponse initialResponse=(Socks5InitialResponse)msg;
            if(initialResponse.authMethod()== Socks5AuthMethod.NO_AUTH){
                //ctx_remote.pipeline().addFirst(new Socks5CommandResponseDecoder());
                //ctx_remote.pipeline().addBefore("");
                ctx_remote.pipeline().writeAndFlush(request);
            }
        }else if(msg instanceof Socks5CommandResponse){
            Socks5CommandResponse commandResponse=(Socks5CommandResponse)msg;
            if(commandResponse.status()==Socks5CommandStatus.SUCCESS){
                ChannelFuture responseFuture =
                        ctx_local.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                Socks5CommandStatus.SUCCESS,
                                request.dstAddrType(),
                                request.dstAddr(),
                                request.dstPort()));
                responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                    ctx_remote.pipeline().remove(this);
                    ctx_local.pipeline().remove(localSocksServerConnectHandler);

                    //ctx_remote.pipeline().addLast(new MessageProtocolEncoder());
                    //ctx_remote.pipeline().addFirst(new MessageProtocolDecoder());

                    ctx_remote.pipeline().addLast(new OutRelayHandler(ctx_local.channel()));
                    ctx_local.pipeline().addLast(new InRelayHandler(ctx_remote.channel()));
                });
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.pipeline().writeAndFlush(new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }

}
