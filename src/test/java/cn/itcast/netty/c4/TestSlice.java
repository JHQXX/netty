package cn.itcast.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static cn.itcast.netty.c4.TestByteBuf.log;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf=ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a','b','c','d','e','f','g','h','i','j'});
        log(buf);

        //在切片过程中没有发生数据复制  切片后的容量不可再增加
        ByteBuf f1 = buf.slice(0, 5);
        //保留内存空间
        f1.retain();
        ByteBuf f2 = buf.slice(5, 5);
        log(f1);
        log(f2);

        //释放原有 byteBuf 后  切片不可用
        buf.release();
        log(f1);


        f1.release();
        /*f1.setByte(0,'b');
        f2.setByte(0,'g');
        log(f1);
        log(f2);
        log(buf);*/
    }
}
