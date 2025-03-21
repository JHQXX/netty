package cn.itcast.nio.c4;

import cn.itcast.nio.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用 非阻塞模式 单线程
        //0.ByteBuffer
        ByteBuffer buffer=ByteBuffer.allocate(16);
        //1.创建完服务器
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);//非阻塞模式 切换为
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3.连接集合
        List<SocketChannel> channels=new ArrayList<>();
        while (true){
            //4.accept 建立与客户端的连接  SocketChannel 用来与客户端之间通信
            SocketChannel sc = ssc.accept(); //非阻塞，线程还是会继续运行，如果没有连接建立 sc返回一个null
            if (sc!=null){
                log.debug("connected....{}",sc);
                sc.configureBlocking(false);//非阻塞模式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                //5.接收客户端发送的数据
                int read = channel.read(buffer);//非阻塞模式，线程还是会继续运行，如果没有读到数据，read 返回 0
                if (read>0){
                    buffer.flip();
                    ByteBufferUtil.debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}",channel);
                }
            }
        }

    }
}
