package com.amosannn.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
@EnableCaching
public class RedisCacheConfiguration extends CachingConfigurerSupport {

  Logger logger = LoggerFactory.getLogger(RedisCacheConfiguration.class);

  @Value("${spring.redis.host}")
  private String host;

  @Value("${spring.redis.port}")
  private int port;

  @Value("${spring.redis.timeout}")
  private int timeout;

  @Value("${spring.redis.pool.max-active}")
  private int maxActive;

  @Value("${spring.redis.pool.max-idle}")
  private int maxIdle;

  @Value("${spring.redis.pool.min-idle}")
  private int minIdle;

  @Value("${spring.redis.pool.max-wait}")
  private long maxWaitMillis;

//  @Value("${spring.redis.password}")
//  private String password;

  @Bean
  public JedisPool redisPoolFactory() {
    logger.info("JedisPool注入成功！！");
    logger.info("redis地址：" + host + ":" + port);
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(maxActive);
    jedisPoolConfig.setMaxIdle(maxIdle);
    jedisPoolConfig.setMinIdle(minIdle);//设置最小空闲数
    jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
    jedisPoolConfig.setTestOnBorrow(true);
    jedisPoolConfig.setTestOnReturn(true);
    //Idle时进行连接扫描
    jedisPoolConfig.setTestWhileIdle(true);
    //表示idle object evitor两次扫描之间要sleep的毫秒数
    jedisPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
    //表示idle object evitor每次扫描的最多的对象数
    jedisPoolConfig.setNumTestsPerEvictionRun(10);
    //表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
    jedisPoolConfig.setMinEvictableIdleTimeMillis(60000);

//    JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
    JedisPool jedisPool = new JedisPool(jedisPoolConfig, host, port, timeout);

    return jedisPool;
  }
}
