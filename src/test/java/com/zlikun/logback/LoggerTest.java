package com.zlikun.logback;

import ch.qos.logback.classic.LoggerContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Date;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/11 18:07
 */
public class LoggerTest {

    @Test
    public void test() {

        Logger logger = LoggerFactory.getLogger(LoggerTest.class) ;

        // 测试在Context中保存信息，由encoder.setIncludeContext(true);设定来控制是否输出到日志中
        // 当设定为true时，#putProperty()方法设定的键值对会被输出到日志中，#putObject()方法设置信息则不会
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.putProperty("ctx_name" ,"ctx_zlikun");
        context.putObject("ctx_birthday" ,new Date());

        // 添加键值对到MDC中
        MDC.put("userId" ,"10000");
        MDC.put("username" ,"zlikun");

        // 输出日志
        logger.debug("I'm {} ,{} years old .", "Ashe" ,24);
        logger.debug("程序执行出错!" ,new IllegalArgumentException("参数错误"));

    }

}
