package com.zm.zmq.businesslogic;

import com.alibaba.fastjson.JSON;
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

@Aspect
@Component
public class OfferAspect {

    private Logger logger = LoggerFactory.getLogger(OfferAspect.class);

    // pointcut签名
    @Pointcut("execution(* com.zm.zmq.businesslogic.MyMessageQueue.offer(..))")
    public void offer() {
    }

    // advice
    @Before("offer()")
    public void beforeOffer(JoinPoint joinPoint) {
        File dir = new File("log");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File logFile = new File("log/" + getDate(new Date()) + ".log");
        System.out.println(logFile.getAbsolutePath());
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
        Object[] args = joinPoint.getArgs();
        StringBuilder sb = new StringBuilder();
        for (Object o : args) {
            sb.append(JSON.toJSONString(o)).append("\n");
        }
        String jsonMessage = sb.toString();
        OutputStream fos = null;
        PrintWriter pw = null;
        try {
            fos = new FileOutputStream(logFile);
            pw = new PrintWriter(fos);
            pw.write("------" + new Date() + "------" + "\n");
            pw.write(jsonMessage);
            pw.write("-----------------------" + "\n");
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
