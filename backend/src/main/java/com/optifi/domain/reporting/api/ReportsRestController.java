package com.optifi.domain.reporting.api;

import com.optifi.config.web.CurrentUser;
import com.optifi.domain.reporting.api.mapper.ReportingMapper;
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
import com.optifi.domain.shared.UserContext;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final ReportingMapper mapper;

    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryResponseDto> getSummaryReport(
            @Valid @ModelAttribute ReportSummaryRequestDto requestDto,
            @CurrentUser UserContext ctx
    ) {
        ReportSummaryCommand cmd = mapper.toSummaryCommand(requestDto, ctx);
        ReportSummaryResult result = reportService.getReportSummary(cmd);
        ReportSummaryResponseDto response = mapper.toSummaryResponseDto(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ReportCategoriesResponseDto> getCategoriesReport(
            @Valid @ModelAttribute ReportCategoriesRequestDto requestDto,
            @CurrentUser UserContext ctx
    ) {
        ReportCategoriesCommand cmd = mapper.toCategoriesCommand(requestDto, ctx);
        ReportCategoriesResult result = reportService.getReportCategories(cmd);
        ReportCategoriesResponseDto response = mapper.toCategoriesResponseDto(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time-chart")
    public ResponseEntity<ReportTimeChartResponseDto> getTimeChartReport(
            @Valid @ModelAttribute ReportTimeChartRequestDto requestDto,
            @CurrentUser UserContext ctx
    ) {
        ReportTimeChartCommand cmd = mapper.toTimeChartCommand(requestDto, ctx);
        ReportTimeChartResult result = reportService.getReportTimeChart(cmd);
        ReportTimeChartResponseDto response = mapper.toTimeChartResponseDto(result);
        return ResponseEntity.ok(response);
    }
}
