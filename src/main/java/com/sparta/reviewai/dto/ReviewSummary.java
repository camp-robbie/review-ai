package com.sparta.reviewai.dto;

import java.util.List;

public record ReviewSummary(
        String summary,
        String sentiment,
        List<String> pros,
        List<String> cons,
        int recommendedStars
) {
}