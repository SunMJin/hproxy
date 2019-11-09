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
package netty;

import com.sunrt.proxy.utils.SocksServerUtils;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.socks.SocksInitRequest;
import io.netty.handler.codec.socksx.SocksMessage;
import io.netty.handler.codec.socksx.v5.*;
import pojo.SubscribeReq;
import pojo.SubscribeResp;

/**
 * @author lilinfeng
 * @date 2014年2月14日
 * @version 1.0
 */
@Sharable
public class SubReqServerHandler extends SimpleChannelInboundHandler<SocksMessage> {

    /*@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
	    throws Exception {
		//DefaultSocks5InitialRequest req = (DefaultSocks5InitialRequest) msg;
		ctx.writeAndFlush(msg);
    }*/

	/*@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ctx.channel().write(new DefaultSocks5InitialRequest(Socks5AuthMethod.NO_AUTH));
	}*/

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, SocksMessage message) throws Exception {
 		if(message instanceof Socks5InitialRequest){
			ctx.pipeline().addFirst(new Socks5CommandRequestDecoder());
			ctx.write(new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH));
		} else if (message instanceof Socks5CommandRequest) {
			Socks5CommandRequest request = (Socks5CommandRequest) message;
			ctx.channel().writeAndFlush(new DefaultSocks5CommandResponse(
					Socks5CommandStatus.SUCCESS,
					request.dstAddrType(),
					request.dstAddr(),
					request.dstPort()));
		}
		System.out.println("Receive server response : [" + message + "]");
	}



	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		throwable.printStackTrace();
		SocksServerUtils.closeOnFlush(ctx.channel());
	}
}
