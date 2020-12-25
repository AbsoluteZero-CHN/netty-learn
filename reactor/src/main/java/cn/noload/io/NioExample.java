package cn.noload.io;

import sun.nio.ch.DirectBuffer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;

/**
 * NIO 模型实现
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-15 17:24
 */
public class NioExample {

    private static Selector selector;

    public static void main(String[] args) throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        selector = Selector.open();
        channel.bind(new InetSocketAddress(5000));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            // 线程阻塞
            int select = selector.select();
            if(select > 0) {
                for (SelectionKey selectedKey : selector.selectedKeys()) {
                    handle(selectedKey);
                }
            }
        }
    }

    private static void handle(SelectionKey selectionKey) throws IOException {
        if(selectionKey.isValid()) {
            if(selectionKey.isAcceptable()) {
                // 发送消息对应的 channel
                ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                // 接受链接
                SocketChannel accept = channel.accept();
                accept.configureBlocking(false);
                accept.register(selector, SelectionKey.OP_READ);
            } else if(selectionKey.isReadable()) {
                // 可读的 channel 是客户端的 channel
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                int size = channel.read(byteBuffer);
                if(size > 0) {
                    byteBuffer.flip();
                    // java.nio.Buffer.remaining 可读长度
                    byte[] request = new byte[byteBuffer.remaining()];
                    byteBuffer.get(request);
                    System.out.println("接收到的数据: " + new String(request, StandardCharsets.UTF_8));
                    write(channel, "OK");
                }
            }
        }
    }

    private static void write(SocketChannel channel, String response) throws IOException {
        if (response != null && response.length() > 0) {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            buffer.put(bytes);
            buffer.flip();
            channel.write(buffer);
        }
    }

    /**
     * 手动释放直接内存
     * */
    private static void clean(ByteBuffer byteBuffer) {
        if(byteBuffer.isDirect()) {
            ((DirectBuffer) byteBuffer).cleaner().clean();
        }
    }
}
