package com.coverflow.inquiry.presentation;

import com.coverflow.global.annotation.AdminAuthorize;
import com.coverflow.global.annotation.MemberAuthorize;
import com.coverflow.global.handler.ResponseHandler;
import com.coverflow.inquiry.application.InquiryService;
import com.coverflow.inquiry.domain.InquiryStatus;
import com.coverflow.inquiry.dto.request.SaveInquiryRequest;
import com.coverflow.inquiry.dto.request.UpdateInquiryRequest;
import com.coverflow.inquiry.dto.response.FindAllInquiriesResponse;
import com.coverflow.inquiry.dto.response.FindInquiryResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/inquiry")
@RestController
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<FindInquiryResponse>> findInquiryByMemberId(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") @NotBlank final String criterion,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindInquiryResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(inquiryService.findInquiryByMemberId(pageNo, criterion, userDetails.getUsername()))
                        .build());
    }

    @GetMapping("/admin")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FindAllInquiriesResponse>> findInquiries(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") @NotBlank final String criterion
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindAllInquiriesResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(inquiryService.findInquiries(pageNo, criterion))
                        .build());
    }

    @GetMapping("/admin/status")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<FindAllInquiriesResponse>> findInquiriesByStatus(
            @RequestParam @PositiveOrZero final int pageNo,
            @RequestParam(defaultValue = "createdAt") @NotBlank final String criterion,
            @RequestParam @Valid final InquiryStatus inquiryStatus
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindAllInquiriesResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .data(inquiryService.findInquiriesByStatus(pageNo, criterion, inquiryStatus))
                        .build()
                );
    }

    @PostMapping
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<Void>> saveInquiry(
            @RequestBody @Valid final SaveInquiryRequest saveInquiryRequest,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        inquiryService.saveInquiry(saveInquiryRequest, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.CREATED)
                        .build());
    }

    @PutMapping("/admin")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> updateInquiry(
            @RequestBody @Valid final UpdateInquiryRequest updateInquiryRequest
    ) {
        inquiryService.updateInquiry(updateInquiryRequest);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }

    @DeleteMapping("/admin/{inquiryId}")
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<Void>> deleteInquiry(
            @PathVariable @Positive final long inquiryId
    ) {
        inquiryService.deleteInquiry(inquiryId);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.NO_CONTENT)
                        .build());
    }
}