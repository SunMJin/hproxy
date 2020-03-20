package com.sunrt.proxy.protocol;


import com.sunrt.proxy.encryption.ICrypt;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public final class CryptDecode extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ICrypt crypt = ctx.channel().attr(SSCommon.CIPHER).get();
        try {
            ByteArrayOutputStream _localOutStream = new ByteArrayOutputStream(64 * 1024);
            int len=in.readableBytes();
            if(len>0){
                byte arr[] = new byte[len];
                in.readBytes(arr);
                crypt.decrypt(arr, _localOutStream);
                byte x[]=_localOutStream.toByteArray();
                out.add(Unpooled.wrappedBuffer(x));
                if (_localOutStream != null) {
                    try {
                        _localOutStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}