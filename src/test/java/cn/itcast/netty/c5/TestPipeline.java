package cn.itcast.netty.c5;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * 双向通信
 */
@Slf4j
public class TestPipeline {
    public static void main(String[] args) {
        new ServerBootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        //1.通过channel拿取pipeline
                        ChannelPipeline pipeline = ch.pipeline();
                        //2.添加处理器 hear -> h1(new) -> h2 -> h3 -> h4 -> h5 -> h6 -> tail
                        pipeline.addLast("read",new ChannelInboundHandlerAdapter(){
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("read");
                                ByteBuf buf=(ByteBuf)msg;
                                String name = buf.toString(Charset.defaultCharset());
                                ch.writeAndFlush(name);
                            }
                        });
                        pipeline.addLast("write",new ChannelOutboundHandlerAdapter(){
                            @Override
                            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                log.debug("write");
                            }
                        });
                    }
                }).bind(8080);
    }
    @Data
    @AllArgsConstructor
    static  class Student{
        private String name;
    }
}
