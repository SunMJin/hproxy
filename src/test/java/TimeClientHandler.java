import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socks.SocksAuthScheme;
import io.netty.handler.codec.socks.SocksCmdRequest;
import io.netty.handler.codec.socks.SocksInitRequest;
import io.netty.handler.codec.socksx.v5.*;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(TimeClientHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) {


        Socks5CommandRequest socks5CommandRequest=new DefaultSocks5CommandRequest(Socks5CommandType.CONNECT, Socks5AddressType.DOMAIN,"180.163.26.39",80);



        ByteBuf byteBuf = Unpooled.directBuffer();
        byteBuf.writeByte(socks5CommandRequest.version().byteValue());
        byteBuf.writeByte(socks5CommandRequest.type().byteValue());
        byteBuf.writeByte(0x00);
        Socks5AddressType socks5AddressType=socks5CommandRequest.dstAddrType();
        byteBuf.writeByte(socks5CommandRequest.dstAddrType().byteValue());
        int port=socks5CommandRequest.dstPort();
        String host=socks5CommandRequest.dstAddr();
        if(socks5CommandRequest.dstAddrType().equals(Socks5AddressType.IPv4)){
            byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(host));
            byteBuf.writeShort(port);
        }else if(socks5AddressType.equals(Socks5AddressType.DOMAIN)){
            byteBuf.writeByte(host.length());
            byteBuf.writeCharSequence(host, CharsetUtil.US_ASCII);
            byteBuf.writeShort(port);
        }else if(socks5AddressType.equals(Socks5AddressType.IPv6)){
            byteBuf.writeBytes(NetUtil.createByteArrayFromIpAddressString(host));
            byteBuf.writeShort(port);
        }

        List<SocksAuthScheme> list=new ArrayList<>();
        list.add(SocksAuthScheme.NO_AUTH);
        SocksInitRequest socksInitRequest=new SocksInitRequest(list);
        ByteBuf buff = Unpooled.buffer();
        socksInitRequest.encodeAsByteBuf(buff);

        ctx.writeAndFlush(buff);


        ctx.writeAndFlush(byteBuf);
        System.out.println("发送了");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        System.out.println(Arrays.toString(req));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        logger.warning("Unexpected exception from downstream : "
                + cause.getMessage());
        ctx.close();
    }
}