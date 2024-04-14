package com.yupi.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 队列测试controller
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({"dev","local"}) // 只在开发环境和本地环境生效
public class QueueController {


    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @GetMapping("/add")
    // 接收一个参数name，然后将任务添加到线程池中
    public void add(String name){
    // 使用CompletableFuture运行一个异步任务
        CompletableFuture.runAsync(()->{

            log.info("任务执行中：" + name + "执行人：" + Thread.currentThread().getName());
            try {
                // 让线程休眠10分钟，模拟长时间运行的任务
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //// 异步任务在threadPoolExecutor中执行
        },threadPoolExecutor);
    }


    @GetMapping("/get")
    public String  get(){
        Map<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度",size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数",taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成的任务总数",completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数",activeCount);
        return JSONUtil.toJsonStr(map);
    }
}
