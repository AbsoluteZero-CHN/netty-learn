package cn.noload.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-18 14:37
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerHandler> {

    /**
     * @param result 客户端对象
     * @param attachment 对应之前传递的 this(AsynchronousChannel)
     * */
    @Override
    public void completed(AsynchronousSocketChannel result, AioServerHandler attachment) {
        // 这里重新注册一个接收事件, 形成循环, 可以不断地去注册事件
        attachment.getChannel().accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // read 函数的第二个参数又是一个 attachment, 这个参数作为回调对象会传递给 ReadCompletionHandler
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AioServerHandler attachment) {

    }
}
