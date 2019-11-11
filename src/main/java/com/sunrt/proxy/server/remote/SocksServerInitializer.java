/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.sunrt.proxy.server.remote;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import io.netty.handler.ssl.SslContext;

public final class SocksServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public SocksServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    public static Socks5InitialRequestDecoder socks5InitialRequestDecoder=new Socks5InitialRequestDecoder();

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        socks5InitialRequestDecoder=new Socks5InitialRequestDecoder();
        ch.pipeline().addLast(
                sslCtx.newHandler(ch.alloc()),
                Socks5ServerEncoder.DEFAULT,
                new Socks5InitialRequestDecoder(),
                new Socks5CommandRequestDecoder(),
                SocksServerHandler.INSTANCE);
    }
}
