package com.sparta.reviewai.tool;

import com.sparta.reviewai.domain.Product;
import com.sparta.reviewai.domain.ProductStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class ProductTools {

    private static final Logger log = LoggerFactory.getLogger(ProductTools.class);

    private final ProductStore productStore;

    public ProductTools(ProductStore productStore) {
        this.productStore = productStore;
    }

    @Tool(description = "우리 쇼핑몰에 등록된 전체 상품 수를 센다")
    public int countProducts() {
        return productStore.count();
    }

    @Tool(description = "상품 ID로 현재 재고 수량을 조회한다")
    public int getProductStock(
            @ToolParam(description = "조회할 상품의 ID. 예: 1") Long productId
    ) {
        return productStore.findById(productId)
                .map(Product::stockQuantity)
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 상품 ID입니다: " + productId));
    }

    public record RatingInfo(double averageRating, int reviewCount) {}

    @Tool(description = "상품 ID로 평균 별점과 리뷰 수를 조회한다")
    public RatingInfo getAverageRating(
            @ToolParam(description = "조회할 상품의 ID. 예: 1") Long productId
    ) {
        return productStore.findById(productId)
                .map(p -> new RatingInfo(p.averageRating(), p.reviewCount()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "존재하지 않는 상품 ID입니다: " + productId));
    }

}