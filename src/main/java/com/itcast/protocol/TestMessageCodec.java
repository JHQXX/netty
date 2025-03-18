package com.itcast.protocol;

import com.itcast.message.LoginRequestMessage;
import com.itcast.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,12,4,0,0),
                new LoggingHandler(),
                new MessageCodec()
        );

        //encode方法
        LoginRequestMessage message = new LoginRequestMessage("lizhi", "123456");
        channel.writeOutbound(message);

        //decode
        ByteBuf buf= ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null,message,buf);
        //入栈
        channel.writeInbound(buf);
    }


}
