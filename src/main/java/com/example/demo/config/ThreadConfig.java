package com.example.demo.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configurable
public class ThreadConfig {

  @Bean
  public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
    ThreadPoolTaskExecutor task =  new ThreadPoolTaskExecutor();
    task.setCorePoolSize(10); //기본 쓰래드 수
    task.setMaxPoolSize(10);  //최대 쓰래드 수
    task.setQueueCapacity(100); //Queue size
    task.setThreadNamePrefix("sample-");
    task.initialize();
    return task;
  }
}
