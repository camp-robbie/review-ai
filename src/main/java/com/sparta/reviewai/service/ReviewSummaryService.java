package com.sparta.reviewai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewSummaryService {

    private final ChatClient chatClient;

    public ReviewSummaryService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String summarize(List<String> reviews) {
        return chatClient.prompt()
                .user("""
                        아래는 같은 상품에 달린 고객 리뷰 %d건이야.
                        구매를 고민하는 사람이 한눈에 읽을 수 있게 한 문단으로 정리해 줘.
                        여러 명이 같은 말을 하면 그걸 먼저 쓰고, 리뷰에 없는 내용은 지어내지 마.

                        [리뷰]
                        %s
                        """.formatted(reviews.size(), String.join("\n---\n", reviews)))
                .call()
                .content();
    }
}