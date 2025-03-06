package com.example.demo.deepceek;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 基础埋点数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiParam {

    private String message;

    private List<String> messages;
}
