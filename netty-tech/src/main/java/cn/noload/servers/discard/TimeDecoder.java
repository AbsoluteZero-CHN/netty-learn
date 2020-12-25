package cn.noload.servers.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-25 10:25
 */
public class TimeDecoder extends ByteToMessageDecoder {

    /**
     * 每当接收到新数据时, 都会使用内部维护的鸡肋缓冲区调用该方法
     * */
    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() >= 4) {
            list.add(byteBuf.readBytes(4));
        }
    }
}
