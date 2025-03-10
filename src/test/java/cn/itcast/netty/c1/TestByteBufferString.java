package cn.itcast.netty.c1;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static cn.itcast.netty.c1.ByteBufferUtil.debugAll;

public class TestByteBufferString {
    public static void main(String[] args) {
        // 1.字符串转为ByteBuffer 不会自动化切换为读模式 需要 buffer.flip();  手动切换为读模式
        ByteBuffer buffer =ByteBuffer.allocate(16);
        buffer.put("hello".getBytes());
        debugAll(buffer);

        //2.Charset  直接切换为读模式
        ByteBuffer encode = StandardCharsets.UTF_8.encode("hello-zh");
        debugAll(encode);

        //3.wrap
        ByteBuffer wrap = ByteBuffer.wrap("hello".getBytes());
        debugAll(wrap);

        String encode1 = StandardCharsets.UTF_8.decode(encode).toString();
        System.out.println(encode1);


        buffer.flip();//切换为读模式
        String buffer1 = StandardCharsets.UTF_8.decode(buffer).toString();
        System.out.println(buffer1);
    }
}
