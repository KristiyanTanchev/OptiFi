package com.optifi.domain.meta.api;

import com.optifi.config.FeatureProperties;
import com.optifi.domain.meta.api.response.FeatureFlagsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "FeatureFlags")
@SecurityRequirements
@RestController
@RequestMapping("/api/public/features")
@RequiredArgsConstructor
public class FeatureRestController {
    private final FeatureProperties features;

    @Operation(summary = "Get feature flags")
    @ApiResponse(responseCode = "200", description = "Feature flags returned")
    @GetMapping
    public ResponseEntity<FeatureFlagsResponseDto> getFeatures() {
        FeatureFlagsResponseDto response = FeatureFlagsResponseDto.builder()
                .registrationEnabled(features.registrationEnabled())
                .createCategoryEnabled(features.allowUserCategories())
                .build();
        return ResponseEntity.ok(response);
    }
}
