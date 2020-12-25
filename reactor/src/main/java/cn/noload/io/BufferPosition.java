package cn.noload.io;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试 buffer 缓冲区位置
 * -XX:MaxDirectMemorySize=500M
 * -Xmx500M
 * @author hao.caosh@ttpai.cn
 * @date 2020-12-15 17:46
 */
public class BufferPosition {

    public static void main(String[] args) {
        List<ByteBuffer> list = new ArrayList<>();
        while (true) {
            list.add(allocateHeap(1024));
            list.add(allocateDirect(1024));
        }
    }

    private static ByteBuffer allocateHeap(int size) {
        return ByteBuffer.allocate(size);
    }
    private static ByteBuffer allocateDirect(int size) {
        return ByteBuffer.allocateDirect(size);
    }
}
