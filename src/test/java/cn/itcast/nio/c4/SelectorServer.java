package cn.itcast.nio.c4;

import cn.itcast.nio.c1.ByteBufferUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

@Slf4j
public class SelectorServer {

    private static void split(ByteBuffer source){
        //切换为读模式
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            //找到一条完整消息
            if (source.get(i)=='\n') {
                int length = i+1-source.position();
                //存入新的ByteBuffer
                ByteBuffer target=ByteBuffer.allocate(length);
                //从 source 读 向target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                ByteBufferUtil.debugAll(target);

            }
        }
        //不可以使用clean 因为我们需要继续读取
        source.compact();  // 1234567890asdfgh  如果超过容积但是还没有读取到\n 此时的position和limit的值还是16
    }

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
            //客户端主动断开或者被动断开都会触发一次读事件
            selector.select();
            //4.处理事件  拿去所有的可用事件的  SelectionKeys内部包含了所有发生的事件
            //ps 你想在集合遍历时候删除得用迭代器遍历
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); //accept ,read
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                //对于NIO模型 我们拿取到事件后 需要删除对应的key 处理key时 ，要从selectedKeys集合中删除，否则下次处理会有问题  处理后的key事件属性会被删除，但是它的事件类型不会消失
                iterator.remove();
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
                    //如果读取的字节超出原始设定的字节，会触发下一次read 并且传入的字节的  原始字节  减去  次数*16
                    ByteBuffer buffer=ByteBuffer.allocate(16);//attachment 附件
                    //同时把这个SocketChannel 也放进selector进行管理 SelectionKe == 返回对应的key
                    //将一个byteBuffer 作为附件关联到selectionKey上
                    SelectionKey scKey = sc.register(selector, 0, buffer);
                    log.debug("key:{}",scKey);
                    //关注读
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    try {
                        //如果是read事件
                        SocketChannel channel = (SocketChannel)key.channel();//拿到触发事件的channel
                        //获取 selectionKey 上关联的附件
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        int read = channel.read(buffer); //如果是正常断开，read的返回值是-1
                        if (read==-1){
                            key.cancel();
                        }else {
                            split(buffer);
                            if (buffer.position()==buffer.limit()){
                                ByteBuffer newBuffer=ByteBuffer.allocate(buffer.capacity()*2);
                                //把新的buffer传入进去进行替换
                                buffer.flip();//切换为读模式
                                newBuffer.put(buffer);//1234567890asdfgh
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //反注册掉 因为客户端主动断开了，出现了异常，我们需要将这个key取消 将key selector的key集合中删除掉
                        key.cancel();
                    }
                }

                //取消事件
//                key.cancel();
            }

        }









    }
}
