package cn.itcast.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a','b','c','d'});
        //开启读模式
        buffer.flip();
        //rewind 从头开始
        /*buffer.get(new byte[4]);
        debugAll(buffer);
        buffer.rewind();
        System.out.println((char)buffer.get());*/

        //mark & reset
        //mark 做一个标记 记录position位置 reset是将position重置到mark的位置
        /*System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        buffer.mark();//加标记 索引为2的地方
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());
        buffer.reset();//将position 重置到索引2
        System.out.println((char)buffer.get());
        System.out.println((char)buffer.get());*/

        //get(i) 不会改变索引指针
        System.out.println((char)buffer.get(3));
        ByteBufferUtil.debugAll(buffer);

    }
}
