package com.example.mqdemo;

import com.example.mqdemo.netty.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetSocketAddress;

@SpringBootApplication
public class MqdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqdemoApplication.class, args);
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(new InetSocketAddress("192.168.137.120",8090));
    }

}
