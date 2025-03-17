package cn.itcast.client;

import cn.itcast.message.*;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import sun.nio.cs.US_ASCII;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN=new CountDownLatch(1);
        AtomicBoolean LOGIN_SUCCESS=new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    //IdleStateHandler 判断 读空闲时间过长 写空闲时间过长
                    //3s 内没有向服务器写数据 就会触发一个写事件 IdleState#WRITER_IDLE 事件  会一直轮询
                    ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                    //ChannelDuplexHandler 可以同时作为入栈和出栈处理器
                    ch.pipeline().addLast(new ChannelDuplexHandler(){
                        //用来触发特殊事件
                        @Override
                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                            IdleStateEvent event=(IdleStateEvent)evt;
                            //触发了读空闲
                            if (event.state()== IdleState.WRITER_IDLE){
//                                log.debug("写空闲已经超过3秒，发送一个心跳包");
                                ctx.writeAndFlush(new PingMessage());
                            }

                        }
                    });

                    ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){
                        //接收响应信息
                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}", msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage)msg;
                                if (response.isSuccess()) {
                                    //如果登录成功
                                    LOGIN_SUCCESS.set(true);
                                }
                            }
                            //唤醒System.in 线程
                            WAIT_FOR_LOGIN.countDown();
                        }

                        //链接建立后触发active事件
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            //负责接收用户在控制台的输入，负责想服务器发送各种消息
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                //构造消息对象
                                LoginRequestMessage message = new LoginRequestMessage(username, password);
                                //发送消息
                                ctx.writeAndFlush(message);

                                System.out.println("等待后续...");
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //如果登录失败
                                if (!LOGIN_SUCCESS.get()) {
                                    ctx.channel().close();
                                    return;
                                }

                                while (true){
                                    System.out.println("===================================");
                                    System.out.println("send [username][content]");
                                    System.out.println("gsend [group name][content]");
                                    System.out.println("gcreate [group name][m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("===================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]){
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username,s[1],s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username,s[1],s[2]));
                                            break;
                                        case "gcreate":
                                            HashSet<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(username);//加入当前用户
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1],set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username,s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username,s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }

                            },"system in").start();
                        }
                    });
                }
            });

            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
