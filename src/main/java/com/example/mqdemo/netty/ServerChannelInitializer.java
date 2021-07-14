package com.example.mqdemo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //使用http的编码器和解码器
        pipeline.addLast(new HttpServerCodec());
        //添加块处理器
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        //增加心跳支持*针对客户端  读空闲或者写空闲不出来 只对读写空闲请求超过60秒 则主动断开*/
        pipeline.addLast(new IdleStateHandler(4,8,60));
        // webSocket处理的协议，用于指定用户访问的路由：/chat * 本handler会帮你处理一些繁琐的事* 会帮你处理握手动作*/
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        //自定义handler,处理业务逻辑
        pipeline.addLast(new NettyServerHandler());
    }
}
