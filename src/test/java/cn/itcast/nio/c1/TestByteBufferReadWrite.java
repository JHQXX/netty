package cn.itcast.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {

        ByteBuffer buffer=ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);// 0x61 == 'a '
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0X62,0X63,0X64});// b c d
        ByteBufferUtil.debugAll(buffer);


//        System.out.println(buffer.get());
        //切换为读取模式
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        System.out.println(buffer.get());
        ByteBufferUtil.debugAll(buffer);
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{0X65,0X66});// e f
        ByteBufferUtil.debugAll(buffer);
    }
}
