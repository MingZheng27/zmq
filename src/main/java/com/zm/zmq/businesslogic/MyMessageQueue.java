package com.zm.zmq.businesslogic;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class MyMessageQueue {

    // K:topic V:MQ
    Map<String, BlockingQueue> queueMap = new ConcurrentHashMap<>();

    public <T> void offer(List<T> dataList, String topic) {
        BlockingQueue queue = queueMap.get(topic);
        if (null == queue) {
            queue = new LinkedBlockingQueue();
            queueMap.put(topic, queue);
        }
        for (T o : dataList) {
            queue.offer(o);
        }
    }

}
