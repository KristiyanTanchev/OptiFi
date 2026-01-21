package com.optifi.domain.reporting.api;

import com.optifi.domain.reporting.api.request.ReportSummaryRequestDto;
import com.optifi.domain.reporting.api.response.ReportSummaryResponseDto;
import com.optifi.domain.reporting.application.ReportService;
import com.optifi.domain.reporting.application.command.ReportSummaryCommand;
import com.optifi.domain.reporting.application.result.ReportSummaryResult;
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
}
