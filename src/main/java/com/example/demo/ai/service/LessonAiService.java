package com.example.demo.ai.service;

import ar.training.ai.dto.AiSceneConfigVO;
import ar.training.ai.template.SceneTemplate;
import ar.training.common.utils.UUIDUtils;
import ar.training.lesson.dto.req.AddLessonParam;
import ar.training.lesson.dto.req.DownloadLessonTtsParam;
import ar.training.lesson.dto.res.LessonInfoVO;
import ar.training.lesson.dto.res.LessonTtsVO;
import ar.training.lesson.entity.LessonSceneConfigEntityV3;
import ar.training.lesson.enums.AnchorTypeEnum;
import ar.training.lesson.enums.LessonTypeEnum;
import ar.training.lesson.service.LessonSceneConfigService;
import ar.training.lesson.service.LessonService;
import ar.training.remoteApi.SceneRemoteService;
import ar.training.remoteApi.feign.dto.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ForkJoinPool;

@Service
@Slf4j
public class LessonAiService {

    @Resource
    private LessonService lessonService;

    @Resource
    private LessonSceneConfigService lessonSceneConfigService;

    @Resource
    private LLMService llaservice;

    @Resource
    private SceneRemoteService sceneRemoteService;

    public String createSceneJson(String message) {
        try {
            String llaResult = llaservice.llaChat(SceneTemplate.SCENE_JSON_PROMPT + SceneTemplate.SCENE_LEARN_TEMPLATE, message);
            // TTS 音频下载
            AiSceneConfigVO aiSceneConfigVO = JSONObject.parseObject(llaResult, AiSceneConfigVO.class);
            if (Objects.nonNull(aiSceneConfigVO.getAssets()) && CollectionUtils.isNotEmpty(aiSceneConfigVO.getAssets().getTtsAudios())) {
                aiSceneConfigVO.getAssets().getTtsAudios().forEach(tts -> {
                    DownloadLessonTtsParam downloadLessonTtsParam = new DownloadLessonTtsParam();
                    downloadLessonTtsParam.setLessonNo("media");
                    downloadLessonTtsParam.setText(tts.getText());
                    LessonTtsVO lessonTtsVO = lessonService.downloadTts(downloadLessonTtsParam);
                    if (Objects.nonNull(lessonTtsVO)) {
                        tts.setAudio(lessonTtsVO.getFilePackage());
                        tts.setDuration(0);
                    }
                });
            }
            String resultJson = JSON.toJSONString(aiSceneConfigVO);
            log.info("createSceneJson resultJson:{}", resultJson);
            return resultJson;
        } catch (Exception e) {
            log.warn("createSceneJson failed", e);
        }
        return "";
    }

    public String createSceneTitle(String message) {
        try {
            String resultJson = llaservice.llaChat(SceneTemplate.SCENE_TITLE_PROMPT, message);
            log.info("createSceneTitle resultJson:{}", resultJson);
            return resultJson;
        } catch (Exception e) {
            log.warn("createSceneTitle failed", e);
        }
        return "";
    }

    public List<String> splitMessage(String message) {
        List<String> messageChunks = new ArrayList<>();
        // 以 ### 作为章节分割的标志
        String[] sections = message.split("### ");
        for (String section : sections) {
            if (!section.isEmpty()) {
                // 确保每个章节块以 ### 开头
                messageChunks.add("### " + section);
            }
        }
        return messageChunks;
    }

    // 调用示例
    public LessonSceneConfigEntityV3 createSceneJsonWithSplit(String title, String message) {
        // 初始化
        LessonSceneConfigEntityV3 lessonSceneConfigEntityV3 = initSceneConfig();

        // 拆分章节
        List<String> chunks = splitMessage(message);
        // 假设这里设置线程数为 10，你可以根据实际情况调整
        ForkJoinPool customThreadPool = new ForkJoinPool(20);
        customThreadPool.submit(() ->
                chunks.stream().parallel().forEach(chunk -> {
                    // 大模型处理
                    String sceneJson = createSceneJson(chunk);
                    // 合并结果
                    LessonSceneConfigEntityV3 aiSceneConfigVO = JSONObject.parseObject(sceneJson, LessonSceneConfigEntityV3.class);
                    LessonSceneConfigEntityV3.Asset assets = aiSceneConfigVO.getAssets();
                    if (Objects.nonNull(assets) && CollectionUtils.isNotEmpty(assets.getTtsAudios())) {
                        lessonSceneConfigEntityV3.getAssets().getTtsAudios().addAll(assets.getTtsAudios());
                    }
                    if (CollectionUtils.isNotEmpty(aiSceneConfigVO.getChapters())) {
                        lessonSceneConfigEntityV3.getChapters().addAll(aiSceneConfigVO.getChapters());
                    }
                })
        ).join();
        customThreadPool.shutdown();

        // 生成课件
        String lessonNo = createLessonInfo(title, message);

        // 保存配置
        lessonSceneConfigEntityV3.setLessonNo(lessonNo);
        lessonSceneConfigService.saveV3(lessonSceneConfigEntityV3);

        // 获取场景原始数据
        LessonInfoVO lessonInfoVO = lessonService.detail(lessonNo);
        if (Objects.nonNull(lessonInfoVO)) {
            XSceneVersionV2 xSceneVersionV2 = sceneRemoteService.getSceneContentV2(lessonInfoVO.getSceneId(), "draft");
            if (Objects.nonNull(xSceneVersionV2)) {
                // 更新课件版本号
                this.initTrainingEntity(xSceneVersionV2, lessonNo);
                sceneRemoteService.updateScene(xSceneVersionV2);
            }
        }

        return lessonSceneConfigEntityV3;
    }

    private LessonSceneConfigEntityV3 initSceneConfig() {
        LessonSceneConfigEntityV3 lessonSceneConfigEntityV3 = new LessonSceneConfigEntityV3();
        lessonSceneConfigEntityV3.setId(UUIDUtils.randomLowCase());
        LessonSceneConfigEntityV3.Asset asset = new LessonSceneConfigEntityV3.Asset();
        List<LessonSceneConfigEntityV3.Asset.TtsAudio> ttsAudios = new ArrayList<>();
        asset.setTtsAudios(ttsAudios);
        lessonSceneConfigEntityV3.setAssets(asset);
        lessonSceneConfigEntityV3.setChapters(new ArrayList<>());
        lessonSceneConfigEntityV3.setVersionCode("1.0.0");
        lessonSceneConfigEntityV3.setProtocol("1.3.0");
        lessonSceneConfigEntityV3.setLessonType(LessonTypeEnum.KNOWLEDGE.toString());
        return lessonSceneConfigEntityV3;
    }

    private void initTrainingEntity(XSceneVersionV2 xSceneVersionV2, String lessonNo) {
        // 插件
        XSceneFactory.XSceneFactoryEntity.XSceneFactoryEntityPlugin plugin = new XSceneFactory.XSceneFactoryEntity.XSceneFactoryEntityPlugin();
        plugin.setEntityId(xSceneVersionV2.getScene().getEntities().get(0).getId());
        plugin.setPluginCode("training-lesson");
        plugin.setProperties(new HashMap());
        xSceneVersionV2.getScene().getFactories().getEntities().getPlugins().add(plugin);

        // 实体
        String entityId = UUIDUtils.randomLowCase();
        XSceneEntity xSceneEntity = new XSceneEntity();
        xSceneEntity.setType("entity");
        xSceneEntity.setId(entityId);
        xSceneEntity.setName("training");
        xSceneEntity.setEnabled(true);
        xSceneEntity.setVisible(true);
        xSceneEntity.setParent(null);
        xSceneEntity.setChildren(null);
        xSceneEntity.setComponents(initTrainingComponent(lessonNo));
        xSceneVersionV2.getScene().getEntities().add(xSceneEntity);

        XSceneFactory.XSceneFactoryEntity.XSceneFactoryEntityPlugin plugin2 = new XSceneFactory.XSceneFactoryEntity.XSceneFactoryEntityPlugin();
        plugin2.setEntityId(entityId);
        plugin2.setProperties(new HashMap());
        xSceneVersionV2.getScene().getFactories().getEntities().getPlugins().add(plugin2);

        XSceneSettings settings = xSceneVersionV2.getScene().getSettings();
        settings.getEditor().setMapStatus(false);
        settings.getRender().setSpaceOcclusionStatus(false);
        settings.getRender().setWhiteModelStatus(true);
        settings.getRender().setMultiSyncModeStatus(true);
    }

    private List<XSceneComponent> initTrainingComponent(String lessonNo) {
        List<XSceneComponent> xSceneComponents = new ArrayList<>();

        XSceneComponent xSceneComponent = new XSceneComponent();
        xSceneComponent.setId(UUIDUtils.randomLowCase());
        xSceneComponent.setType("training-lesson");
        xSceneComponent.setLessonNo(lessonNo);
        xSceneComponent.setLessonType(LessonTypeEnum.OPERATION.toString());
        xSceneComponent.setVersionCode("1.0.0");
        xSceneComponents.add(xSceneComponent);

        XSceneComponent transformComponent = new XSceneComponent();
        transformComponent.setId(UUIDUtils.randomLowCase());
        transformComponent.setType("transform");
        transformComponent.setVisible(true);
        // 初始化 position
        List<Double> position = Arrays.asList(0.0, 0.0, 0.0);
        // 初始化 scale
        List<Double> scale = Arrays.asList(1.0, 1.0, 1.0);
        // 初始化 quaternion
        List<Double> quaternion = Arrays.asList(0.0, 0.0, 0.0, -1.0);

        transformComponent.setPosition(position);
        transformComponent.setScale(scale);
        transformComponent.setQuaternion(quaternion);
        xSceneComponents.add(transformComponent);

        return xSceneComponents;
    }

    private String createLessonInfo(String title, String message) {
        // 生成课件
        AddLessonParam addLessonParam = new AddLessonParam();
        if (StringUtils.isEmpty(title)) {
            List<String> chunks = splitMessage(message);
            title = createSceneTitle(chunks.get(0));
        }
        addLessonParam.setName(title);
        addLessonParam.setType(LessonTypeEnum.OPERATION);
        addLessonParam.setAnchorType(AnchorTypeEnum.PLANE);
        addLessonParam.setVersion("1.6.0");
        addLessonParam.setUidList(Collections.singletonList("common"));
        return lessonService.add(addLessonParam);
    }
}
