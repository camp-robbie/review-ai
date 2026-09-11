package com.sparta.reviewai.controller;

import com.sparta.reviewai.dto.ReviewRequest;
import com.sparta.reviewai.dto.ReviewSummary;
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

    @PostMapping("/summarize/v2")
    public ReviewSummary summarizeV2(@RequestBody ReviewRequest request) {
        return reviewSummaryService.summarizeV2(request.reviews());
    }

    @PostMapping("/summarize/v3")
    public ReviewSummary summarizeV3(@RequestBody ReviewRequest request) {
        return reviewSummaryService.summarizeV3(request.reviews());
    }

    @PostMapping("/summarize/with-photo")
    public ReviewSummary summarizeWithPhoto(@RequestBody ReviewRequest request) {
        return reviewSummaryService.summarizeWithPhoto(request.reviews());
    }

    @PostMapping("/summarize/v3-logged")
    public ReviewSummary summarizeV3Logged(@RequestBody ReviewRequest request) {
        return reviewSummaryService.summarizeV3Logged(request.reviews());
    }

}
