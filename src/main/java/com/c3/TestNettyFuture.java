package com.c3;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author: 小山
 * @date: 2023/2/15 13:36
 * @content:
 */

@Slf4j
public class TestNettyFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        NioEventLoopGroup group  = new NioEventLoopGroup();

        EventLoop eventLoop = group.next();

        Future<Integer> future = eventLoop.submit(new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                log.info("执行计算");
                Thread.sleep(1000);
                return 70;
            }
        });

//        log.info("等待结果");
//        log.info("结果是：{}",future.get());

        // 异步执行
        future.addListener(new GenericFutureListener<Future<? super Integer>>() {
            @Override
            public void operationComplete(Future<? super Integer> future) throws Exception {
                log.info("异步接收结果：{}",future.getNow());
            }
        });
        log.info("主线程");
    }
}
