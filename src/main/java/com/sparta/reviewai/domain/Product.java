package com.sparta.reviewai.domain;

public record Product(
        Long id,
        String name,
        int stockQuantity,
        double averageRating,
        int reviewCount
) {
}