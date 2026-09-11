package com.sparta.reviewai.service;

import com.sparta.reviewai.dto.ReviewSummary;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

@Service
public class ReviewSummaryService {

    private final ChatClient chatClient;
    private static final String SYSTEM_PROMPT = """
                    당신은 수만 건의 커머스 리뷰를 다뤄온 리뷰 분석가입니다.
                    같은 상품에 달린 리뷰 여러 건을 받아, 구매를 고민하는 사람이 판단할 수 있도록
                    리뷰에 쓰인 사실만 추려 하나로 종합합니다.
                    리뷰에 없는 내용은 지어내지 않습니다.

                    규칙:
                    - summary 는 한 문장, 60자 이내로 씁니다.
                    - sentiment 는 리뷰 전체를 종합해 긍정 / 부정 / 중립 셋 중 하나만 씁니다. 의견이 갈리면 중립입니다.
                    - pros 와 cons 는 각각 최대 3개, 명사구로 짧게 씁니다. 여러 리뷰에서 반복된 것을 먼저 씁니다.
                    - 근거가 없으면 빈 배열로 남깁니다. 억지로 채우지 않습니다.
                    - recommendedStars 는 1~5 정수입니다.

                    예시 (셋 다 리뷰 여러 건을 종합한 결과입니다):
                    - summary: 배송은 빠르지만 마감 지적이 반복되는 제품, sentiment: 중립, pros: 빠른 배송, 튼튼한 포장, cons: 거친 봉제, recommendedStars: 3
                    - summary: 가격 대비 품질에 만족한다는 평이 대부분인 제품, sentiment: 긍정, pros: 가격 대비 품질, 빠른 배송, cons: , recommendedStars: 5
                    - summary: 사이즈가 작다는 지적이 여러 건 반복되는 제품, sentiment: 부정, pros: , cons: 작은 사이즈, 색상 차이, recommendedStars: 2
            """;

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

    public ReviewSummary summarizeV2(List<String> reviews) {
        // 1) 질문을 담아 준비
        ChatClient.CallResponseSpec response = chatClient.prompt()
                .user("""
                    아래는 같은 상품에 달린 고객 리뷰 %d건이야.
                    구매를 고민하는 사람이 판단할 수 있게 분석해 줘.

                    [리뷰]
                    %s
                    """.formatted(reviews.size(), String.join("\n---\n", reviews)))
                .call();

        // 2) 규격을 넘기면 그 모양으로 받아옵니다
        return response.entity(ReviewSummary.class);
    }

    public ReviewSummary summarizeV3(List<String> reviews) {
        ChatClient.CallResponseSpec response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user("""
                    아래는 같은 상품에 달린 고객 리뷰 %d건입니다.

                    [리뷰]
                    %s
                    """.formatted(reviews.size(), String.join("\n---\n", reviews)))
                .call();

        return response.entity(ReviewSummary.class);
    }

    public ReviewSummary summarizeWithPhoto(List<String> reviews) {
        var photo = new ClassPathResource("mac-mini.png");

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u
                        .text("""
                            아래는 같은 상품에 달린 고객 리뷰 %d건입니다.
                            함께 넘긴 상품 사진도 같이 보고 종합해 주세요.
                            사진으로 확인되는 내용은 그렇게 쓰고,
                            사진으로 알 수 없는 것은 리뷰에 쓰인 대로만 씁니다.
                            사진에 없는 것은 추측하지 마세요.

                            [리뷰]
                            %s
                            """.formatted(reviews.size(), String.join("\n---\n", reviews)))
                        .media(MimeTypeUtils.IMAGE_PNG, photo))
                .call()
                .entity(ReviewSummary.class);
    }

    public ReviewSummary summarizeV3Logged(List<String> reviews) {
        return chatClient.prompt()
                .advisors(new SimpleLoggerAdvisor())
                .system(SYSTEM_PROMPT)
                .user("""
                    아래는 같은 상품에 달린 고객 리뷰 %d건입니다.

                    [리뷰]
                    %s
                    """.formatted(reviews.size(), String.join("\n---\n", reviews)))
                .call()
                .entity(ReviewSummary.class);
    }

}