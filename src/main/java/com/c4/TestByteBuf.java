package com.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author: 小山
 * @date: 2023/2/15 14:45
 * @content:
 */
public class TestByteBuf {
    public static void main(String[] args) {
        // ByteBuf 可以自动扩容的
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buf);
        //
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append("a");
        }

        buf.writeBytes(sb.toString().getBytes());
        System.out.println(buf);
    }
}
