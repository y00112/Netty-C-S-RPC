package rpc.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: 小山
 * @date: 2023/2/22 8:57
 * @content:
 */
public class SequenceIdGenerator {

    private static final AtomicInteger id = new AtomicInteger();

    public static int nextId(){
        return id.incrementAndGet();
    }
}
