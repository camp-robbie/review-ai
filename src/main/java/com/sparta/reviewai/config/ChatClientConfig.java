package com.sparta.reviewai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("너는 커머스 서비스의 리뷰를 요약하는 어시스턴트야. 과장하지 말고 리뷰에 있는 내용만 쓴다.")
                .build();
    }
}