package com.example.demo.qwen;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.example.demo.config.ModelConfig;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class QwenService {

    public static final String QIAN_WEN_MODEL = "qwen-plus";
    public static final String SYSTEM_MSG = "### 任务描述\n" +
            "你是一名专业考核AI，需要判断学员在章节考核中是否按考核要求完成口述任务。\n" +
            "\n" +
            "### 考核规则\n" +
            "当考核要求中出现：\n" +
            "1.\"口述\" + 指定内容 → \"口述\"不需要说，但口述后的指定内容语义相似度≥40%即可\n" +
            "2.\"请说\" + 指定内容 → \"请说\"不需要说，但请说后的指定内容语义相似度≥30%即可\n" +
            "3.强制提取含有关键词的连续有效片段（如'锁定。喂。锁定'中提取'锁定'*2）\n" +
            "4.允许关键内容被非连续无效文本分隔，只要总出现次数≥1次纯净匹配\n" +
            "5.句式转换允许性：学员将陈述句'无明显出血'转换为疑问句式'有无明显出血'（疑问句↔陈述句）\n" +
            "\n" +
            "### 特定词\n" +
            "“一二翻”很容易ASR转成123，如果考核点是一二翻，但是ASR翻译成123也算成功\n" +
            "\n" +
            "### 示例\n" +
            "要求：\"口述：锁定\"\n" +
            "学员：\"锁定\" → True\n" +
            "学员：\"锁定。锁定\" → True（存在独立匹配项）\n" +
            "学员：\"你好，锁定\" → True（第二个\"完成\"纯净）\n" +
            "学员：\"行行行行。锁定。喂\" → True(自动过滤无效片段)\n" +
            "\n" +
            "### 输出JSON格式\n" +
            "仅输出以下格式的 JSON 数据：{\"result\": \"True/False\", \"analysis\": \"分析内容\"}，无需额外的分析逻辑说明。" +
            "例如：{\"result\":\"XXXX\",\"analysis\":\"XXXX\"}"
            ;

    public static final String CHECK_MSG =
            "### 考核要求\n" +
            "{asr_text}\n" +
            "\n" +
            "### 学员考核环节说的话\n" +
            "{check_text}\n" +
            "\n" ;

    public String chat(String asrText, String checkText) {
        try {
            Generation gen = new Generation();
            Message systemMsg = Message.builder()
                    .role(Role.SYSTEM.getValue())
                    .content(SYSTEM_MSG)
                    .build();
            String userMessage = replacePlaceholders(asrText, checkText);
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMessage)
                    .build();
            GenerationParam param = GenerationParam.builder()
                    // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                    .apiKey(ModelConfig.ALIYUN_API_KEY)
                    .model(QIAN_WEN_MODEL)
                    .messages(Arrays.asList(systemMsg, userMsg))
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();
            GenerationResult result = gen.call(param);
            System.out.println("GenerationResult:" + result);
            String resultStr = result.getOutput().getChoices().get(0).getMessage().getContent();
            String cleanedJson = resultStr.replace("```json", "").replace("```", "").trim();
            System.out.println("cleanedJson:" + cleanedJson);
            return cleanedJson;
        } catch (Exception e) {
            System.err.println("错误信息："+e.getMessage());
            System.out.println("请参考文档：https://help.aliyun.com/zh/model-studio/developer-reference/error-code");
        }
        return "";
    }

    /**
     * 根据传入的参数替换 CHECK_MSG 中的占位符
     * @param asrText 考核要求文本
     * @param checkText 学员考核环节说的话
     * @return 替换后的字符串
     */
    public String replacePlaceholders(String asrText, String checkText) {
        String result = CHECK_MSG.replace("{asr_text}", asrText);
        result = result.replace("{check_text}", checkText);
        return result;
    }
}
