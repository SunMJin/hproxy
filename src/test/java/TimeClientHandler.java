import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;

import java.util.logging.Logger;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(TimeClientHandler.class.getName());

    public static void main(String[] args) throws InterruptedException {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(Socks5ClientEncoder.DEFAULT);
                            ch.pipeline().addLast(new Socks5InitialResponseDecoder());
                            ch.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            // 发起异步连接操作
            ChannelFuture f = b.connect("127.0.0.1", 2080).sync();

            // 当代客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Socks5InitialRequest socks5InitialRequest=new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH);
        ctx.writeAndFlush(socks5InitialRequest);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        /*if(msg instanceof Socks5InitialResponse){
            Socks5InitialResponse socks5InitialResponse=(Socks5InitialResponse)msg;
            if(socks5InitialResponse.authMethod()==Socks5AuthMethod.PASSWORD){
                ctx.pipeline().addFirst(new Socks5PasswordAuthResponseDecoder());
                Socks5PasswordAuthRequest socks5PasswordAuthRequest=new DefaultSocks5PasswordAuthRequest("123","456");
                ctx.writeAndFlush(socks5PasswordAuthRequest);
            }
        }*/
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        logger.warning("Unexpected exception from downstream : "
                + cause.getMessage());
        ctx.close();
    }
}