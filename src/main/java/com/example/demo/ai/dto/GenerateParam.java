package com.example.demo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 基础埋点数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateParam {

    private String prompt;

    private String message;
}
