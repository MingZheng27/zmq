package com.zm.zmq.businesslogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ReadWriteHandler {

    private Logger logger = LoggerFactory.getLogger(ReadWriteHandler.class);

    // flag for data change
    private volatile boolean isDataChange;
    @Value("${mq.writeThreshold}")
    private int threshold = 1000;
    private long lastWriteTime;
    private static final int BYTE_BUFF_SIZE = 1024;

    public void handleWrite(SelectionKey key) {
        // todo:flag is true and Threshold > a special time\
        if (isDataChange && (System.currentTimeMillis() - lastWriteTime) > threshold) {
            // todo: writeData
            lastWriteTime = System.currentTimeMillis();
        }
    }

    public byte[] handleRead(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(2 * BYTE_BUFF_SIZE);
        int size = -1;
        try {
            size = channel.read(buffer);
        } catch (IOException ex) {
            logger.error("ReadWriteHandler ex:" + ex);
            return null;
        }
        byte[] data = new byte[size];
        // todo:
        System.arraycopy(buffer.array(), 0, data, 0, size);
        // todo: send to mq
        notifyChange();
        return data;
    }

    public boolean isDataChange() {
        return isDataChange;
    }

    public void setDataChange(boolean dataChange) {
        isDataChange = dataChange;
    }

    public void setLastWriteTime(long lastWriteTime) {
        this.lastWriteTime = lastWriteTime;
    }

    public void notifyChange() {
        setDataChange(true);
    }
}
