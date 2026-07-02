package com.example.kkopja.config; // 본인 프로젝트 패키지명으로 맞추기

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 💡 스프링에게 "이건 설정 파일이야!"라고 알려주는 어노테이션
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 웹 브라우저에서 /uploads/어쩌구저쩌구.jpg 로 요청이 들어오면
        registry.addResourceHandler("/uploads/**")
                // 내 컴퓨터의 C:/kkopja/uploads/ 폴더 안에서 파일을 찾아서 돌려줘!
                .addResourceLocations("file:///D:/projects/uploads/");
    }
}