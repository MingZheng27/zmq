package com.zm.zmq.config;

import com.zm.zmq.service.MyServletContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

@Configuration
public class BeanConfiguration {

    @Autowired
    private ConfigBean config;

    @Autowired
    private MyServletContextInitializer initializer;
    // todo:slf4j logback to file
    private static Logger logger = LoggerFactory.getLogger(BeanConfiguration.class);

    @Bean
    public ServerSocketChannel serverSocket() {
        try {
            if (config.isSocketOn()) {
                ServerSocketChannel serverSocket = ServerSocketChannel.open();
                serverSocket.socket().bind(new InetSocketAddress(config.getPort()));
                serverSocket.configureBlocking(false);
                logger.info("socket start and listening on port:" + config.getPort());
                return serverSocket;
            }
        } catch (Exception ex) {
            logger.error("fail to open socket on port:" + config.getPort() + "exception:" + ex);
        }
        return null;
    }

}
