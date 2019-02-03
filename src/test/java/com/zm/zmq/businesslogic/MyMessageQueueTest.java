package com.zm.zmq.businesslogic;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MyMessageQueueTest {

    @Autowired
    private MyMessageQueue mq;

    @Test
    public void offerTest() {
        List<String> list = Lists.newArrayList();
        list.add("hello world");
        list.add("test");
        mq.offer(list, "test");
    }

    @Test
    public void pollTest() throws InterruptedException {
        List<String> list = Lists.newArrayList();
        list.add("hello world");
        list.add("test");
        mq.offer(list, "test");
        List<String> pollResult = mq.poll("test", 2, 1000,
                TimeUnit.MILLISECONDS, String.class);
        System.out.println();
    }

}
