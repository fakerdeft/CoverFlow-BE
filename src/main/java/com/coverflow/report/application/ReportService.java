package com.coverflow.report.application;

import com.coverflow.question.domain.Answer;
import com.coverflow.question.domain.AnswerStatus;
import com.coverflow.question.exception.AnswerException;
import com.coverflow.question.infrastructure.AnswerRepository;
import com.coverflow.report.domain.Report;
import com.coverflow.report.domain.ReportStatus;
import com.coverflow.report.dto.ReportDTO;
import com.coverflow.report.dto.request.SaveReportRequest;
import com.coverflow.report.dto.response.FindReportResponse;
import com.coverflow.report.exception.ReportException;
import com.coverflow.report.infrastructure.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.coverflow.global.constant.Constant.LARGE_PAGE_SIZE;
import static com.coverflow.global.util.PageUtil.generatePageDesc;
import static com.coverflow.report.domain.ReportType.ANSWER;
import static com.coverflow.report.domain.ReportType.QUESTION;

@RequiredArgsConstructor
@Service
public class ReportService {

    private final AnswerRepository answerRepository;
    private final ReportRepository reportRepository;

    /**
     * [특정 회원의 신고 리스트 조회 메서드]
     */
    @Transactional(readOnly = true)
    public FindReportResponse findReportsByMemberId(
            final String memberId,
            final int pageNo,
            final String criterion
    ) {
        Page<Report> reports = reportRepository.findReportsByMemberId(memberId, generatePageDesc(pageNo, LARGE_PAGE_SIZE, criterion))
                .orElseThrow(() -> new ReportException.ReportNotFoundException(memberId));

        return FindReportResponse.of(
                reports.getTotalPages(),
                reports.getContent().stream()
                        .map(ReportDTO::from)
                        .toList()
        );
    }

    /**
     * [관리자 전용: 전체 신고 리스트 조회 메서드]
     */
    @Transactional(readOnly = true)
    public FindReportResponse findReports(
            final int pageNo,
            final String criterion
    ) {
        Page<Report> reports = reportRepository.findAllReports(generatePageDesc(pageNo, LARGE_PAGE_SIZE, criterion))
                .orElseThrow(ReportException.ReportNotFoundException::new);

        return FindReportResponse.of(
                reports.getTotalPages(),
                reports.getContent().stream()
                        .map(ReportDTO::from)
                        .toList()
        );
    }

    /**
     * [관리자 전용: 특정 상태 신고 조회 메서드]
     * 특정 상태(등록/삭제)의 신고를 조회하는 메서드
     */
    @Transactional(readOnly = true)
    public FindReportResponse findReportsByStatus(
            final int pageNo,
            final String criterion,
            final ReportStatus reportStatus
    ) {
        Page<Report> reports = reportRepository.findAllByReportStatus(generatePageDesc(pageNo, LARGE_PAGE_SIZE, criterion), reportStatus)
                .orElseThrow(() -> new ReportException.ReportNotFoundException(reportStatus));

        return FindReportResponse.of(
                reports.getTotalPages(),
                reports.getContent().stream()
                        .map(ReportDTO::from)
                        .toList()
        );
    }

    /**
     * [신고 등록 메서드]
     */
    @Transactional
    public void saveReport(
            final SaveReportRequest request,
            final String memberId
    ) {
        Report report = Report.builder().build();

        if ((QUESTION).equals(request.type())) {
            report = new Report(request, memberId);
        }
        if ((ANSWER).equals(request.type())) {
            Answer answer = answerRepository.findByIdAndAnswerStatus(request.id(), AnswerStatus.REGISTRATION)
                    .orElseThrow(() -> new AnswerException.AnswerNotFoundException(request.id()));

            report = new Report(request, answer, memberId);
        }

        reportRepository.save(report);
    }

    /**
     * [관리자 전용: 신고 삭제 메서드]
     */
    @Transactional
    public void deleteReport(final long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ReportException.ReportNotFoundException(reportId));

        report.updateReportStatus(ReportStatus.DELETION);
    }
}