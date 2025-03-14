package cn.itcast.netty.c3;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        //1.创建事件循环组
        EventLoopGroup group=new NioEventLoopGroup(2);//io 事件，普通任务 ，定时任务
//        EventLoopGroup group=new DefaultEventLoop();//普通任务 ，定时任务

        //2.获取下一个事件循环对象
        /**
         *  System.out.println(group.next()); io.netty.channel.nio.NioEventLoop@1ef7fe8e
         *  System.out.println(group.next()); io.netty.channel.nio.NioEventLoop@6f79caec
         *  System.out.println(group.next()); io.netty.channel.nio.NioEventLoop@1ef7fe8e
         */

        //3.执行普通任务
        /*group.next().execute(()->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.debug("ok");
        });*/

        //4.执行定时任务
        /*group.next().scheduleAtFixedRate(()->{
            log.debug("ok");
        },1,1, TimeUnit.SECONDS);
*/


        log.debug("Main ok");

    }
}
