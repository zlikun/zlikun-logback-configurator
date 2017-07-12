# zlikun-logback-configurator
基于SPI机制配置Logback

#### [配置](https://logback.qos.ch/manual/configuration.html)
```
# 在classpath下添加如下文件，注意文件目录及文件名，文件中只有一行
# 为一个实现了Configurator接口的类全名，Logback将自动加载该实现以配置Logback
META-INF/services/ch.qos.logback.classic.spi.Configurator
```

#### [Logstash](https://github.com/logstash/logstash-logback-encoder)
- <https://github.com/logstash/logstash-logback-encoder#async-appenders>
- 
