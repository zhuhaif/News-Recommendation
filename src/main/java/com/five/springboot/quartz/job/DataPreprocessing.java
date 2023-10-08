package com.five.springboot.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 实现Job接口
 */
public class DataPreprocessing implements Job{

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        //System.out.println("开始数据预处理："+format.format(new Date()));
        //System.out.println("进行中...");
        // TODO 业务
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //System.out.println("结束数据预处理："+format.format(new Date()));
    }

}
