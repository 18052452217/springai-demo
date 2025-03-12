package com.example.demo.ai.service;

import ar.training.ai.mapper.DBMapper;
import ar.training.ai.template.SqlTemplate;
import ar.training.common.enums.ErrorCodeEnums;
import ar.training.common.exception.RokidException;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static ar.training.ai.template.SqlTemplate.REPORT_PROMPT;

@Service
@Slf4j
public class DataReportAiService {

    @Resource
    private LLMService llmService;

    @Resource
    private EmbeddingService embeddingService;

    @Resource
    private DBMapper dbMapper;

    /**
     * 生成报表
     * @param prompt
     * @param message
     * @return
     */
    public String generateReport(String prompt, String message) {
        String sqlStr = generateSql(prompt, message);
        log.info("generateReport sql:{}", sqlStr);
        String result = executeSql(sqlStr);
        log.info("generateReport sql result:{}", result);
        String data = llmService.llaChat(REPORT_PROMPT + "\n\n####" + SqlTemplate.SQL_EXAM_DDL,
                "####数据样本" + result +
                        "####用户输入" + message );
        log.info("generateReport data:{}", data);
        return data;
    }

    /**
     *
     * @param prompt
     * @param message
     * @return
     */
    public String generateSql(String prompt, String message) {
        // 获取向量库表DDL
//        String embeddingText = embeddingService.embedQuery(message);
        // 生成sql
        String sql = llmService.llaChat(SqlTemplate.SQL_PROMPT + "\n\n####" + SqlTemplate.SQL_EXAM_DDL, prompt);
        sql = sql.split("###")[0];
        sql = sql.replace("LIMIT", " LIMIT")
                .replace("ORDER", " ORDER");
        return sql;
    }

    /**
     * 生成动态sql
     * @param sql
     * @return
     */
    public String executeSql(String sql) {
        if (StringUtils.isEmpty(sql) || !sql.toLowerCase().startsWith("select")) {
            log.info("sql不符合格式:{}", sql);
            throw new RokidException("sql不符合格式", ErrorCodeEnums.SYSTEM_ERROR.getCode());
        }
        List<Map<String, Object>> result = dbMapper.executeDynamicSql(sql);
        return JSON.toJSONString(result);
    }
}
