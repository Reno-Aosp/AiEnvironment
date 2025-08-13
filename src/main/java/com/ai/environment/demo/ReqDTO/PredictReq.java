package com.ai.environment.demo.ReqDTO;

// src/main/java/com/ai/environment/demo/dto/PredictRequest.java

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictReq {
    @NotBlank(message = "text is required")
    @Size(max = 4096, message = "text is too long")
    private String text;
}
