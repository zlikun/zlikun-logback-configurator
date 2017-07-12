package com.zlikun.logback;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

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

    /**
     * 测试异步日志，通过不同参数组合观察各参数工作情况
     *
     * [queueSize = 8 / neverBlock = false / discardingThreshold = 2 / maxFlushTime = 1000]
     * WARN 300 / DEBUG 123
     * WARN 300 / DEBUG 111
     * WARN 300 / DEBUG 75
     * 由于neverBlock = false，所以日志将不会发生队外丢弃(程序阻塞，等待入队)，又由于discardingThreshold = 2，导致DEBUG级别日志有一定几率队内丢弃
     *
     * [queueSize = 8 / neverBlock = true / discardingThreshold = 2 / maxFlushTime = 1000]
     * WARN 48  / DEBUG 40
     * WARN 38  / DEBUG 39
     * WARN 34  / DEBUG 30
     * 由于neverBlock = true，所以日志发生了队外丢弃(程序不阻塞，超出队列容量部分直接丢弃)，导致WARN、DEBUG级别的日志都发生了丢弃
     *
     * [queueSize = 8 / neverBlock = false / discardingThreshold = 0 / maxFlushTime = 1000]
     * WARN 300 / DEBUG 300
     * WARN 300 / DEBUG 300
     * WARN 300 / DEBUG 300
     * 由于neverBlock = false且discardingThreshold = 0，队内、队外都不丢弃日志，所以日志没有丢失
     */
    @Test @Ignore
    public void async() {

        final Logger logger = LoggerFactory.getLogger("com.zlikun.async") ;

        // 计数器
        final AtomicInteger counter = new AtomicInteger() ;

        ExecutorService exec = Executors.newFixedThreadPool(20) ;

        for (int i = 0; i < 100; i++) {
            final int index  = i ;
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    logger.debug("{}\t- D - {}" ,counter.incrementAndGet() ,index);
                    logger.debug("{}\t- D - {}" ,counter.incrementAndGet() ,index);
                    logger.debug("{}\t- D - {}" ,counter.incrementAndGet() ,index);
                    logger.warn("{}\t- W  - {}" ,counter.incrementAndGet() ,index);
                    logger.warn("{}\t- W  - {}" ,counter.incrementAndGet() ,index);
                    logger.warn("{}\t- W  - {}" ,counter.incrementAndGet() ,index);
                }
            });
        }

        exec.shutdown();
        while (!exec.isTerminated()) ;
        logger.info("程序执行完成，计数器：{} !" ,counter.get());

    }

    /**
     * 测试异步Appender不同参数设定对性能影响 [50 / 100000 * 12]
     *
     * [queueSize = 65536 / neverBlock = false / discardingThreshold = -1 / maxFlushTime = 1000]
     * 计数器：1200000 ，执行耗时：4322 毫秒
     * 计数器：1200000 ，执行耗时：4346 毫秒
     * 计数器：1200000 ，执行耗时：4253 毫秒
     *
     * [queueSize = 65536 / neverBlock = false / discardingThreshold = 0 / maxFlushTime = 1000]
     * 计数器：1200000 ，执行耗时：7464 毫秒
     * 计数器：1200000 ，执行耗时：7149 毫秒
     * 计数器：1200000 ，执行耗时：7344 毫秒
     *
     * [queueSize = 65536 / neverBlock = true / discardingThreshold = 0 / maxFlushTime = 1000]
     * 计数器：1200000 ，执行耗时：943 毫秒
     * 计数器：1200000 ，执行耗时：841 毫秒
     * 计数器：1200000 ，执行耗时：890 毫秒
     */
    @Test @Ignore
    public void async_performance() {

        final Logger logger = LoggerFactory.getLogger("com.zlikun.async") ;

        // 计数器
        final AtomicInteger counter = new AtomicInteger() ;

        ExecutorService exec = Executors.newFixedThreadPool(50) ;

        long time = System.currentTimeMillis() ;

        for (int i = 0; i < 100 * 1000; i++) {
            exec.execute(new Runnable() {
                @Override
                public void run() {
                    logger.debug("{}\t- 测试DEBUG日志" ,counter.incrementAndGet());
                    logger.debug("{}\t- 测试DEBUG日志" ,counter.incrementAndGet());
                    logger.debug("{}\t- 测试DEBUG日志" ,counter.incrementAndGet());
                    logger.info("{}\t- 测试INFO日志" ,counter.incrementAndGet());
                    logger.info("{}\t- 测试INFO日志" ,counter.incrementAndGet());
                    logger.info("{}\t- 测试INFO日志" ,counter.incrementAndGet());
                    logger.warn("{}\t- 测试WARN日志" ,counter.incrementAndGet());
                    logger.warn("{}\t- 测试WARN日志" ,counter.incrementAndGet());
                    logger.warn("{}\t- 测试WARN日志" ,counter.incrementAndGet());
                    logger.error("{}\t- 测试ERROR日志" ,counter.incrementAndGet());
                    logger.error("{}\t- 测试ERROR日志" ,counter.incrementAndGet());
                    logger.error("{}\t- 测试ERROR日志" ,counter.incrementAndGet());
                }
            });
        }

        exec.shutdown();
        while (!exec.isTerminated()) ;
        System.out.printf("程序执行完成，计数器：%d ，执行耗时：%d 毫秒 !\n" ,counter.get() ,System.currentTimeMillis() - time);

    }

}
