package art.yang.alarm.controller;

import art.yang.alarm.service.AlarmToWxService;
import cn.hutool.json.JSONUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author arTGOD
 * @Date 2024/10/12 17:21
 * @Description
 */
@RestController
@RequestMapping("/es-alert")
public class alarmController {
    @Resource
    private AlarmToWxService alarmToWxService;

    @PostMapping
    public void receiveAlert(@RequestBody String reqBody) {
        ArrayList<String> LBHealthCheckAlarmList = new ArrayList<>();
        if (reqBody != null) {
            String alarmData = JSONUtil.parseObj(reqBody).getStr("body");
            String[] alarms = alarmData.split("\n");
            LBHealthCheckAlarmList.addAll(Arrays.asList(alarms));
        }
        alarmToWxService.processLBHealthCheckAlarm(LBHealthCheckAlarmList);
    }
}
