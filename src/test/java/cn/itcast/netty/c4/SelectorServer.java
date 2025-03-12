package cn.itcast.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static cn.itcast.netty.c1.ByteBufferUtil.debugRead;

@Slf4j
public class SelectorServer {
    public static void main(String[] args) throws IOException {
        //1.创建Selector 可以管理多个Channel
        Selector selector= Selector.open();

        /**
         * 有四种事件
         * accept -会在有连接请求时触发
         * connect -客户端 连接建立后触发
         * read -可读事件
         * write -可写事件
         */




        ByteBuffer buffer=ByteBuffer.allocate(16);
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2.建立Selector和channel的联系（注册）
        //SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);


        ssc.bind(new InetSocketAddress(8080));
        List<SocketChannel> channels=new ArrayList<>();
        while (true){
            SocketChannel sc = ssc.accept();
            if (sc!=null){
                log.debug("connected....{}",sc);
                sc.configureBlocking(false);
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                int read = channel.read(buffer);
                if (read>0){
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read...{}",channel);
                }
            }
        }









    }
}
