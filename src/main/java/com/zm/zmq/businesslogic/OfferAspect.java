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
public class OfferAspect {

    private Logger logger = LoggerFactory.getLogger(OfferAspect.class);

    // pointcut签名
    @Pointcut("execution(* com.zm.zmq.businesslogic.MyMessageQueue.offer(..))")
    public void offer() {
    }

    // write log
    // advice
    @Before("offer()")
    public void beforeOffer(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        File dir = new File("log/" + args[1]);
        if (!dir.exists()) {
            dir.mkdir();
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
            pw.write(";");
            pw.write(jsonMessage);
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

    }

    private String getDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

}
