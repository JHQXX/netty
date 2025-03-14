package cn.itcast.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static cn.itcast.netty.c4.TestByteBuf.log;

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});

        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf2.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});

        /*
        会造成两次复制 会出现问题
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(buf1).writeBytes(buf2);
        log(buffer);*/

        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();
        buffer.addComponents(true,buf1,buf2);
        buffer.retain();
        log(buffer);
        buffer.release();

    }
}
