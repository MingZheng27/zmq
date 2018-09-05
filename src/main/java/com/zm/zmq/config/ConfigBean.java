package com.zm.zmq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("application.properties")
public class ConfigBean {

    @Value("${mq.writeThreshold}")
    private int threshold = 1000;

    @Value("${socket.isSocketOn}")
    private volatile boolean isSocketOn;

    @Value("${socket.port}")
    private int port;

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isSocketOn() {
        return isSocketOn;
    }

    public void setSocketOn(boolean socketOn) {
        isSocketOn = socketOn;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
