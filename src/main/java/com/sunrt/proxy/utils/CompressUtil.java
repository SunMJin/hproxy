package com.sunrt.proxy.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import org.xerial.snappy.Snappy;

import java.io.IOException;

public class CompressUtil {
    public static ByteBuf compressByteBuf(ByteBuf byteBuf) throws IOException {
        byte data[]=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        ReferenceCountUtil.release(byteBuf);
        byte arr[]=Snappy.compress(data);
        ByteBuf newBuf= Unpooled.buffer(arr.length);
        return newBuf.writeBytes(arr);
    }
    public static ByteBuf uncompressByteBuf(ByteBuf byteBuf) throws IOException {
        byte data[]=new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(data);
        ReferenceCountUtil.release(byteBuf);
        byte arr[]=Snappy.uncompress(data);
        ByteBuf newBuf= Unpooled.buffer(arr.length);
        return newBuf.writeBytes(arr);
    }
}
