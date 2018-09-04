package com.zm.zmq.config;

import com.zm.zmq.service.MyServletContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class BeanConfiguration {

    @Value("${socket.port}")
    private int port;
    @Value("${mq.size}")
    private int mqSize;
    @Autowired
    private MyServletContextInitializer initializer;
    // todo:slf4j logback to file
    private static Logger logger = LoggerFactory.getLogger(BeanConfiguration.class);

    @Bean
    public ServerSocketChannel serverSocket() {
        try {
            if (initializer.isSocketOn()) {
                ServerSocketChannel serverSocket = ServerSocketChannel.open();
                serverSocket.socket().bind(new InetSocketAddress(port));
                serverSocket.configureBlocking(false);
                // Selector selector = Selector.open();
                // serverSocket.register(selector, SelectionKey.OP_ACCEPT);
                logger.info("socket start and listening on port:" + port);
                return serverSocket;
            }
        } catch (Exception ex) {
            logger.error("fail to open socket on port:" + port + "exception:" + ex);
        }
        return null;
    }

    @Bean
    public AtomicReference<BlockingQueue> blockingQueue() {
        // todo:
        return null;
    }
}
