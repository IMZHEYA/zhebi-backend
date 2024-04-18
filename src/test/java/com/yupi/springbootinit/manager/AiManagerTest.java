package com.yupi.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AiManagerTest {

    @Resource
    private AiManager aiManager;

    @Test
    void doChat() {
        String result = aiManager.doChat(1671545080793600002L,"今天有点不开心宝宝");
        System.out.println(result);
    }

    @Test
    void xinghuo(){
        String result = aiManager.sendMesToAIUseXingHuo("请问你是什么模型？");
        System.out.println(result);
    }
}