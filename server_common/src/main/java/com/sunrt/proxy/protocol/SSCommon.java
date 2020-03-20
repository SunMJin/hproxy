package com.sunrt.proxy.protocol;

import com.sunrt.proxy.encryption.ICrypt;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public class SSCommon {
    public static final AttributeKey<ICrypt> CIPHER = AttributeKey.valueOf("sscipher");
}
