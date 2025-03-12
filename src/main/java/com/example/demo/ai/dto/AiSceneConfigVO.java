package com.example.demo.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础埋点数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiSceneConfigVO {

    private String id;

    private String protocol = "v1.3.0";

    private Assets assets = new Assets();

    private List<Object> chapters = new ArrayList<>();

    @Data
    public static class Assets {
        private List<TtsAudio> ttsAudios = new ArrayList<>();
    }

    @Data
    public static class TtsAudio {
        private String id;
        private String text;
        private String audio;
        private Integer duration;
    }
}
