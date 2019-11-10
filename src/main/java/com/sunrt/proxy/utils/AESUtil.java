package com.sunrt.proxy.utils;

import com.sunrt.proxy.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    private static final byte[] raw = "BywOYACPCxiYAMQJ".getBytes();
    private static final SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");

    public static byte[] encrypt(byte[] sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(sSrc);
    }

    public static byte[] decrypt(byte[] sSrc) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(sSrc);
    }

    public static MessageProtocol encrypt(ByteBuf byteBuf) throws Exception {
        byte buf[]=getBytesByByteBuf(byteBuf);
        byte data[]=AESUtil.encrypt(buf);
        MessageProtocol messageProtocol=new MessageProtocol(data.length,data);
        return messageProtocol;
    }

    public static ByteBuf decrypt(MessageProtocol messageProtocol) throws Exception {
        byte buf[]=messageProtocol.getContent();
        ByteBuf newBuf=Unpooled.buffer(buf.length);
        newBuf.writeBytes(decrypt(buf));
        return newBuf;
    }

    public static byte[] getBytesByByteBuf(ByteBuf byteBuf){
        byte buf[]=new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,buf);
        ReferenceCountUtil.release(byteBuf);
        return buf;
    }

}