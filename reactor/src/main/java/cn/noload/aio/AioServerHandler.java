package cn.noload.aio;

import lombok.Getter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-18 14:02
 */
public class AioServerHandler implements Runnable {

    @Getter
    private final AsynchronousServerSocketChannel channel;

    public AioServerHandler(int port) throws IOException {
        channel = AsynchronousServerSocketChannel.open();
        channel.bind(new InetSocketAddress(port));
        System.out.println("Aio server start");
    }

    @Override
    public void run() {
        doAccept();
    }

    private void doAccept() {
        // AsynchronousServerSocketChannel.accept 是一个异步的方法, 调用会直接返回.
        // 为了让子线程能够有时间处理监听客户端的连接会话, 这里让主线程能够处理到客户端的连接, 这里让线程阻塞
        channel.accept(this, new AcceptCompletionHandler());
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new AioServerHandler(5000), "aio-server-5000").start();
    }
}
