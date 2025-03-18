package com.itcast.protocol;

import com.itcast.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import sun.nio.cs.ext.MS874;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Message message, ByteBuf byteBuf) throws Exception {
        //1. 4字节的魔数
        byteBuf.writeBytes(new byte[]{1,2,3,4});
        //2. 1字节版本
        byteBuf.writeByte(1);
        //3. 1字节的序列化的方式 ,0 jdk , 1 json
        byteBuf.writeByte(0);
        //4. 1字节的写入的指令类型
        byteBuf.writeByte(message.getMessageType());
        //5. 4个字节的请求序号
        byteBuf.writeInt(message.getSequenceId());
        //无意义 对其填充使用
        byteBuf.writeByte(0xff);
        //6. 获取内容的字节数组
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutputStream oos=new ObjectOutputStream(bos);
        oos.writeObject(message);
        byte[] bytes = bos.toByteArray();
        //7. 长度
        byteBuf.writeInt(bytes.length);
        //8.写入内容
        byteBuf.writeBytes(bytes);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int magicNum = byteBuf.readInt();
        byte version = byteBuf.readByte();
        byte serializerType = byteBuf.readByte();
        byte messageType = byteBuf.readByte();
        int sequenceId = byteBuf.readInt();
        byteBuf.readByte();
        int length = byteBuf.readInt();
        byte[] bytes=new byte[length];
        byteBuf.readBytes(bytes,0,length);
        //jdk 序列化
        ObjectInputStream ois=new ObjectInputStream(new ByteArrayInputStream(bytes));
        Message message = (Message)ois.readObject();
        log.debug("{},{},{},{},{},{}",magicNum,version,serializerType,messageType,sequenceId,length);
        log.debug("{}",message);
        list.add(messageType);
//        if (serializerType==0){
//        }
    }
}
