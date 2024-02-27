package com.coverflow.question.presentation;

import com.coverflow.global.annotation.AdminAuthorize;
import com.coverflow.global.annotation.MemberAuthorize;
import com.coverflow.global.handler.ResponseHandler;
import com.coverflow.question.application.QuestionService;
import com.coverflow.question.dto.QuestionDTO;
import com.coverflow.question.dto.request.SaveQuestionRequest;
import com.coverflow.question.dto.request.UpdateQuestionRequest;
import com.coverflow.question.dto.response.FindAllQuestionsResponse;
import com.coverflow.question.dto.response.FindQuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/question")
@RestController
public class QuestionController {

    private final QuestionService questionService;

    /**
     * 일단 보류
     */
    @GetMapping("/find-questions/{companyId}")
    public ResponseEntity<ResponseHandler<List<QuestionDTO>>> findAllQuestionsByCompanyId(
            @RequestParam(defaultValue = "0", value = "pageNo") @Valid final int pageNo,
            @RequestParam(defaultValue = "createdAt", value = "criterion") @Valid final String criterion,
            @PathVariable @Valid final Long companyId
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<List<QuestionDTO>>builder()
                        .statusCode(HttpStatus.OK)
                        .message("특정 회사의 질문 조회에 성공했습니다.")
                        .data(questionService.findAllQuestionsByCompanyId(pageNo, criterion, companyId))
                        .build()
                );
    }

    @GetMapping("/find-question/{questionId}")
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<FindQuestionResponse>> findQuestionById(
            @RequestParam(defaultValue = "0", value = "pageNo") @Valid final int pageNo,
            @RequestParam(defaultValue = "createdAt", value = "criterion") @Valid final String criterion,
            @PathVariable @Valid final Long questionId
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<FindQuestionResponse>builder()
                        .statusCode(HttpStatus.OK)
                        .message("특정 질문과 답변 조회에 성공했습니다.")
                        .data(questionService.findQuestionById(pageNo, criterion, questionId))
                        .build()
                );
    }

    @GetMapping("/admin/find-questions")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<List<FindAllQuestionsResponse>>> findAllQuestions(
            @RequestParam(defaultValue = "0", value = "pageNo") @Valid final int pageNo,
            @RequestParam(defaultValue = "createdAt", value = "criterion") @Valid final String criterion
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<List<FindAllQuestionsResponse>>builder()
                        .statusCode(HttpStatus.OK)
                        .message("전체 질문 조회에 성공했습니다.")
                        .data(questionService.findAllQuestions(pageNo, criterion))
                        .build()
                );
    }

    @GetMapping("/admin/find-by-status")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<List<FindAllQuestionsResponse>>> findQuestionsByStatus(
            @RequestParam(defaultValue = "0", value = "pageNo") @Valid final int pageNo,
            @RequestParam(defaultValue = "createdAt", value = "criterion") @Valid final String criterion,
            @RequestParam(defaultValue = "등록", value = "status") @Valid final String status
    ) {
        return ResponseEntity.ok()
                .body(ResponseHandler.<List<FindAllQuestionsResponse>>builder()
                        .statusCode(HttpStatus.OK)
                        .message("특정 상태의 질문 검색에 성공했습니다.")
                        .data(questionService.findQuestionsByStatus(pageNo, criterion, status))
                        .build()
                );
    }

    @PostMapping("/save-question")
    @MemberAuthorize
    public ResponseEntity<ResponseHandler<Void>> saveQuestion(
            @RequestBody @Valid final SaveQuestionRequest saveQuestionRequest,
            @AuthenticationPrincipal final UserDetails userDetails
    ) {
        questionService.saveQuestion(saveQuestionRequest, userDetails.getUsername());
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .message("질문 등록에 성공했습니다.")
                        .build());
    }

    @PostMapping("/admin/update-question")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> updateQuestion(
            @RequestBody @Valid final UpdateQuestionRequest updateQuestionRequest
    ) {
        questionService.updateQuestion(updateQuestionRequest);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .message("질문 수정에 성공했습니다.")
                        .build());
    }

    @PostMapping("/admin/delete-question/{questionId}")
    @AdminAuthorize
    public ResponseEntity<ResponseHandler<Void>> deleteQuestion(
            @PathVariable @Valid final Long questionId
    ) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok()
                .body(ResponseHandler.<Void>builder()
                        .statusCode(HttpStatus.OK)
                        .message("질문 삭제에 성공했습니다.")
                        .build());
    }
}
