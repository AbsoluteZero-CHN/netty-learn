package cn.noload.demo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-22 14:02
 */
public class NettyServer {

    public NettyServer(int port) throws InterruptedException {
        // 线程组, 负责 accept 事件(主反应堆)
        NioEventLoopGroup group = new NioEventLoopGroup();
        // 线程组, 负责读写事件(Handler)
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        // 服务端引导程序
        ServerBootstrap bootstrap = new ServerBootstrap();
        NettyServerHandler nettyServerHandler = new NettyServerHandler();
        ChannelInitializer channelInitializer = new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                // 添加事件处理器(运行期也可以添加/删除, 原因是因为每来一个客户端都会调用一次)
                socketChannel.pipeline()
                        .addLast(new HandlerA())
                        .addLast(new HandlerB())
                        // 这里如果需要复用 Handler, 需要在目标类上添加 @ChannelHandler.Sharable 注解, 告诉 Netty 这个 Handler 是线程安全的
                        .addLast(nettyServerHandler);
            }
        };
        // 主从模型
        bootstrap.group(group, workGroup)
                // 通讯模式 -> NIO
                .channel(NioServerSocketChannel.class)
                // 通讯模式 -> OIO(BIO)
                //         .channel(OioServerSocketChannel.class);
                // 通讯模式 -> Epoll(多路复用)
                //         .channel(EpollServerSocketChannel.class);
                // 通讯模式 -> Epoll(多路复用)
                //         .channel(KQueueServerSocketChannel.class);
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 事件处理器
                .childHandler(channelInitializer);
        // 多线程模型
        // bootstrap.group(workGroup);
        try {
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer(5000);
    }
}
