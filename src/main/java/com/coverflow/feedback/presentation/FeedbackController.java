package com.coverflow.feedback.presentation;


import com.coverflow.feedback.application.FeedbackService;
import com.coverflow.feedback.dto.request.SaveFeedbackRequest;
import com.coverflow.feedback.dto.response.FeedbackResponse;
import com.coverflow.global.annotation.AdminAuthorize;
import com.coverflow.global.handler.ResponseHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@RestController
public class FeedbackController {

    private FeedbackService feedbackService;

    @GetMapping("/admin")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FeedbackResponse>> findFeedback(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") final String criterion
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FeedbackResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(feedbackService.findFeedback(pageNo, criterion))
                        .build());
    }

    @PostMapping
    public ResponseEntity<ResponseHandler<Void>> saveFeedback(
            @RequestBody @Valid final SaveFeedbackRequest request
    ) {
        feedbackService.saveFeedback(request);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.CREATED)
                        .build());
    }

    @DeleteMapping("/admin/{feedbackId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> deleteFeedback(
            @PathVariable @Positive final long feedbackId
    ) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }
}
