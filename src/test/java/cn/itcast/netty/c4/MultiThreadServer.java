package cn.itcast.netty.c4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static cn.itcast.netty.c1.ByteBufferUtil.debugAll;
@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc=ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));
        //1.创建固定数量的worker 并初始化   阿姆达尔  计算IO密集    cpu密集 线程数量为CPU核心数量
        Worker[] workers=new Worker[Runtime.getRuntime().availableProcessors()];
        for (int i = 0; i < workers.length; i++) {
             workers[i] = new Worker("worker-"+i);
        }
        AtomicInteger index=new AtomicInteger();
        while (true){
            //开启监听
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected..{}",sc.getRemoteAddress());
                    //2.关联selector
                    log.debug("before register..{}",sc.getRemoteAddress());
                    //round rabin 轮询算法
                    workers[index.getAndIncrement() % workers.length].register(sc);//被boss线程调用 初始化selector 启动worker-0
                    log.debug("after register..{}",sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable{
        private Thread thread;

        private Selector selector;

        private String name;

        private ConcurrentLinkedQueue<Runnable> queue=new ConcurrentLinkedQueue<>();

        private volatile boolean start=false;//还未初始化


        public Worker(String name) {
            this.name = name;
        }

        //初始 线程 和 selector
        public void register(SocketChannel sc) throws IOException {
            if (!start){
                //先再boss线程初始化Selector
                selector =Selector.open();
                thread = new Thread(this,name);
                thread.start();
                start=true;
            }
            //向队列添加了任务 但是任务并没有立刻执行
            queue.add(()->{
                try {
                    sc.register(selector,SelectionKey.OP_READ,null);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup();//唤醒 selector.select(); 因为阻塞了

        }

        //工作线程抢断了
        @Override
        public void run() {
            while (true){
                try {
                    selector.select();//worker-0 阻塞，wakeup
                    Runnable task = queue.poll();
                    if (task!=null){
                        task.run(); //执行了 sc.register(selector,SelectionKey.OP_READ,null); 注册
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()){
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel=(SocketChannel)key.channel();
                            log.debug("read...{}",channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
