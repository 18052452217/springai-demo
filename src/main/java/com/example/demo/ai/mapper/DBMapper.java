package com.example.demo.ai.mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DBMapper extends BaseMapper {
    List<Map<String, Object>> executeDynamicSql(String sql);
}
