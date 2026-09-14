package com.sparta.reviewai.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.metadata.Usage;

import java.util.Objects;

public class CostAdvisor implements BaseAdvisor {

    private static final Logger log = LoggerFactory.getLogger(CostAdvisor.class);

    private final double inputPer1m;
    private final double outputPer1m;
    private final int order;

    public CostAdvisor(double inputPer1m, double outputPer1m, int order) {
        this.inputPer1m = inputPer1m;
        this.outputPer1m = outputPer1m;
        this.order = order;
    }

    // 요청이 나갈 때 : 지금은 그대로 통과시킵니다
    @Override
    public ChatClientRequest before(ChatClientRequest request, AdvisorChain chain) {
        return request;
    }

    // 응답이 돌아올 때 : 여기서 토큰을 읽습니다
    @Override
    public ChatClientResponse after(ChatClientResponse response, AdvisorChain chain) {
        var chatResponse = response.chatResponse();
        if (chatResponse == null || chatResponse.getMetadata().getUsage() == null) {
            return response;   // 사용량을 안 주는 경우도 있습니다
        }

        Usage usage = chatResponse.getMetadata().getUsage();
        double cost = (usage.getPromptTokens() * inputPer1m
                + usage.getCompletionTokens() * outputPer1m) / 1_000_000.0;

        log.info("LLM 호출 — 입력={}, 출력={}, 합={}, 비용=${}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens(),
                String.format("%.6f", cost));

        var output = Objects.requireNonNull(response.chatResponse().getResult()).getOutput();   // AssistantMessage

        if (output.hasToolCalls()) {
            output.getToolCalls().forEach(tc ->
                    log.info("  ↳ 모델이 요증한 도구: {} {}", tc.name(), tc.arguments()));
        }

        return response;
    }

    @Override
    public int getOrder() {
        return order;
    }
}