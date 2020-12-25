package cn.noload.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.charset.StandardCharsets;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-22 16:14
 */
public class NettyClient {


    public NettyClient(String host, int port) {
        // 线程组, 用于读
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 客户端引导程序
        Bootstrap bootstrap = new Bootstrap();
        ChannelInitializer initializer = new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                channel.pipeline().addLast(new NettyClientHandler());
            }


        };
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(initializer);
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            GenericFutureListener listener = (GenericFutureListener<Future<? super Void>>) f -> {
                ByteBuf outBuffer = Unpooled.copiedBuffer("Hi netty server", StandardCharsets.UTF_8);
                future.channel().writeAndFlush(outBuffer);
            };
            future.addListener(listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyClient("127.0.0.1", 5000);
    }
}
