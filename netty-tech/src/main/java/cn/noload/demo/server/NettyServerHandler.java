package cn.noload.demo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-22 15:53
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 关注读事件
     * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String command = new String(bytes, StandardCharsets.UTF_8);
        System.out.println("服务端接收到的请求: " + command);
        ByteBuf outBuffer = Unpooled.copiedBuffer(("服务端接收到的请求: " + command).getBytes());
        ctx.writeAndFlush(outBuffer);
    }
}
