package art.yang.alarm.service.impl;

import art.yang.alarm.entity.AlarmLog;
import art.yang.alarm.entity.LBHealthCheck;
import art.yang.alarm.mapper.AlarmLogMapper;
import art.yang.alarm.mapper.LBHealthCheckMapper;
import art.yang.alarm.service.AlarmToWxService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:30
 * @Description
 */
@Slf4j
@Service
public class AlarmToWxServiceImpl implements AlarmToWxService {
    private static final int ALARM_THRESHOLD = 3;
    private static final int TIME_WINDOW_MINUTES = 5;

    @Resource
    private AlarmLogMapper alarmLogMapper;

    @Resource
    private LBHealthCheckMapper lBHealthCheckMapper;


    public void processLBHealthCheckAlarm(List<String> LBHealthCheckAlarmList) {
        LocalDateTime now = LocalDateTime.now();

        for (String lbHealthCheckAlarm : LBHealthCheckAlarmList) {
            String[] alarmParts = lbHealthCheckAlarm.split("&");
            String pool = alarmParts[0].split(":")[1];
            String rsIp = alarmParts[1].split(":")[1];
            String status = alarmParts[2].split(":")[1];

            saveLBHealthCheck(pool, rsIp, status, now);

            if ("down".equals(status)) {
                processDownAlarm(pool, rsIp, now);
            } else if ("up".equals(status)) {
                boolean alarmStatus = checkAlarmStatus(pool, rsIp);
                if (alarmStatus) {
                    processRecoveryAlert(pool, rsIp);
                }
            }
        }
    }

    /**
     * 保存负载监控检查数据
     */
    private void saveLBHealthCheck(String pool, String rsIp, String status, LocalDateTime now) {
        LBHealthCheck lbHealthCheck = new LBHealthCheck();
        lbHealthCheck.setPool(pool);
        lbHealthCheck.setRsIp(rsIp);
        lbHealthCheck.setStatus(status);
        lbHealthCheck.setTimestamp(now);
        lBHealthCheckMapper.insert(lbHealthCheck);
    }

    /**
     * 处理 down 状态的告警
     */
    private void processDownAlarm(String pool, String rsIp, LocalDateTime now) {
        QueryWrapper<LBHealthCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pool", pool)
                .eq("rs_ip", rsIp)
                .eq("status", "down")
                .ge("timestamp", now.minusMinutes(TIME_WINDOW_MINUTES));

        List<LBHealthCheck> recentAlarms = lBHealthCheckMapper.selectList(queryWrapper);
        if (recentAlarms.size() >= ALARM_THRESHOLD) {
            AlarmLog alarmLog = new AlarmLog();
            alarmLog.setPool(pool);
            alarmLog.setRsIp(rsIp);
            alarmLog.setStatus("未恢复");
            alarmLog.setAlarmMessage(generateAlertMessage(pool,rsIp));
            alarmLog.setTimestamp(now);
            alarmLogMapper.insert(alarmLog);

            sendAlarm(alarmLog);
        }
    }

    private boolean checkAlarmStatus(String pool, String rsIp){
        LambdaQueryWrapper<AlarmLog> alarmLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        alarmLogLambdaQueryWrapper.eq(AlarmLog::getPool,pool)
                .eq(AlarmLog::getRsIp,rsIp)
                .orderByDesc(AlarmLog::getTimestamp)
                .last("LIMIT 1");
        List<AlarmLog> alarmLogs = alarmLogMapper.selectList(alarmLogLambdaQueryWrapper);
        if (alarmLogs.get(0).getStatus().equals("down")){
            return true;
        }
        return false;
    }

    private void processRecoveryAlert(String pool, String rsIp) {
        // 发送恢复告警
        String recoveryMessage = String.format("恢复告警: Pool：%s Member：%s 状态为 up", pool, rsIp);
        log.info(recoveryMessage);
        // TODO:发送告警
    }

    private String generateAlertMessage(String pool, String rsIp) {
        return String.format("告警: Pool：%s Member：%s 状态为 down", pool, rsIp);
    }


    private void sendAlarm(AlarmLog alarmLog) {
        log.info("发送告警: " + alarmLog.getAlarmMessage());
        // TODO:发送告警
    }
}
