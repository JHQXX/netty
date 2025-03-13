package cn.itcast.nio.c1;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {


    public static void main(String[] args) {
        //FileChannel
        //1.输入输出流 获得FileChannel  2.RandomAccessFile
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            //准备缓冲区 来存储读取的数据  allocate划分一块内存  capacity 表示容量 单位为字节 这里表示10个字节
            ByteBuffer buffer=ByteBuffer.allocate(10);
            while (true){
                //从channel 读取数据 先向着buffer写入数据
                int len = channel.read(buffer);
                log.debug("读取到的字节数{}",len);
                if(len==-1){ //read方法有返回值， 当返回为-1 表示没有读取到内容
                    break;
                }
                //打印buffer 的内容
                buffer.flip();//切换到读模式
                while (buffer.hasRemaining()){ //是否还有剩余未读的数据
                    byte b = buffer.get();
                    log.debug("实际字节{}",(char)b);
                }
                //切换为写模式
                buffer.clear();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }




    }
}
