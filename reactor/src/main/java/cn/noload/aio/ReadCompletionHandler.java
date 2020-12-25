package cn.noload.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-18 15:10
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {

    private final AsynchronousSocketChannel channel;

    ReadCompletionHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        String input = new String(attachment.array(), attachment.position(), attachment.limit(), StandardCharsets.UTF_8);
        System.out.println("input: " + input);
        doWrite("输入的命令: " + input);
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {

    }

    private void doWrite(String output) {
        byte[] body = output.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(body.length);
        buffer.put(body);
        buffer.flip();
        channel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if(attachment.hasRemaining()) {
                    // 如果没写完, 再写一次
                    channel.write(buffer, buffer, this);
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });
    }
}
