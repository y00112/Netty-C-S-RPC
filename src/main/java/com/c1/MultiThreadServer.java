package com.c1;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey boosKey = ssc.register(boss,0,null);
        boosKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(9999));
        // 创建固定数量的worker
        // 获取cpu的线程核心数量    Runtime.getRuntime().availableProcessors();

        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        // 计数器
        AtomicInteger index = new AtomicInteger();
        while (true){
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()){
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.info("connected....{}",sc.getRemoteAddress());
                    // 关联worker
                    log.info("before register...{}",sc.getRemoteAddress());
                    // round robin 负载均衡的轮询算法
                    workers[index.getAndIncrement() % workers.length].register(sc);

                    log.info("after register...{}",sc.getRemoteAddress());

                }
            }

        }
    }

    static class Worker implements  Runnable{
        private Thread thread;
        private Selector workerSelector;
        private String name;
        private volatile boolean start = false;
        // 使用消息队列来解耦合
        private ConcurrentLinkedDeque<Runnable> queue= new ConcurrentLinkedDeque<>();

        public Worker(String name) {
            this.name = name;
        }

        /**
         * 初始化线程，和 selector
         */
        public void register(SocketChannel sc) throws IOException {
           if (!start){
               workerSelector = Selector.open();
               thread = new Thread(this,name);
               thread.start();
               start = true;
           }
           // 向队列中添加了一个任务，这个任务没有被执行
           queue.add(()->{
               try {
                   sc.register(workerSelector, SelectionKey.OP_READ,null);
               } catch (ClosedChannelException e) {
                   e.printStackTrace();
               }

           });
           workerSelector.wakeup(); // 唤醒 select（） 方法

        }

        @Override
        public void run() {
            while (true){
                try {
                    workerSelector.select();
                    Runnable task = queue.poll();
                    if (task != null){
                        task.run();
                    }
                    Iterator<SelectionKey> iterator = workerSelector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()){
                            ByteBuffer buffer = ByteBuffer.allocate(26);
                            SocketChannel channel = (SocketChannel) key.channel();
                            channel.read(buffer);
                            buffer.flip();
                            log.info(thread.getName() + "read...." + channel.getRemoteAddress());
                            ByteBufferUtil.debugAll(buffer);
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

