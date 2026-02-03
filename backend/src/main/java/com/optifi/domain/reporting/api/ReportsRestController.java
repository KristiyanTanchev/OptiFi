package com.optifi.domain.reporting.api;

import com.optifi.domain.reporting.api.request.ReportCategoriesRequestDto;
import com.optifi.domain.reporting.api.request.ReportSummaryRequestDto;
import com.optifi.domain.reporting.api.request.ReportTimeChartRequestDto;
import com.optifi.domain.reporting.api.response.ReportCategoriesResponseDto;
import com.optifi.domain.reporting.api.response.ReportSummaryResponseDto;
import com.optifi.domain.reporting.api.response.ReportTimeChartResponseDto;
import com.optifi.domain.reporting.application.ReportService;
import com.optifi.domain.reporting.application.command.ReportCategoriesCommand;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.command.ReportTimeChartCommand;
import com.optifi.domain.reporting.application.result.ReportCategoriesResult;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
import com.optifi.domain.reporting.application.result.ReportTimeChartResult;
import com.optifi.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class ReportsRestController {

    private final ReportService reportService;

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponseDto> getSummaryReport(
            @Valid @ModelAttribute ReportSummaryRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReportSummaryCommand cmd = requestDto.toCommand(principal.getId());
        ReportSummaryResult result = reportService.getReportSummary(cmd);
        ReportSummaryResponseDto response = ReportSummaryResponseDto.from(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ReportCategoriesResponseDto> getCategoriesReport(
            @Valid @ModelAttribute ReportCategoriesRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReportCategoriesCommand cmd = requestDto.toCommand(principal.getId());
        ReportCategoriesResult result = reportService.getReportCategories(cmd);
        ReportCategoriesResponseDto response = ReportCategoriesResponseDto.from(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time-chart")
    public ResponseEntity<ReportTimeChartResponseDto> getTimeChartReport(
            @Valid @ModelAttribute ReportTimeChartRequestDto requestDto,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        ReportTimeChartCommand cmd = requestDto.toCommand(principal.getId());
        ReportTimeChartResult result = reportService.getReportTimeChart(cmd);
        ReportTimeChartResponseDto response = ReportTimeChartResponseDto.from(result);
        return ResponseEntity.ok(response);
    }
}
