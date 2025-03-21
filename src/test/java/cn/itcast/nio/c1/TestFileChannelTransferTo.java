package cn.itcast.nio.c1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {

    public static void main(String[] args) {
        try (
                FileChannel from = new FileInputStream("data.txt").getChannel();
                FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 效率高，底层会利用操作系统的零拷贝进行优化
            // 坑 一次最多传输最多2g 大于2g 多的内容不会被保留
            long size = from.size();
            //left 变量表示还剩余多少字节
            for (long left = size; left>0;){
                //left 减去传输的字节
                System.out.printf("position"+(size-left)+"left"+left);
                left-=from.transferTo(size-left,left,to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
