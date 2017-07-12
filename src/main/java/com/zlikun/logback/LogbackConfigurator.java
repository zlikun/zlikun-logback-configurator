package com.zlikun.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.spi.ContextAwareBase;
import net.logstash.logback.encoder.LogstashEncoder;
import net.logstash.logback.stacktrace.ShortenedThrowableConverter;

import java.util.Arrays;

/**
 * @auther zlikun <zlikun-dev@hotmail.com>
 * @date 2017/7/11 18:04
 */
public class LogbackConfigurator extends ContextAwareBase implements Configurator {

    @Override
    public void configure(LoggerContext context) {

        // 模板布局Encoder
        // PatternLayoutEncoder encoder = patternLayoutEncoder("%date{yyyy/MM/dd HH:mm:ss.SSS} %-5level %-18thread %20.20logger - %message%n") ;

        // LogstashEncoder
        LogstashEncoder encoder = logstashEncoder() ;

        // 控制台Appender
        ConsoleAppender<ILoggingEvent> consoleAppender = consoleAppender(encoder) ;

        // 设置ROOT输出配置[CONSOLE ,DEBUG ,false]
        setAppender(context.getLogger(Logger.ROOT_LOGGER_NAME), consoleAppender, Level.INFO, false);
        setAppender(context.getLogger("com.zlikun.logback"), Level.DEBUG, true);

    }

    /**
     * LogstashEncoder
     * @return
     */
    LogstashEncoder logstashEncoder() {
        LogstashEncoder encoder = new LogstashEncoder() ;
        encoder.setContext(context);
        encoder.setEncoding("UTF-8");

        // 设置属性
        // 设置自定义字段(全局)
        encoder.setCustomFields("{\"_project\":\"zlikun-logback-configurator\" ,\"_author\":\"zlikun\"}");
        // 设置包含MDC信息(以JSON形式)
        encoder.setIncludeMdc(true);
        // 设置包含Caller信息，该设置比较影响性能，慎用
        // 包含：caller_class_name、caller_method_name、caller_file_name、caller_line_number 几个字段
        // {"caller_class_name":"com.zlikun.logback.LoggerTest","caller_method_name":"test","caller_file_name":"LoggerTest.java","caller_line_number":20}
        encoder.setIncludeCallerData(false);
        // 设置包含Context信息，效果参考测试用例注释部分
        encoder.setIncludeContext(false);
        // 设置排除的MDC字段信息
        // encoder.setExcludeMdcKeyNames(Arrays.asList("username"));
        // 添加排除的MDC字段信息，区别于#setExcludeMdcKeyNames()
        encoder.addExcludeMdcKeyName("username");
        // 与添加相反，类似于白名单、黑名单的区别，两者不能同时设置(二选一)
        // encoder.addIncludeMdcKeyName("userId");
        // 设置使用缩短的Logger名称，设定其触发缩短的长度
        encoder.setShortenedLoggerNameLength(20);
        // 设置时区，"GMT+08:00"表示东八区(由当前语言环境决定默认值，一般不用设定)，"GMT+00:00" / "GMT"表示格林威治时间
        // "@timestamp":"2017-07-12T13:59:35.144+08:00" 时间戳由日期、时间、时区构成
        // encoder.setTimeZone("GMT+08:00");
        // 设置@version值，取值为任意整数
        encoder.setVersion(1);
        // 设置字段名(修改)
        // LogstashFieldNames lfn = new LogstashFieldNames() ;
        // lfn.setTimestamp("_timestamp");
        // encoder.setFieldNames(lfn);
        // 设置异常转换器
        ShortenedThrowableConverter converter = new ShortenedThrowableConverter() ;
        converter.setContext(context);
        // 指定异常层数
        // converter.setMaxDepthPerThrowable(15);
        // 使用短类名，指定触发长度
        // converter.setShortenedClassNameLength(30);
        // 指定异常信息最大长度，超过部分将被截取
        converter.setMaxLength(2048);
        converter.setRootCauseFirst(true);
        // 设置排除条件，使用正则表达式
        converter.setExcludes(Arrays.asList("^sun.reflect.*" ,"^java\\.lang\\.reflect.*" ,"^org\\.junit.*" ,"^com.intellij.*"));
        encoder.setThrowableConverter(converter);

        // 设置立即刷新
        encoder.setImmediateFlush(true);
        // 设置最小缓冲区大小，单位：字节，默认：1024
        encoder.setMinBufferSize(64);
        // encoder.setJsonFactoryDecorator();
        // encoder.setJsonGeneratorDecorator();

        // encoder.start();
        return encoder ;
    }

    /**
     * 模板布局Encoder
     * @param pattern
     * @return
     */
    PatternLayoutEncoder patternLayoutEncoder(String pattern) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern(pattern);
        return encoder ;
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