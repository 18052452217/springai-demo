package com.example.demo.ai.template;

public class SqlTemplate {

    public static final String REPORT_PROMPT = """
                
                ### 角色
                你是一个数据分析专家，精通JSON解析和语义理解，擅长数据分析和统计
                
                ### 任务描述
                你是任务是根据用户输入和数据样本，分析数据，生成统计数据
                
                ### 规范要求
                1. 只返回最终的统计数据，不要返回任何其他内容。
                2. 使用JSON格式返回，不要返回任何其他内容。
                
                ### 示例
                {
                    "new_course_count": 10,
                    "new_course_name": "课程名称"
                }
            """;

    public static final String SQL_PROMPT = """
                
                ### 角色
                你是一个精通SQL语言的数据库专家，精通mysql使用，同时擅长解读和分析数据
                
                ### 任务描述
                你是任务是根据用户输入和上下文内容，生成SQL语句，并按照约定格式输出
                
                ### 规范要求
                1. 请使用 MySQL 语法，关键字和表名、字段名之间需用空格分隔
                2. 请使用驼峰命名法给计算列命名。
                3. 请使用反引号包裹字段名、别名、表名。
                4. 遇到 MySQL 关键字，请使用大写字母，并且前后用" "分隔
                5. 只返回SQL语句，不要返回任何其他内容。
                6. 请确保查询的字段和表名一定在提供的上下文中存在。
                7. 如果表中有deleted字段，查询时请加上deleted=0的条件并作为第一个条件
              
                ### 示例
                输入：
                我想查询公司名称和课程数量
                输出：
                SELECT\s
                    `company_name`,
                    COUNT(*) AS `new_course_count`\s
                FROM\s
                    `course_info` \s
                WHERE\s
                    `course_status` = 1\s
                GROUP BY\s
                    `company_name`\s
                ORDER BY\s
                    `new_course_count`\s
                DESC\s
                LIMIT `10`\s
            """;

    public static final String SQL_DDL = """
        DROP TABLE IF EXISTS `chapter_learn_record` ;
        
        CREATE TABLE `chapter_learn_record` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `learn_id` varchar(64) NOT NULL COMMENT '学习id',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `chapter_id` varchar(64) NOT NULL COMMENT '章节id',
          `chapter_name` varchar(500) NOT NULL COMMENT '章节名称',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
          `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `scene_publish_date` datetime DEFAULT NULL COMMENT '场景发布时间',
          `finish_rate` decimal(5,2) DEFAULT '0.00' COMMENT '完成率',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_learnid` (`learn_id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_startTime` (`start_time`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=10909 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='章节学习记录表';
        
        DROP TABLE IF EXISTS `data_chapter_day` ;
        
        CREATE TABLE `data_chapter_day` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `lesson_no` varchar(64) NOT NULL COMMENT '课程no',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `stat_date` varchar(20) NOT NULL COMMENT '统计日期',
          `chapter_id` varchar(64) NOT NULL COMMENT '章节ID',
          `chapter_name` varchar(500) NOT NULL COMMENT '章节名称',
          `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '总次数',
          `total_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '总时长，单位ms',
          `max_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '最高用时，单位ms',
          `min_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '最短用时，单位ms',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          UNIQUE KEY `idx_chapter_date` (`chapter_id`,`stat_date`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_statdate` (`stat_date`)
        ) ENGINE=InnoDB AUTO_INCREMENT=597 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='章节学习每日统计表';
        
        DROP TABLE IF EXISTS `data_delay_task` ;
        
        CREATE TABLE `data_delay_task` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `point_message` text COMMENT '延迟上报消息',
          `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
          `process` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已处理 0:未处理 1:已处理',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=977 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='数据延迟上报任务表';
        
        DROP TABLE IF EXISTS `data_learn_day` ;
        
        CREATE TABLE `data_learn_day` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `stat_date` varchar(20) NOT NULL COMMENT '统计日期',
          `uid_count` int(11) NOT NULL DEFAULT '0' COMMENT '人数',
          `learn_count` int(11) NOT NULL DEFAULT '0' COMMENT '次数',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_statdate` (`stat_date`)
        ) ENGINE=InnoDB AUTO_INCREMENT=116 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='学习情况每日统计表';
        
        DROP TABLE IF EXISTS `data_learn_hour` ;
        
        CREATE TABLE `data_learn_hour` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `stat_date` varchar(20) NOT NULL COMMENT '统计日期',
          `week` int(11) NOT NULL COMMENT '星期',
          `hour` int(11) NOT NULL COMMENT '统计小时',
          `uid_count` int(11) NOT NULL DEFAULT '0' COMMENT '人数',
          `learn_count` int(11) NOT NULL DEFAULT '0' COMMENT '次数',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_statdate_hour` (`stat_date`,`hour`)
        ) ENGINE=InnoDB AUTO_INCREMENT=344 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='学习情况每小时统计表';
        
        DROP TABLE IF EXISTS `data_learn_month` ;
        
        CREATE TABLE `data_learn_month` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `stat_date` varchar(20) NOT NULL COMMENT '统计日期',
          `uid_count` int(11) NOT NULL DEFAULT '0' COMMENT '人数',
          `learn_count` int(11) NOT NULL DEFAULT '0' COMMENT '次数',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_statdate` (`stat_date`)
        ) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='学习情况每月统计表';
        
        DROP TABLE IF EXISTS `data_lesson_day` ;
        
        CREATE TABLE `data_lesson_day` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `lesson_no` varchar(64) NOT NULL COMMENT '课程no',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `stat_date` varchar(20) NOT NULL COMMENT '统计日期',
          `total_count` int(11) NOT NULL DEFAULT '0' COMMENT '总次数',
          `total_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '总时长，单位ms',
          `max_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '最高用时，单位ms',
          `min_duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '最短用时，单位ms',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_statdate` (`stat_date`),
          KEY `idx_lesson_no` (`lesson_no`)
        ) ENGINE=InnoDB AUTO_INCREMENT=438 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件学习每日统计表';
        
        DROP TABLE IF EXISTS `lesson_exam` ;
        // 考试信息表，记录考试结果
        CREATE TABLE `lesson_exam` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `exam_id` varchar(64) NOT NULL COMMENT '考试id',
          `scene_version_id` varchar(64) NOT NULL COMMENT '场景内容id',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `result` text COMMENT '考试结果',
          `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
          `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_examid` (`exam_id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=956 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件考试表';
        
        DROP TABLE IF EXISTS `lesson_exam_record` ;
        // 考试记录表，记录考试每个考题的结果
        CREATE TABLE `lesson_exam_record` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `exam_id` varchar(64) NOT NULL COMMENT '考试id',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `result` text COMMENT '考试结果',
          `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
          `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_examid` (`exam_id`),
          KEY `idx_uid` (`uid`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=2515 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件记录表';
        
        DROP TABLE IF EXISTS `lesson_info` ;
        
        CREATE TABLE `lesson_info` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `scene_id` varchar(64) NOT NULL COMMENT '场景ID',
          `company_id` varchar(32) NOT NULL COMMENT '组织id',
          `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
          `description` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
          `type` varchar(32) NOT NULL COMMENT '类型',
          `state` varchar(32) NOT NULL COMMENT '状态',
          `cover` text COMMENT '封面图片',
          `anchor_type` varchar(32) DEFAULT NULL COMMENT '定位类型',
          `map_id` varchar(640) DEFAULT NULL COMMENT '地图id',
          `anchor_url` text COMMENT '锚定图',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `scene_publish_date` datetime DEFAULT NULL COMMENT '场景发布时间',
          `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
          `modified_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          `outline_url` varchar(500) NOT NULL DEFAULT '' COMMENT '轮廓图',
          `guide_audio` varchar(500) NOT NULL DEFAULT '' COMMENT '语音',
          `guide_model` varchar(20) DEFAULT 'anchor' COMMENT '引导模式：outline 轮廓图，anchor 锚定图',
          `is_exam` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否考核 0:不支持 1:支持',
          PRIMARY KEY (`id`),
          KEY `idx_appid_companyid` (`company_id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_sceneid` (`scene_id`)
        ) ENGINE=InnoDB AUTO_INCREMENT=1438 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件信息表';
        
        DROP TABLE IF EXISTS `lesson_learn_record` ;
        
        CREATE TABLE `lesson_learn_record` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `learn_id` varchar(64) NOT NULL COMMENT '学习id',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
          `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `scene_publish_date` datetime DEFAULT NULL COMMENT '场景发布时间',
          `finish_rate` decimal(5,2) DEFAULT '0.00' COMMENT '完成率',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_learnid` (`learn_id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_startTime` (`start_time`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=1432 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件学习记录表';
        
        DROP TABLE IF EXISTS `lesson_learn_result` ;
        
        CREATE TABLE `lesson_learn_result` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `learn_count` int(11) NOT NULL DEFAULT '0' COMMENT '次数',
          `duration` bigint(20) NOT NULL DEFAULT '0' COMMENT '时长，单位ms',
          `scene_publish_date` datetime DEFAULT NULL COMMENT '场景发布时间',
          `finish_rate` decimal(5,2) DEFAULT '0.00' COMMENT '完成率',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='学员课件学习结果表';
        
        DROP TABLE IF EXISTS `lesson_user_rel` ;
        
        CREATE TABLE `lesson_user_rel` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=330 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件人员表';
        
        DROP TABLE IF EXISTS `room` ;
        
        CREATE TABLE `room` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `room_no` varchar(64) NOT NULL COMMENT '课件编号',
          `room_num` varchar(20) NOT NULL COMMENT '房间号',
          `lesson_no` varchar(64) NOT NULL DEFAULT '' COMMENT '课件编号',
          `uid` varchar(64) NOT NULL DEFAULT '' COMMENT '用户uid',
          `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          `lesson_mode` varchar(20) NOT NULL DEFAULT 'learn' COMMENT '课程模式:learn学习 exam考核 review查错',
          `exam_id` varchar(64) DEFAULT '' COMMENT '考试id',
          PRIMARY KEY (`id`),
          KEY `idx_roomno` (`room_no`),
          KEY `idx_roomnum` (`room_num`),
          KEY `idx_lessonno` (`lesson_no`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=5803 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='房间信息表';
        
        DROP TABLE IF EXISTS `subject_info` ;
        
        CREATE TABLE `subject_info` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `company_id` varchar(64) NOT NULL COMMENT '组织id',
          `subject_no` varchar(64) NOT NULL COMMENT '课程编号',
          `lector` varchar(64) NOT NULL COMMENT '讲师uid',
          `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
          `description` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
          `cycle_type` varchar(32) NOT NULL DEFAULT 'no' COMMENT '培训周期类型',
          `start_time` varchar(32) DEFAULT NULL COMMENT '开始时间',
          `end_time` varchar(32) DEFAULT NULL COMMENT '结束时间',
          `place` varchar(200) NOT NULL DEFAULT '' COMMENT '地点',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
          `modified_by` varchar(64) DEFAULT NULL COMMENT '更新人',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          `cover_url` varchar(500) NOT NULL DEFAULT '' COMMENT '封面图',
          PRIMARY KEY (`id`),
          KEY `idx_companyid` (`company_id`),
          KEY `idx_subjectno` (`subject_no`),
          KEY `idx_starttime_endtime` (`start_time`,`end_time`)
        ) ENGINE=InnoDB AUTO_INCREMENT=297 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课程信息表';
        
        DROP TABLE IF EXISTS `subject_lesson_rel` ;
        
        CREATE TABLE `subject_lesson_rel` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `subject_no` varchar(64) NOT NULL COMMENT '课程编号',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_subjectno` (`subject_no`),
          KEY `idx_lessonno` (`lesson_no`)
        ) ENGINE=InnoDB AUTO_INCREMENT=491 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课程课件关系表';
        
        DROP TABLE IF EXISTS `subject_task` ;
        
        CREATE TABLE `subject_task` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `task_no` varchar(64) NOT NULL COMMENT '任务编号',
          `task_name` varchar(258) NOT NULL COMMENT '任务名称',
          `subject_no` varchar(64) NOT NULL COMMENT '课程编号',
          `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `task_status` varchar(32) NOT NULL DEFAULT '' COMMENT '任务状态：doing 进行中, finish 已完成',
          `start_date` varchar(32) DEFAULT NULL COMMENT '开始时间',
          `end_date` varchar(32) DEFAULT NULL COMMENT '结束时间',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_taskno` (`task_no`),
          KEY `idx_uid` (`uid`),
          KEY `idx_startdate_enddate` (`start_date`,`end_date`)
        ) ENGINE=InnoDB AUTO_INCREMENT=2595 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课程任务表';
        
        DROP TABLE IF EXISTS `subject_user_rel` ;
        
        CREATE TABLE `subject_user_rel` (
          `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
          `subject_no` varchar(64) NOT NULL COMMENT '课程编号',
          `uid` varchar(64) NOT NULL COMMENT '人员id',
          `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
          `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
          `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
          PRIMARY KEY (`id`),
          KEY `idx_subjectno` (`subject_no`),
          KEY `idx_uid` (`uid`)
        ) ENGINE=InnoDB AUTO_INCREMENT=547 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课程人员表';
    """;


    public static final String SQL_EXAM_DDL = """
                
                DROP TABLE IF EXISTS `lesson_exam` ;
                // 考试信息表，记录考试结果
                CREATE TABLE `lesson_exam` (
                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `exam_id` varchar(64) NOT NULL COMMENT '考试id',
                  `scene_version_id` varchar(64) NOT NULL COMMENT '场景内容id',
                  `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
                  `company_id` varchar(64) NOT NULL COMMENT '组织id',
                  `result` text COMMENT '考试结果',
                  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
                  `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
                  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
                  PRIMARY KEY (`id`),
                  KEY `idx_examid` (`exam_id`),
                  KEY `idx_lessonno` (`lesson_no`),
                  KEY `idx_companyid` (`company_id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=956 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件考试表';
                ##lesson_exam表result考试结果结构如下
                {
                	"chapterList": [{
                		"chapterId": "UUID", //章节id
                		"checkList": [{
                			"checkId": "UUID",  //考点(考题)id
                			"status": "N",  //S成功，F失败，N未完成(默认)
                			"type": "panel.ready"   //panel.ready 表示面板类型
                		}, {
                			"checkId": "c4dd0a6c-3a29-4355-8ade-1bb0d91df6df",
                			"status": "N",
                			"type": "touch"  //touch 表示动作类型
                		}],
                		"checkStatus": "N",  //章节考试状态 S成功，F失败，N未完成(默认)
                		"title": "XXXX" //章节名称
                	}],
                	"endTime": 1741750461131,
                	"examId": "05318041178c47febbea06c8dfea3b63",
                	"examStatus": "N",  //本场考试结果 S成功，F失败，N未完成(默认)
                	"lessonNo": "5799BEB160F0451C8C3B112C75BCCB75",
                	"startTime": 1741750461131
                }
                
                
                DROP TABLE IF EXISTS `lesson_exam_record` ;
                // 考试记录表，记录考试每个考题的结果
                CREATE TABLE `lesson_exam_record` (
                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `exam_id` varchar(64) NOT NULL COMMENT '考试id', //lesson_exam.exam_id
                  `uid` varchar(64) NOT NULL COMMENT '人员id',
                  `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
                  `company_id` varchar(64) NOT NULL COMMENT '组织id',
                  `result` text COMMENT '考试结果',  //和lesson_exam结果一样
                  `start_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '开始时间',
                  `end_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '结束时间',
                  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
                  PRIMARY KEY (`id`),
                  KEY `idx_examid` (`exam_id`),
                  KEY `idx_uid` (`uid`),
                  KEY `idx_lessonno` (`lesson_no`),
                  KEY `idx_companyid` (`company_id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=2515 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件记录表';
                ##lesson_exam_record表result考试结果结构如下
                {
                  "examId":"UUID", //考试id
                  "lessonNo":"课程编号",
                  "chapterId":"chapterId",  //章节id
                  "checkList":[  //考点列表
                    {
                      "checkId":"XXX",  //考点(考题)id
                      "status":"F"  //S成功，F失败，N未完成(默认)
                    },
                    {
                      "checkId":"XXXX",
                      "status":"N"
                    },
                    {
                      "checkId":"XXX",
                      "status":"S"
                    }
                  ],
                  "startTime":12321131, //开始时间戳
                  "endTime":12321131 //结果时间戳
                }
                
                DROP TABLE IF EXISTS `lesson_info` ;
                // 课程信息表
                CREATE TABLE `lesson_info` (
                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `lesson_no` varchar(64) NOT NULL COMMENT '课件编号',
                  `scene_id` varchar(64) NOT NULL COMMENT '场景ID',
                  `company_id` varchar(32) NOT NULL COMMENT '组织id',
                  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
                  `description` varchar(500) NOT NULL DEFAULT '' COMMENT '描述',
                  `type` varchar(32) NOT NULL COMMENT '类型',
                  `state` varchar(32) NOT NULL COMMENT '状态',
                  `cover` text COMMENT '封面图片',
                  `anchor_type` varchar(32) DEFAULT NULL COMMENT '定位类型',
                  `map_id` varchar(640) DEFAULT NULL COMMENT '地图id',
                  `anchor_url` text COMMENT '锚定图',
                  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                  `scene_publish_date` datetime DEFAULT NULL COMMENT '场景发布时间',
                  `create_by` varchar(64) NOT NULL DEFAULT '' COMMENT '创建人',
                  `modified_by` varchar(64) NOT NULL DEFAULT '' COMMENT '更新人',
                  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
                  `outline_url` varchar(500) NOT NULL DEFAULT '' COMMENT '轮廓图',
                  `guide_audio` varchar(500) NOT NULL DEFAULT '' COMMENT '语音',
                  `guide_model` varchar(20) DEFAULT 'anchor' COMMENT '引导模式：outline 轮廓图，anchor 锚定图',
                  `is_exam` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否考核 0:不支持 1:支持',
                  PRIMARY KEY (`id`),
                  KEY `idx_appid_companyid` (`company_id`),
                  KEY `idx_lessonno` (`lesson_no`),
                  KEY `idx_sceneid` (`scene_id`)
                ) ENGINE=InnoDB AUTO_INCREMENT=1438 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='课件信息表';
                
                DROP TABLE IF EXISTS `room` ;
                // AR考场房间
                CREATE TABLE `room` (
                  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `room_no` varchar(64) NOT NULL COMMENT '课件编号',
                  `room_num` varchar(20) NOT NULL COMMENT '房间号',
                  `lesson_no` varchar(64) NOT NULL DEFAULT '' COMMENT '课件编号',
                  `uid` varchar(64) NOT NULL DEFAULT '' COMMENT '用户uid',
                  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '名称',
                  `gmt_created` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除 0:未删除 1:已删除',
                  `lesson_mode` varchar(20) NOT NULL DEFAULT 'learn' COMMENT '课程模式:learn学习 exam考核 review查错',
                  `exam_id` varchar(64) DEFAULT '' COMMENT '考试id',
                  PRIMARY KEY (`id`),
                  KEY `idx_roomno` (`room_no`),
                  KEY `idx_roomnum` (`room_num`),
                  KEY `idx_lessonno` (`lesson_no`),
                  KEY `idx_uid` (`uid`)
                ) ENGINE=InnoDB AUTO_INCREMENT=5803 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='房间信息表';
                
            """;
}
