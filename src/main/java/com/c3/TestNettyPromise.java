package com.c3;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author: 小山
 * @date: 2023/2/15 13:48
 * @content:
 */
@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // 1.准备 EventLoop对象
        EventLoop next = new NioEventLoopGroup().next();

        // 2可以主动创建这个 promise对象
        DefaultPromise<Integer> promise = new DefaultPromise<>(next);

        new Thread(()->{
            System.out.println("开始计算...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(200);
        }).start();

        // 4.接收结果的线程
        log.info("等待结果...");
        log.info("结果是：{}",promise.get());

    }
}
