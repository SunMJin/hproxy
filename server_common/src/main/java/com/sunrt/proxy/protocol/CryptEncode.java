package com.sunrt.proxy.protocol;


import com.sunrt.proxy.encryption.ICrypt;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.ByteArrayOutputStream;

public class CryptEncode extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        ICrypt crypt = ctx.channel().attr(SSCommon.CIPHER).get();
        try {
            ByteArrayOutputStream _remoteOutStream = new ByteArrayOutputStream(64 * 1024);
            int len=msg.readableBytes();
            if(len>0){
                byte arr[]=new byte[len];
                msg.readBytes(arr);
                crypt.encrypt(arr, _remoteOutStream);
                byte x[]=_remoteOutStream.toByteArray();
                out.writeBytes(x);
                if (_remoteOutStream != null) {
                    _remoteOutStream.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
