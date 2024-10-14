package art.yang.alarm.controller;

import art.yang.alarm.service.AlarmToWxService;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * @Author arTGOD
 * @Date 2024/10/12 17:21
 * @Description
 */
@RestController
@RequestMapping("/lb-alert")
@Slf4j
public class alarmController {
    @Resource
    private AlarmToWxService alarmToWxService;

    @PostMapping
    public void receiveAlert(@RequestBody String reqBody) {
        log.info("reqBody:{}", reqBody);

        LinkedList<String> LBHealthCheckAlarmList = new LinkedList<>();
        String[] alarms = reqBody.split("\n");
        LBHealthCheckAlarmList.addAll(Arrays.asList(alarms));


        alarmToWxService.processLBHealthCheckAlarm(LBHealthCheckAlarmList);
    }
}
