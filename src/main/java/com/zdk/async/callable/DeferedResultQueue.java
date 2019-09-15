package com.zdk.async.callable;

import org.springframework.web.context.request.async.DeferredResult;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DeferedResultQueue {
    public static Queue<DeferredResult> deferredResultQueue = new ConcurrentLinkedDeque<>();

    public static void add(DeferredResult deferredResult) {
        deferredResultQueue.add(deferredResult);
    }

    public static DeferredResult get() {
        return deferredResultQueue.poll();
    }

}
