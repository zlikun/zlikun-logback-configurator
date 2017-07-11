package com.zlikun.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/11 18:04
 */
public class LogbackConfigurator extends ContextAwareBase implements Configurator {

    @Override
    public void configure(LoggerContext context) {
        addInfo("日志组件正使用默认配置 .");

//        // 使用默认配置
//        ConsoleAppender<ILoggingEvent> consoleAppender = defaultConsoleAppender() ;

        // 自定义Encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        // https://logback.qos.ch/manual/layouts.html#ClassicPatternLayout
        encoder.setPattern("%date{yyyy/MM/dd HH:mm:ss.SSS} %5level [%thread] %20.20logger - %message%n");
        ConsoleAppender<ILoggingEvent> consoleAppender = consoleAppender(encoder) ;

        // 文件Appender
        // FileAppender<ILoggingEvent> fileAppender = fileAppender(encoder) ;
        // 滚动文件Appender
        FileAppender<ILoggingEvent> fileAppender = rollingFileAppender(encoder) ;

        // 设置ROOT输出配置[CONSOLE ,DEBUG ,false]
        setAppender(context.getLogger(Logger.ROOT_LOGGER_NAME), consoleAppender, Level.INFO, false);
        // additive 设置为 true ，表示日志向上传递，使用其上级配置输出，如果指定了不同类的Appender，则输出多次
        setAppender(context.getLogger("com.zlikun.user"), fileAppender, Level.DEBUG, true);

    }

    /**
     * 滚动文件Appender，自定义Encoder
     * @param encoder
     * @return
     */
    FileAppender<ILoggingEvent> rollingFileAppender(Encoder encoder) {
        if (encoder == null) throw new IllegalArgumentException("encoder is required .") ;
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<ILoggingEvent>();
        appender.setContext(this.context);
        appender.setName("file");

        // 设置输出日志文件
        appender.setAppend(true);
        appender.setBufferSize(FileSize.valueOf("4kb"));
        appender.setFile("./target/log/server.log");
        appender.setPrudent(false);
        appender.setImmediateFlush(true);

        // 设置滚动策略
        TimeBasedRollingPolicy policy = new TimeBasedRollingPolicy() ;
        // 设置上下文
        policy.setContext(this.context);
        // 设置启动时清理历史
        policy.setCleanHistoryOnStart(false);
        // 设置最大历史文件5个
        policy.setMaxHistory(5);
        // 设置文件名称模板，这里用于测试，以秒滚动
        policy.setFileNamePattern("./target/log/server.%d{HHmmss}.zip");
        // 设置总文件容量，默认：0，表示不限制
        policy.setTotalSizeCap(FileSize.valueOf("16kb"));
        // parent and context required
        // https://github.com/tony19/logback-android/wiki/Appender-Notes#configuration-in-code
        policy.setParent(appender);
        // 启动滚动策略
        policy.start();
        appender.setRollingPolicy(policy);

        encoder.setContext(this.context);
        if (!encoder.isStarted()) encoder.start();
        appender.setEncoder(encoder);

        appender.start();
        return appender ;
    }

    /**
     * 文件Appender，自定义Encoder
     * @param encoder
     * @return
     */
    FileAppender<ILoggingEvent> fileAppender(Encoder encoder) {
        if (encoder == null) throw new IllegalArgumentException("encoder is required .") ;
        FileAppender<ILoggingEvent> appender = new FileAppender<ILoggingEvent>() ;
        // 设置context要在设置文件相关属性之前
        appender.setContext(this.context);
        appender.setName("file");

        // 设置以追加方式记录日志
        appender.setAppend(true);
        // 设置文件缓存区，可选单位：[kb/mb/gb]，默认：8192 (字节，即：8kb)
        // appender.setBufferSize(FileSize.valueOf("8192"));
        appender.setBufferSize(FileSize.valueOf("4kb"));
        // 设置输出文件路径
        appender.setFile("./target/log/server.log");
        // 设置多个JVM日志输出可以安全输出到同一日志文件
        appender.setPrudent(false);
        // 设置立即刷新
        appender.setImmediateFlush(true);

        encoder.setContext(this.context);
        if (!encoder.isStarted()) encoder.start();
        appender.setEncoder(encoder);
        appender.start();
        return appender ;
    }

    /**
     * 控制台Appender，自定义Encoder
     * @param encoder
     * @return
     */
    ConsoleAppender<ILoggingEvent> consoleAppender(Encoder encoder) {
        if (encoder == null) throw new IllegalArgumentException("encoder is required .") ;
        // 构造Appender实例
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        // 设置上下文
        appender.setContext(this.context);
        // 设置名称
        appender.setName("console");
        // 设置Encoder上下文
        encoder.setContext(this.context);
        // encoder要在appender启动之前启动，否则不会输出日志
        if (!encoder.isStarted()) encoder.start();
        // 设置Encoder
        appender.setEncoder(encoder);
        // 启动Appender
        appender.start();
        // 返回实例
        return appender;
    }

    /**
     * 默认控制台Appender
     * @see ch.qos.logback.classic.BasicConfigurator
     * @return
     */
    ConsoleAppender<ILoggingEvent> defaultConsoleAppender() {
        // 构造Appender实例
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<ILoggingEvent>();
        // 设置上下文
        appender.setContext(this.context);
        // 设置名称
        appender.setName("console");
        // 设置布局Encoder
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
        encoder.setContext(this.context);
        // 设置布局
        TTLLLayout layout = new TTLLLayout();
        // 设置上下文
        layout.setContext(this.context);
        layout.start();
        encoder.setLayout(layout);
        // 设置Encoder
        appender.setEncoder(encoder);
        // 启动Appender
        appender.start();
        // 返回实例
        return appender;
    }

    private void setAppender(Logger logger, Appender<ILoggingEvent> appender, Level level, boolean additive) {
        if (logger == null) return;
        if (appender != null) logger.addAppender(appender);
        if (level != null) logger.setLevel(level);
        logger.setAdditive(additive);
    }

    private void setAppender(Logger logger, Appender<ILoggingEvent> appender, Level level) {
        this.setAppender(logger ,appender ,level ,false);
    }

    private void setAppender(Logger logger, Level level, boolean additive) {
        this.setAppender(logger ,null ,level ,additive);
    }

    private void setAppender(Logger logger, Level level) {
        this.setAppender(logger ,null ,level);
    }

}