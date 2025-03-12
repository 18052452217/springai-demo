package com.example.demo.ai.template;

public class SceneTemplate {

    public static final String SCENE_TITLE_PROMPT = """
        ### 任务描述
        请根据文档内容生成标题，20字以内，以名词为准
        
        ### 输出格式
        字符串
        
        ### 示例
        "浙一脊柱搬运培训课程"
    """;

    public static final String SCENE_JSON_PROMPT = """
        ### 任务描述
        你是一个专业的JSON配置生成助手，请根据文档内容生成完整的配置结构：根据输入文档长度自动调整输出规模。
        
        ### 考核规则
        基于输入文档和相关文档，生成JSON结构
          输出要求：
          1. 生成完整的panel配置结构
          2. ttsAudios, 只填id和text，其他字段填空, text不要超过300字符
          3. 包含必要的空数组占位
          4. id字段使用uuidv4生成
          5. 输出JSON格式规范，不要包含其他内容, 不需要解释，不要携带文档中的注释，不规范的引号要转义
    """;

    public static final String SCENE_LEARN_TEMPLATE = """ 
                ### 输出JSON格式
                {
                  "id":"", // uuidv4生成随机id
                  "versionCode": "1.0.1", // 配置版本
                  "protocol": "v1.3.0", // 协议版本
                  "assets": {
                    "ttsAudios": [
                      {
                        "id": "",
                        "text": "",
                      }
                    ]
                  },
                  "chapters": [
                    {
                      "id": "",  // 章节id
                      "type": "operation", // 章节类型demonstrate, dismantling, operation
                      "title": "", // 章节名称
                      "elements": [],
                      "events": [
                          {
                            "id": "", // 事件id
                            "type": "panel.ready",
                            "target": "", //目标id,和panels.id保持一致
                            "actions": [
                              {
                                "type": "panel.play",
                                "target": "",  //目标id,和panels.id保持一致
                                "delay": 0,
                                "duration": 0
                              }
                            ],
                            "check": true
                          }
                      ],
                      "guideLines": [], // 章节引导线
                      "sources": [], // 章节资源
                      "check": true, // 是否考核点, 默认为true
                      "panels": [
                        {
                          "id": "", // 面板id
                          "title": "", // 面板标题
                          "description": "", // 面板文案
                          "sources": [], // 面板资源
                          "transform": { // 面板位置
                            "position": [0, 0, 0],
                            "scale": [0, 0, 0],
                            "quaternion": [0, 0, 0, 0]
                          },
                          "visible": true, // 面板可见性
                          "guideTts": "ttsId" // 面板语音引导，对应assets.ttsAudio.id
                        }
                      ],
                      "panelTransform": {
                                "position": [
                                  0,
                                  0,
                                  0
                                ],
                                "scale": [
                                  1,
                                  1,
                                  1
                                ],
                                "quaternion": [
                                  0,
                                  0,
                                  0,
                                  1
                                ]
                      }
                    }
                  ]
                }
            """;

    public static final String SCENE_EXAM_TEMPLATE = """
                 ### 输出JSON格式
                 {
                         	"id": "", // 配置id
                         	"lessonNo": "", // 课件id
                         	"protocol": "v1.3.0", // 协议版本
                         	"versionCode": "", // 配置版本
                         	"assets": {
                         		"entities": ["entityId"],
                         		"nodes": [{
                         			"id": "nodeUUID",
                         			"type": "audio", // normal screen audio
                         			"nodeId": "",
                         			"nodeName": "",
                         			"entityId": ""
                         		}],
                         		"voices": [{
                         			"id": "",
                         			"lang": "zh-CN", // 默认是中文
                         			"label": "下一步",
                         			"pinyin": "xia yi bu"
                         		}],
                         		"ttsAudios": [{
                         			"id": "",
                         			"text": "",
                         			"audio": "",
                         			"duration": ""
                         		}],
                         		"images": [{
                         			"id": "",
                         			"src": "",
                         			"previewUrl": ""
                         		}],
                         		"videos": [{
                         			"id": "",
                         			"src": "",
                         			"previewUrl": "",
                         			"duration": ""
                         		}],
                         		"audios": [{
                         			"id": "",
                         			"src": "",
                         			"duration": ""
                         		}]
                         	},
                         	"chapters": [{
                         		"id": "", // 章节id
                         		"check": true, // 是否考核点, 默认为true
                         		"type": "operation", // 章节类型demonstrate, dismantling, operation
                         		"title": "", // 章节名称
                         		"coverUrl": "", // 章节封面
                         		"description": "", // 章节描述
                         		"switchChapterTts": "ttsId" // 章节切换语音
                         		"panels": [{
                         			"id": "", // 面板id
                         			"title": "", // 面板标题
                         			"description": "", // 面板文案
                         			"sources": [ // 面板资源
                         				{
                         					"assetId": "",
                         					"type": "image/video"
                         				}
                         			],
                         			"transform": { // 面板位置
                         				"position": [0, 0, 0],
                         				"scale": [0, 0, 0],
                         				"quaternion": [0, 0, 0, 0]
                         			},
                         			"visible": true, // 面板可见性
                         			"guideTts": "ttsId" // 面板语音引导
                         		}],
                         		"elements": [ // 场景元素
                         			{
                         				"nodeId": "nodeUUID",
                         				"type": "拆|装", // attach, detach
                         				"global": true,
                         				"targetNodeId": "", // 打标签的node id
                         				"tag": {
                         					"id": "tagUUID",
                         					"label": "",
                         					"type": "image", // image video 可为空字符串，表示没有选媒资
                         					"assetId": "",
                         					"guideTts: "
                         					"
                         				},
                         				"transform": {
                         					"position": [0, 0, 0],
                         					"scale": [1, 1, 1],
                         					"quaternion": [0, 0, 0, -1]
                         				},
                         				"animations": [{
                         						"id": "animationUUID",
                         						"type": "rotation",
                         						"axis": "X|Y|Z",
                         						"angle": 90,
                         						"loop": -1,
                         						"duration": 1000,
                         						"clockwise": true
                         					},
                         					{
                         						"id": "uuid",
                         						"type": "transform",
                         						"target": {
                         							"position": [0, 0, 0],
                         							"quaternion": [0, 0, 0, -1],
                         							"scale": [1, 1, 1]
                         						},
                         						"loop": "-1",
                         						"duration": 1000,
                         						"reverse": true
                         					}
                         				],
                         				"guideTts": "", // asset.ttsAudio.id 已经不能配置，永久为空
                         				"completeAudio": "", // asset.audio.id
                         				"draggable": true,
                         				"blinkable": true, // 闪烁，老字段保留
                         				"emissive": true, // 自发光，老字段保留
                         				"outline": true, // 外边框，老字段保留
                         				"render": {
                         					"material": {
                         						"open": true,
                         						"color": [255, 255, 255],
                         						"opacity": 0.5
                         					},
                         					"emissive": {
                         						"open": true,
                         						"color": [0, 0, 0],
                         						"intensity": 1
                         					},
                         					"outline": {
                         						"open": true,
                         						"color": [0, 0, 0],
                         						"opacity": 0.5
                         					}
                         				},
                         				"sources": [ // 多媒体屏幕/音频 的资源
                         					{
                         						"type": "tts",
                         						"assetId": ""
                         					}
                         				],
                         				"playMode": {
                         					"loop": 1, // 播放次数 -1 表示循环播放
                         					"muted": true // 视频是否静音播放
                         				},
                         				// "videoLoop": 1, // 视频播放次数 -1 表示循环播放
                         				// "videoMuted": true, // 视频是否静音播放
                         				"geometry": {
                         					"width": 1.0, // 米
                         					"height": 0.8, // 米
                         					"angle": 90.0 // 曲面屏角度，为 0 则表示平面
                         				}
                         			}
                         		],
                         		"guideLines": [ // 引导线
                         			{
                         				"path": ["#panel", "nodeId"]
                         			},
                         			{
                         				"path": ["nodeId", "nodeId"]
                         			}
                         		],
                         		"events": [ // 事件
                         			{
                         				"id": "",
                         				"type": "panel.ready",
                         				"check": false,
                         				"target": "panelId",
                         				"actions": [{
                         					"type": "panel.play",
                         					"duration": 0,
                         					"target": "panelId",
                         					"delay": 0,
                         				}]
                         			}
                         		],
                         		"mapDetail": {
                         			"outlineModelUrl": "", // 客户端地图,
                         			"glbModelUrl": "",
                         			"mapId": "", // 地图id
                         			"groupId": "", // 地图分组id
                         			"anchorUrl": "", // 定位引导图
                         			"anchorTts": "ttsId", // 定位引导语音，
                         			"mappingType": 'pano|hd|xgrids', // 建图类型
                         			"outlineUrl": '', // 轮廓图
                         			"lccMapUrl": '' // 雷达地图
                         		}
                         	}]
                         }   
            """;
}
