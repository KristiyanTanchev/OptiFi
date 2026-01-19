package com.optifi.domain.meta.api;

import com.optifi.config.FeatureProperties;
import com.optifi.domain.meta.api.response.FeatureFlagsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/features")
@RequiredArgsConstructor
public class FeatureRestController {
    private final FeatureProperties features;

    @GetMapping
    public ResponseEntity<FeatureFlagsResponseDto> getFeatures() {
        FeatureFlagsResponseDto response = new FeatureFlagsResponseDto(features.registrationEnabled());
        return ResponseEntity.ok(response);
    }
}
