package com.zm.zmq.service;

import com.google.common.collect.Lists;
import com.zm.zmq.businesslogic.ReadWriteHandler;
import com.zm.zmq.config.ConfigBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 启动NIO连接通过Socket来实现订阅/发布
 */
@Component
public class MyServletContextInitializer implements CommandLineRunner {

    @Autowired
    private ServerSocketChannel serverSocket;
    @Autowired
    private ReadWriteHandler handler;
    @Autowired
    private ConfigBean config;
    private Thread socketThread;
    private static Logger logger = LoggerFactory.getLogger(MyServletContextInitializer.class);
    private List<SocketChannel> socketList;

    @Override
    public void run(String... args) throws Exception {
        if (null != serverSocket && config.isSocketOn()) {
            socketList = Lists.newArrayList();
            try {
                final Selector selector = Selector.open();
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
                socketThread = new Thread(() -> {
                    while (config.isSocketOn() && selector.isOpen()) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> it = selectionKeys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            if (key.isAcceptable()) {
                                try {
                                    // todo:客户端连接SocketChannel在accept的时候获取到
                                    SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                                    socketList.add(socketChannel);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (key.isReadable()) {
                                handler.handleRead(key);
                            } else if (key.isWritable()) {
                                handler.handleWrite(key);
                            }
                            it.remove();
                        }
                    }
                });
                socketThread.start();
            } catch (Exception ex) {
                logger.error("failed to open or register selector to channel, ex:" + ex);
            }
        }
    }
}
