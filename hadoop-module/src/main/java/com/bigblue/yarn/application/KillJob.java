package com.bigblue.yarn.application;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/3/15
 */
public class KillJob {

    private static YarnClient client;

    public static void main(String[] args) {
        testKillJob("");
    }

    private static void testKillJob(String oprtId) {
        Configuration conf = new Configuration();
        client = YarnClient.createYarnClient();
        client.init(conf);
        client.start();
        EnumSet<YarnApplicationState> appStates = EnumSet.noneOf(YarnApplicationState.class);
        if (appStates.isEmpty()) {
            appStates.add(YarnApplicationState.RUNNING);
//            appStates.add(YarnApplicationState.ACCEPTED);
//            appStates.add(YarnApplicationState.SUBMITTED);
        }
        try {
            //只获取正在运行的应用
            List<ApplicationReport> applications = client.getApplications(appStates);
            Stream<ApplicationReport> filteredApps = applications.stream().filter(app -> app.getName().equalsIgnoreCase(oprtId));
            filteredApps.forEach(app -> {
                try {
                    client.killApplication(app.getApplicationId());
                    System.out.println("Application " + app.getApplicationId() + " has already finished ");
                    //TODO 更新数据库
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
