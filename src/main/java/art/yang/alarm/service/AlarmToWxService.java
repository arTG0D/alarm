package art.yang.alarm.service;

import java.util.List;

/**
 * @Author arTGOD
 * @Date 2024/10/12 17:22
 * @Description
 */
public interface AlarmToWxService {
    void processLBHealthCheckAlarm(List<String> LBHealthCheckAlarmList);
}
