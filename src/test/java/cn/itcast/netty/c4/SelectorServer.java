package cn.itcast.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
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
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);

        //2.建立Selector和channel的联系（注册）
        //SelectionKey 就是将来事件发生后，通过它可以知道事件和哪个channel的事件 这里初始值为0 表示不关注任何事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        //key 只关注accept 事件 这里把0改为16 可以看见这里这个 final int OP_ACCEPT = 16 为16 表示关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key:{}",sscKey);
        ssc.bind(new InetSocketAddress(8080));
        while (true){
            //3.select 方法   没有事件发生，select会阻塞 但是有事件发生 会让线程不阻塞
            //select 在事件未处理时候 它不会阻塞     事件发生后要么处理要么取消 不可以置之不理
            selector.select();
            //4.处理事件  拿去所有的可用事件的  SelectionKeys内部包含了所有发生的事件
            //ps 你想在集合遍历时候删除得用迭代器遍历
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); //accept ,read
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                log.debug("key:{}",key);
                //区分事件类型
                if (key.isAcceptable()) {
                    //如果是accept事件
                    //处理事件
                    ServerSocketChannel channel = (ServerSocketChannel)key.channel();
                    SocketChannel sc = channel.accept();
                    log.debug("{}",sc);
                    //把SocketChannel也设置为不阻塞
                    sc.configureBlocking(false);
                    //同时把这个SocketChannel 也放进selector进行管理 SelectionKe == 返回对应的key
                    SelectionKey scKey = sc.register(selector, 0, null);
                    log.debug("key:{}",scKey);
                    //关注读
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    //如果是read事件
                    SocketChannel channel = (SocketChannel)key.channel();//拿到触发事件的channel
                    ByteBuffer buffer=ByteBuffer.allocate(16);
                    channel.read(buffer);
                    buffer.flip();
                    debugRead(buffer);

                }

                //取消事件
//                key.cancel();
            }

        }









    }
}
