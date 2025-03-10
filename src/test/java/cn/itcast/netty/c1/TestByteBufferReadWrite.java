package cn.itcast.netty.c1;

import java.nio.ByteBuffer;

import static cn.itcast.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {

        ByteBuffer buffer=ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);// 0x61 == 'a '
        debugAll(buffer);
        buffer.put(new byte[]{0X62,0X63,0X64});// b c d
        debugAll(buffer);


//        System.out.println(buffer.get());
        //切换为读取模式
        buffer.flip();
        debugAll(buffer);
        System.out.println(buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        buffer.put(new byte[]{0X65,0X66});// e f
        debugAll(buffer);
    }
}
