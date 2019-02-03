package com.zm.zmq.businesslogic;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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

    public <T> List<T> poll(String topic, int size, long timeOut, TimeUnit timeUnit, Class<T> clazz)
            throws InterruptedException {
        List<T> resultList = Lists.newArrayListWithCapacity(size);
        BlockingQueue<T> blockingQueue = queueMap.get(topic);
        for (int i = 0; i < size; i++) {
            resultList.add(blockingQueue.poll(timeOut, timeUnit));
        }
        return resultList;
    }

}
