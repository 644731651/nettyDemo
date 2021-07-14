package com.example.mqdemo.netty;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.mqdemo.vo.nettyVo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;

public class NettyServerHandler  extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        removeUserId(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient"+incoming.remoteAddress()+"在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // TODO Auto-generated method stub
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient"+incoming.remoteAddress()+"掉线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // TODO Auto-generated method stub
        Channel incoming = ctx.channel();
        System.out.println("SimpleChatClient"+incoming.remoteAddress()+"异常");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        //传过来的是json字符串
        String text = textWebSocketFrame.text();
        JSONObject jsonObject = JSON.parseObject(text);
        //获取到发送人的用户id
        Object msg = jsonObject.get("msg");
        String userId = (String) jsonObject.get("userId");
        String fromUserId = (String) jsonObject.get("fromUserId");
        Channel channel = ctx.channel();
        System.out.println(userId +""+ msg);
        if (msg == null) {
            //说明是第一次登录上来连接，还没有开始进行聊天，将uid加到map里面
            register(userId, channel);
        } else {
            //有消息了,开始聊天了
            sendMsg(msg, userId, fromUserId);
        }
    }

    private void register(String userId, Channel channel) {
        if (!ChatConfig.concurrentHashMap.containsKey(userId)) { //没有指定的userId
            ChatConfig.concurrentHashMap.put(userId, channel);
            // 将用户ID作为自定义属性加入到channel中，方便随时channel中获取用户ID
            AttributeKey<String> key = AttributeKey.valueOf("userId");
            channel.attr(key).setIfAbsent(userId);
        }
    }

    private void sendMsg(Object msg, String userId, String fromUserId) {
        Channel channel1 = ChatConfig.concurrentHashMap.get(userId);
        nettyVo vo = new nettyVo();
        if (channel1 != null) {
            vo.setMsg(fromUserId+" : "+ msg);
            vo.setUserId(userId);
            channel1.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(vo)));
        }else {
            Channel channelFrom = ChatConfig.concurrentHashMap.get(fromUserId);
            vo.setMsg(userId + ", 已下线无法发送消息");
            vo.setUserId(fromUserId);
            channelFrom.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(vo)));
        }
    }

    private void removeUserId(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        AttributeKey<String> key = AttributeKey.valueOf("userId");
        String userId = channel.attr(key).get();
        ChatConfig.concurrentHashMap.remove(userId);
        System.out.println("用户下线,userId："+userId);
    }

    public static void main(String[] args) {
        nettyVo vo = new nettyVo();
        vo.setMsg("121");
        vo.setUserId("12");
        System.out.println(JSON.toJSONString(vo));
    }
}
