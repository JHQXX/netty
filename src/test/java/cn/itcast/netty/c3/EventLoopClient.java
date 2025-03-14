package cn.itcast.netty.c3;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //2.带Future ,Promise 的类型都是和异步方法配套使用，用来处理结果
        ChannelFuture channelFuture = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override//在连接建立后被调用
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                })
                //1.连接服务器
                // 异步非阻塞 main发起调用 真正执行连接 connect 是NioEventLoopGroup 中的一个线程
                .connect("localhost", 8080);  //1s才能把这个连接建立好
        //2.1使用sync 方法同步处理结果
        /*channelFuture.sync(); //阻塞方法 直到nio连接建立完成后才可以往下运行
        //无阻塞的向下获取channel
        Channel channel = channelFuture.channel();
        log.debug("{}",channel);
        channel .writeAndFlush("hello world");*/

        //2.2使用 addListener（回调对象） 方法异步处理结果
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            //在nio线程连接建立好了之后，会调用 operationComplete 方法
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                log.debug("线程是：{}",channel);
                channel.writeAndFlush("你好");
            }
        });


    }
}
