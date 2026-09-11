package com.sparta.reviewai.config;

import com.sparta.reviewai.advisor.CostAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder builder,
            @Value("${app.llm.input-per-1m}") double inputPer1m,
            @Value("${app.llm.output-per-1m}") double outputPer1m
    ) {

        return builder
                .defaultSystem("너는 커머스 서비스의 리뷰를 요약하는 어시스턴트야. 과장하지 말고 리뷰에 있는 내용만 쓴다.")
                .defaultAdvisors(new CostAdvisor(inputPer1m, outputPer1m, 100))
                .build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)   // 최근 20개만 들고 있습니다
                .build();
    }
}