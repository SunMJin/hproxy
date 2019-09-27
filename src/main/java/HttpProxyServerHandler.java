import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {

    private ChannelFuture cf;
    private String ip;
    private int port=80;

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
        ChannelInitializer channelInitializer=null;
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String host=request.headers().get(HttpHeaderNames.HOST);
            String[] temp = host.split(":");
            ip=temp[0];
            if(temp.length==2){
                port=Integer.valueOf(temp[1]);
            }
            System.out.println(request.method().name()+"----"+request.uri());
            if ("CONNECT".equalsIgnoreCase(request.method().name())) {
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, new HttpResponseStatus(200, "Connection established"));
                ctx.writeAndFlush(response);
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpObject");
                ReferenceCountUtil.release(msg);
                return;
            }
            channelInitializer=new HttpProxyInitializer(ctx.channel());
        }else{
            channelInitializer= new ChannelInitializer() {
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                        @Override
                        public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                            ctx.channel().writeAndFlush(msg).sync();
                        }
                    });
                }
            };
        }
        proxy(ctx,msg,channelInitializer);
    }



    public void proxy(final ChannelHandlerContext ctx, final Object msg,ChannelInitializer channelInitializer){
        if(cf==null){
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer);
            cf = bootstrap.connect(ip, port);
            cf.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        ctx.channel().close();
                    }
                }
            });
        }else{
            cf.channel().writeAndFlush(msg);
        }
    }
}
