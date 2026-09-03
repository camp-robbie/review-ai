package com.sparta.reviewai.controller;

import com.sparta.reviewai.dto.ReviewRequest;
import com.sparta.reviewai.service.ReviewSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewSummaryService reviewSummaryService;

    @PostMapping("/summarize")
    public String summarize(@RequestBody ReviewRequest request) {
        return reviewSummaryService.summarize(request.reviews());
    }
}
