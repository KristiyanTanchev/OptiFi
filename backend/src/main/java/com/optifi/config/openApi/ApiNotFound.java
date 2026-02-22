package com.optifi.config.openApi;

import com.optifi.exceptions.ApiError;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@ApiResponse(
        responseCode = "404",
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiError.class))
)
public @interface ApiNotFound {
    String description() default "Not found";

    Content content() default @Content;
}
