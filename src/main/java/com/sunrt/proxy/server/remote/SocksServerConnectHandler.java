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

import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandResponse;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequest;
import io.netty.handler.codec.socksx.v5.Socks5CommandStatus;
import io.netty.handler.codec.socksx.v5.Socks5Message;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

@ChannelHandler.Sharable
public final class SocksServerConnectHandler extends SimpleChannelInboundHandler<Socks5Message> {

    private final Bootstrap b = new Bootstrap();

    @Override
    public void channelRead0(final ChannelHandlerContext server_ctx, final Socks5Message message) throws Exception {
        final Socks5CommandRequest request = (Socks5CommandRequest) message;
        Promise<Channel> promise = server_ctx.executor().newPromise();
        promise.addListener(
                (FutureListener<Channel>) future -> {
                    final Channel remote_channel = future.getNow();
                    if (future.isSuccess()) {
                        ChannelFuture responseFuture =
                                server_ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
                                        Socks5CommandStatus.SUCCESS,
                                        request.dstAddrType(),
                                        request.dstAddr(),
                                        request.dstPort()));
                        responseFuture.addListener((ChannelFutureListener) channelFuture -> {
                            server_ctx.pipeline().remove(SocksServerConnectHandler.this);
                            remote_channel.pipeline().addLast(new InRelayHandler(server_ctx.channel()));
                            server_ctx.pipeline().addLast(new OutRelayHandler(remote_channel));
                        });
                    } else {
                        server_ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                        SocksServerUtils.closeOnFlush(server_ctx.channel());
                    }
                });

        final Channel inboundChannel = server_ctx.channel();
        b.group(inboundChannel.eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new DirectClientHandler(promise));

        b.connect(request.dstAddr(), request.dstPort()).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
            } else {
                server_ctx.channel().writeAndFlush(
                        new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, request.dstAddrType()));
                SocksServerUtils.closeOnFlush(server_ctx.channel());
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocksServerUtils.closeOnFlush(ctx.channel());
    }
}
