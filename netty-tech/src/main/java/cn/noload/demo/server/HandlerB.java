package cn.noload.demo.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-24 11:27
 */
public class HandlerB extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("cn.noload.demo.server.HandlerB executed");
    }
}
