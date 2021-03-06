package com.jyblife.datasource;
import com.jyblife.datasource.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = MybatisAutoConfiguration.class)
@MapperScan(
        basePackage = "com.jyblife.datasource.dao.mapper",
        configLocation = "classpath:mybatis/mybatis-config.xml",
        mapperLocations = "classpath*:mybatis/mapper/**/*Mapper*.xml"
)
public class Application implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
