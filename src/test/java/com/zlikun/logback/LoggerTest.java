package com.zlikun.logback;

import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/11 18:07
 */
public class LoggerTest {

    @Test
    public void test() {

        LoggerFactory.getLogger("com.zlikun").debug("I'm {} ,{} years old .", "Jinx" ,18);
        LoggerFactory.getLogger("com.zlikun").info("I'm {} ,{} years old .", "Jinx" ,18);
        LoggerFactory.getLogger("com.zlikun.user").debug("I'm {} ,{} years old .", "Ashe" ,24);

    }

}
