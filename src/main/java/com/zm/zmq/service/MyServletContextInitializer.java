package com.zm.zmq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * 启动NIO连接通过Socket来实现订阅/发布
 */
@Component
public class MyServletContextInitializer implements CommandLineRunner {

    @Autowired
    private ServerSocketChannel serverSocket;
    @Value("${socket.isSocketOn}")
    private volatile boolean isSocketOn;
    private static final int BYTE_BUFF_SIZE = 1024;
    private Thread socketThread;
    private static Logger logger = LoggerFactory.getLogger(MyServletContextInitializer.class);

    @Override
    public void run(String... args) throws Exception {
        if (null != serverSocket && isSocketOn) {
            try {
                final Selector selector = Selector.open();
                serverSocket.register(selector, SelectionKey.OP_READ);
                socketThread = new Thread(() -> {
                    while (isSocketOn && selector.isOpen()) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> it = selectionKeys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            if (key.isReadable()) {
                                handleRead(key);
                            } else if (key.isWritable()) {
                                handleWrite(key);
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

    private void handleWrite(SelectionKey key) {
        // todo:
    }

    private void handleRead(SelectionKey key) {
        // todo:
    }

    public boolean isSocketOn() {
        return isSocketOn;
    }

    public void setSocketOn(boolean socketOn) {
        isSocketOn = socketOn;
    }
}
