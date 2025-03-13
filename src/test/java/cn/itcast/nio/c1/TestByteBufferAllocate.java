package cn.itcast.nio.c1;

import java.nio.ByteBuffer;

public class TestByteBufferAllocate {
    public static void main(String[] args) {

        System.out.println(ByteBuffer.allocate(16).getClass());
        System.out.println(ByteBuffer.allocateDirect(16).getClass());
        /**
         * class java.nio.HeapByteBuffer -java 堆内存，读写效率较低，受到垃圾回收（GC）影响
         * class java.nio.DirectByteBuffer -java 直接内存 ，读写效率高（少一次拷贝），不会受到垃圾回收（GC）影响 ，分配内存的效率低可能会造成内存泄露
         */
    }
}
