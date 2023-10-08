package com.five.springboot.service.impl;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewsSpiderCMD {
    private String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p;
        try {
            p = process.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();

        } catch (IOException e) {
            System.out.print("error");
            e.printStackTrace();
        }
        return null;
    }

    public void execCmd(String startTime) {
        //curl http://localhost:6800/schedule.json -d project=scrapyspider
        // -d spider=sohunews -d startTime=2020-7-20 21:25
        //String startTime = "2020-7-20-21-25-00";   // %Y-%m-%d-%H-%M-%S
        String[] cmds = {"curl", "http://localhost:6800/schedule.json",
                "-d", "project=scrapyspider", "-d", "spider=sohunews", "-d"
                , "startTime=",startTime};
        System.out.println(execCurl(cmds));
    }
}
