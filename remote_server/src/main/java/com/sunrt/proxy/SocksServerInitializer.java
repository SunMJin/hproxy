package com.sunrt.proxy;

import com.sunrt.proxy.encryption.CryptFactory;
import com.sunrt.proxy.protocol.CryptDecode;
import com.sunrt.proxy.protocol.CryptEncode;
import com.sunrt.proxy.protocol.SSCommon;
import com.sunrt.proxy.utils.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {

    private ServerConfig serverConfig;

    public SocksServerInitializer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ch.attr(SSCommon.CIPHER).set(CryptFactory.get(serverConfig.method, serverConfig.password));
        ch.pipeline().addLast(new CryptEncode());

        ch.pipeline().addLast(new CryptDecode());
        ch.pipeline().addLast(new SocksServerConnectHandler());
    }
}
