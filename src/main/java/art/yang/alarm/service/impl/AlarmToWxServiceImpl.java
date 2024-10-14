package art.yang.alarm.service.impl;

import art.yang.alarm.config.AlarmConfig;
import art.yang.alarm.entity.AlarmLog;
import art.yang.alarm.entity.LBHealthCheck;
import art.yang.alarm.mapper.AlarmLogMapper;
import art.yang.alarm.mapper.LBHealthCheckMapper;
import art.yang.alarm.message.TextMessage;
import art.yang.alarm.service.AlarmToWxService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author arTGOD
 * @Date 2024/10/12 16:30
 * @Description
 */
@Slf4j
@Service
public class AlarmToWxServiceImpl implements AlarmToWxService {
    /*private static final int ALARM_THRESHOLD = 3;
    private static final int ALARM_THRESHOLD_TIME = 30;
    private static final int TIME_WINDOW_TIME = 5;*/

    @Resource
    private AlarmLogMapper alarmLogMapper;

    @Resource
    private LBHealthCheckMapper lBHealthCheckMapper;

    @Resource
    private WxWorkBotService wxWorkBotService;

    private final int ALARM_THRESHOLD;
    private final int ALARM_THRESHOLD_TIME;
    private final int ALARM_WINDOW_TIME;
    private final String webhook;

    public AlarmToWxServiceImpl(AlarmConfig alarmConfig) {
        this.ALARM_THRESHOLD = alarmConfig.getAlarmThreshold();
        this.ALARM_THRESHOLD_TIME = alarmConfig.getAlarmThresholdTime();
        this.ALARM_WINDOW_TIME = alarmConfig.getAlarmWindowTime();
        this.webhook = alarmConfig.getWebhook();
    }

    public void processLBHealthCheckAlarm(List<String> LBHealthCheckAlarmList) {
        LocalDateTime now = LocalDateTime.now();

        for (String lbHealthCheckAlarm : LBHealthCheckAlarmList) {
            String[] alarmParts = lbHealthCheckAlarm.split("&");
            String pool = alarmParts[0].split("=")[1];
            String rsIp = alarmParts[1].split("=")[1];
            String status = alarmParts[2].split("=")[1];

            saveLBHealthCheck(pool, rsIp, status, now);

            if ("down".equals(status)) {
                processDownAlarm(pool, rsIp, now);
            } else if ("up".equals(status)) {
                boolean alarmStatus = checkAlarmStatus(pool, rsIp);
                if (alarmStatus) {
                    processRecoveryAlert(pool, rsIp, now);
                }
            }
        }
    }


    private void saveLBHealthCheck(String pool, String rsIp, String status, LocalDateTime now) {
        LBHealthCheck lbHealthCheck = new LBHealthCheck();
        lbHealthCheck.setPool(pool);
        lbHealthCheck.setRsIp(rsIp);
        lbHealthCheck.setStatus(status);
        lbHealthCheck.setTimestamp(now);
        lBHealthCheckMapper.insert(lbHealthCheck);
    }

    /**
     * 处理 down 状态的告警,告警抑制
     */
    private void processDownAlarm(String pool, String rsIp, LocalDateTime now) {
        QueryWrapper<LBHealthCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pool", pool)
                .eq("rs_ip", rsIp)
                .ge("timestamp", now.minusMinutes(ALARM_WINDOW_TIME));
        List<LBHealthCheck> recentAlarms = lBHealthCheckMapper.selectList(queryWrapper);
        List<LBHealthCheck> sortedAlarms = recentAlarms.stream()
                .sorted(Comparator.comparing(LBHealthCheck::getTimestamp).reversed())
                .collect(Collectors.toList());

        int upIndex = IntStream.range(0, sortedAlarms.size())
                .filter(i -> "up".equals(sortedAlarms.get(i).getStatus()))
                .findFirst()
                .orElse(-1);

        if (upIndex >= ALARM_THRESHOLD) {
            String alarmMsg = String.format("告警: Pool：%s rs_ip：%s 状态为 down", pool, rsIp);
            if (!checkRepeatAlarm(pool, rsIp, now, alarmMsg)) {
                AlarmLog alarmLog = new AlarmLog();
                alarmLog.setPool(pool);
                alarmLog.setRsIp(rsIp);
                alarmLog.setStatus("未恢复");
                alarmLog.setAlarmMessage(alarmMsg);
                alarmLog.setTimestamp(now);
                alarmLogMapper.insert(alarmLog);
                TextMessage message = new TextMessage(alarmMsg);
                wxWorkBotService.send(message, webhook);
            }
        }
    }

    private boolean checkRepeatAlarm(String pool, String rsIp, LocalDateTime now, String alarmMsg) {
        LambdaQueryWrapper<AlarmLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AlarmLog::getPool, pool)
                .eq(AlarmLog::getRsIp, rsIp)
                .eq(AlarmLog::getAlarmMessage, alarmMsg)
                .eq(AlarmLog::getStatus, "未恢复")
                .ge(AlarmLog::getTimestamp, now.minusMinutes(ALARM_THRESHOLD_TIME));

        List<AlarmLog> recentAlarms = alarmLogMapper.selectList(queryWrapper);
        if (recentAlarms.isEmpty()) {
            return false;
        }

        for (AlarmLog alarm : recentAlarms) {
            LambdaQueryWrapper<AlarmLog> recoveryWrapper = new LambdaQueryWrapper<>();
            recoveryWrapper.eq(AlarmLog::getPool, pool)
                    .eq(AlarmLog::getRsIp, rsIp)
                    .eq(AlarmLog::getStatus, "已恢复")
                    .ge(AlarmLog::getTimestamp, alarm.getTimestamp()); // 恢复告警必须晚于告警

            Long recoveryCount = alarmLogMapper.selectCount(recoveryWrapper);

            if (recoveryCount == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean checkAlarmStatus(String pool, String rsIp) {
        LambdaQueryWrapper<AlarmLog> alarmLogLambdaQueryWrapper = new LambdaQueryWrapper<>();
        alarmLogLambdaQueryWrapper.eq(AlarmLog::getPool, pool)
                .eq(AlarmLog::getRsIp, rsIp)
                .orderByDesc(AlarmLog::getTimestamp)
                .last("LIMIT 1");
        List<AlarmLog> alarmLogs = alarmLogMapper.selectList(alarmLogLambdaQueryWrapper);
        if (!alarmLogs.isEmpty()) {
            return alarmLogs.get(0).getStatus().equals("未恢复");
        }
        return false;
    }

    private void processRecoveryAlert(String pool, String rsIp, LocalDateTime now) {
        String recoveryMessage = String.format("(已恢复)告警: Pool：%s rs_ip：%s 状态为 up", pool, rsIp);
        log.info(recoveryMessage);
        AlarmLog alarmLog = new AlarmLog();
        alarmLog.setPool(pool);
        alarmLog.setRsIp(rsIp);
        alarmLog.setStatus("已恢复");
        alarmLog.setAlarmMessage(recoveryMessage);
        alarmLog.setTimestamp(now);
        alarmLogMapper.insert(alarmLog);
        TextMessage message = new TextMessage(recoveryMessage);
        wxWorkBotService.send(message, webhook);
    }

}
