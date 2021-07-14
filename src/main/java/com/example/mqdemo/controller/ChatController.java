package com.example.mqdemo.controller;

import com.example.mqdemo.vo.nettyVo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/netty")
public class ChatController {

    @Autowired
    private RedisTemplate<String,? extends Object> redisTemplate;
    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = "/reg", method = RequestMethod.GET)
    public void reg(@RequestParam("userId") String userId) {
        ListOperations<String,nettyVo> listOps = (ListOperations<String, nettyVo>) redisTemplate.opsForList();
        nettyVo user = new nettyVo();
        user.setUserId(userId);
        user.setMsg("");
        ArrayList<nettyVo> userList = new ArrayList<>();
        userList.add(user);
        listOps.leftPushAll("nettyUsers",userList);

        //此时拿到的是jackson序列化后的json字符串
        List<nettyVo> lists = listOps.range("nettyUsers", 0, -1);
        //jackson解析出具体的bean
        List<nettyVo> users= objectMapper.convertValue(lists, new TypeReference<List<nettyVo>>() { });
        users.forEach(x-> System.out.println(x.getUserId()));

    }

//    @GetMapping("/reg")
//    public String login(@RequestParam("userId") String userId) throws InterruptedException {
//        while (true) {
//            Channel channel = ChatConfig.concurrentHashMap.get(sendId);
//            if (channel != null) {
//                channel.writeAndFlush(new TextWebSocketFrame("test"));
//                Thread.sleep(1000);
//            }
//        }
//    }

}
