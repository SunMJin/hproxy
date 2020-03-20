package com.sunrt.proxy;

import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

public class test {
    public static void main(String[] args) {
        AESEngine engine = new AESEngine();
        StreamBlockCipher cipher = new CFBBlockCipher(engine, 16 * 8);
       // cipher.init(isEncrypt, cipherParameters);
        String str="123";
        byte[]data=str.getBytes();
        byte buffer[]=new byte[data.length];

        int noBytesProcessed = cipher.processBytes(str.getBytes(), 0, data.length, buffer,
                0);
        System.out.println(new String(data));
        System.out.println(new String(buffer,0,noBytesProcessed));
        //stream.write(buffer, 0, noBytesProcessed);
    }
}
