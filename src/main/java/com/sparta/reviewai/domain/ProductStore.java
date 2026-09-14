package com.sparta.reviewai.domain;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ProductStore {

    private final Map<Long, Product> products;

    public ProductStore() {
        this.products = List.of(
                new Product(1L, "면 100% 기본 티셔츠", 42, 4.3, 128),
                new Product(2L, "공기청정기 필터 2개입", 0, 4.7, 512),
                new Product(3L, "스테인리스 보온병", 7, 3.9, 64),
                new Product(4L, "경추 지지 메모리폼 베개", 210, 4.1, 33)
        ).stream().collect(Collectors.toMap(Product::id, p -> p));
    }

    public int count() {
        return products.size();
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }
}