//package com.sky.task;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//
///**
// * @author mingbb
// * @create 2023-10-11-13:50
// */
//@Component
//@Slf4j
//public class MyTask {
//
//    /**
//     * 定时任务，每隔3秒输出
//     */
//    @Scheduled(cron = "0/3 * * * * ? ")
//    public void executeTask(){
//        log.info("定时任务开启：{}",new Date());
//    }
//}
