package cn.noload.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-17 13:41
 */
public class ReactorServer implements Runnable {
    public static void main(String[] args) {
        try {
            Thread th = new Thread(new ReactorServer(5000));
            th.setName("Reactor");
            th.start();
            th.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 选择器，通知通道就绪的事件
    final Selector selector;
    final ServerSocketChannel serverSocket;

    public ReactorServer(int port) throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(port)); // 绑定端口
        serverSocket.configureBlocking(false); // 设置成非阻塞模式
        // 注册并关注一个 IO 事件
        SelectionKey sk = serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        // 关联事件的处理程序
        sk.attach(new Acceptor());

        System.out.println("Listening on port " + port);
    }

    @Override
    public void run() { // normally in a new Thread
        try {
            while (!Thread.interrupted()) { // 死循环
                selector.select(); // 阻塞，直到有通道事件就绪
                Set<SelectionKey> selected = selector.selectedKeys(); // 拿到就绪通道 SelectionKey 的集合
                Iterator<SelectionKey> it = selected.iterator();
                while (it.hasNext()) {
                    SelectionKey skTmp = it.next();
                    dispatch(skTmp); // 分发
                }
                selected.clear(); // 清空就绪通道的 key
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void dispatch(SelectionKey k) {
        Runnable r = (Runnable) (k.attachment()); // 获取key关联的处理器
        if (r != null) r.run(); // 执行处理
    }

    /**
     * 处理连接建立事件
     *
     * @author tongwu.net
     */
    class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel sc = serverSocket.accept(); // 接收连接，非阻塞模式下，没有连接直接返回 null
                if (sc != null) {
                    // 把提示发到界面
                    sc.write(ByteBuffer.wrap("Implementation of Reactor Design Partten by tonwu.net\r\nreactor> ".getBytes()));
                    System.out.println("Accept and handler - " + sc.socket().getLocalSocketAddress());
//                    new BasicHandler(selector, sc); // 单线程处理连接
					new MultiThreadHandler(selector, sc); // 线程池处理连接
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
