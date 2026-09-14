package com.sparta.reviewai.controller;

import com.sparta.reviewai.tool.ProductTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ProductTools productTools;

    public ChatController(ChatClient chatClient, ChatMemory chatMemory, ProductTools productTools) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
        this.productTools = productTools;
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String conversationId,
                       @RequestParam String message) {
        return chatClient.prompt()
                .advisors(a -> a
                        .advisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)   // conversationId 를 받기만 하고 아직 쓰지 않습니다
                .call()
                .content();
    }

    @GetMapping("/chat/safe")
    public String safeChat(@RequestParam String conversationId,
                           @RequestParam String message) {

        // 1) 가장 밖 : 입력을 먼저 검사합니다
        var safeGuard = SafeGuardAdvisor.builder()
                .sensitiveWords(List.of("폭탄", "해킹", "마약"))
                .failureResponse("죄송합니다. 해당 주제는 도와드릴 수 없어요.")
                .order(1)
                .build();

        // 2) 통과한 요청에만 이전 대화를 붙입니다
        var memory = MessageChatMemoryAdvisor.builder(chatMemory)
                .order(2)
                .build();

        // 3) 가장 안 : 최종적으로 나가는 요청 전문을 찍습니다
        //    Memory 보다 큰 order 여야 "이전 대화가 붙은 뒤"를 볼 수 있습니다
        var logger = new SimpleLoggerAdvisor(3);

        return chatClient.prompt()
                .advisors(a -> a
                        .advisors(safeGuard, memory, logger)
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/ask/v1")
    public String askV1(@RequestParam String message) {
        return chatClient.prompt()
                .tools(productTools)     // ← 도구를 넘깁니다
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/ask/v2")
    public String askV2(@RequestParam String message) {
        return chatClient.prompt()
                .tools(productTools)
                .user(message)
                .call()
                .content();
    }

    @GetMapping("/ask/v3")
    public String askV3(@RequestParam String message) {
        return chatClient.prompt()
                .tools(productTools)
                .user(message)
                .call()
                .content();
    }

}