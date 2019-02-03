package com.zm.zmq.businesslogic;

import com.alibaba.fastjson.JSON;
import com.zm.zmq.entity.LogEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Aspect
@Component
public class OfferAndPollAspect {

    private Logger logger = LoggerFactory.getLogger(OfferAndPollAspect.class);

    // pointcut签名
    @Pointcut("execution(* com.zm.zmq.businesslogic.MyMessageQueue.offer(..))")
    public void offer() {
    }

    @Pointcut("execution(* com.zm.zmq.businesslogic.MyMessageQueue.poll(..))")
    public void poll() {
    }

    // write log
    // advice
    @Before("offer()")
    public void beforeOffer(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        File dir = new File("log/" + args[1]);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File("log/" + args[1] + "/" + getDate(new Date()) + ".log");
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    throw new IOException("can't create file");
                }
            } catch (IOException e) {
                logger.error("create log file error ex:" + e);
                return;
            }
        }
        List<Object> dataList = (List<Object>) args[0];
        LogEntity entity = new LogEntity();
        entity.setData(dataList);
        entity.setDate(new Date().toString());
        entity.setSize(dataList.size());
        String jsonMessage = JSON.toJSONString(entity);
        OutputStream fos = null;
        PrintWriter pw = null;
        try {
            fos = new FileOutputStream(logFile, true);
            pw = new PrintWriter(fos);
            pw.append(";");
            pw.append(jsonMessage);
            pw.append("\n");
            pw.flush();
        } catch (FileNotFoundException e) {
            logger.error("Open stream error,file not found ex:" + e);
        } finally {
            if (null != pw) {
                pw.close();
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("close outputStream error ex:" + e);
                }
            }
        }
    }

    @After("offer()")
    public void afterOffer() {
        logger.info("offer success,time:" + System.currentTimeMillis());
    }

    @Before("poll()")
    public void beforePoll(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        logger.info("begin poll,Topic:" + args[0] + "size:" + args[1]);
        File dir = new File("log/" + args[0]);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File logFile = new File("log/" + args[0] + "/" + getDate(new Date()) + ".log");
        FileOutputStream fos = null;
        BufferedWriter writer = null;
        try {
            fos = new FileOutputStream(logFile, true);
            writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.append("poll,Topic:" + args[0] + ",size:" + args[1]);
            writer.append("\n");
            writer.flush();
        } catch (IOException e) {
            logger.error("poll occur exception:", e);
        } finally {
            if (null != writer) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.error("close writer error",e);
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("close fileOutputStream error", e);
                }
            }
        }
    }

    @After("poll()")
    public void afterPoll() {
        logger.info("poll success,time:" + System.currentTimeMillis());
    }

    private String getDate(Date date) {
        // not thread safety or use ThreadLocal
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

}
