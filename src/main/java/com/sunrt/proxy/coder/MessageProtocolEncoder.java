package com.sunrt.proxy.coder;

import com.sunrt.proxy.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageProtocolEncoder extends MessageToByteEncoder<MessageProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        out.writeInt(msg.getHead_data());
        out.writeInt(msg.getContentLength());
        out.writeBytes(msg.getContent());
    }
}