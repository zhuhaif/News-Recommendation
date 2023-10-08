package com.five.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.WebApplicationInitializer;

@MapperScan(value = "com.five.springboot.mapper")//使用MapperScan批量扫描所有新的mapper接口，从而不用添加@Mapper注解
@SpringBootApplication
@EnableScheduling//配置SpringBoot内置的定时任务
public class NewsrecommendationApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(NewsrecommendationApplication.class, args);
    }

    //运行war包的启动项
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(NewsrecommendationApplication.class);
    }

}
